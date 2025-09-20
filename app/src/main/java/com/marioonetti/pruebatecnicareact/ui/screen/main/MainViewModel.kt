package com.marioonetti.pruebatecnicareact.ui.screen.main


import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Message
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marioonetti.pruebatecnicareact.ui.screen.RootViewModel
import com.marioonetti.pruebatecnicareact.ui.screen.ViewEvent
import com.marioonetti.pruebatecnicareact.ui.screen.ViewState
import com.regula.facesdk.FaceSDK
import com.regula.facesdk.callback.FaceInitializationCompletion
import com.regula.facesdk.configuration.FaceCaptureConfiguration
import com.regula.facesdk.configuration.InitializationConfiguration
import com.regula.facesdk.configuration.MatchFacesConfiguration
import com.regula.facesdk.enums.ImageType
import com.regula.facesdk.enums.ProcessingMode
import com.regula.facesdk.model.MatchFacesImage
import com.regula.facesdk.model.results.matchfaces.MatchFacesResponse
import com.regula.facesdk.request.MatchFacesRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val appContext: Context,
): RootViewModel<MainState, MainEvent>(MainState.Loading) {

    init {
        initSDK()
    }
    override fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnFaceCaptureClick -> {
                presentFaceCapture()
            }

            is MainEvent.OnSelectGalleryClick -> {
                presentFaceCapture()
            }

            is MainEvent.OnGalleryImageSelected -> {
                handleUri(event.uri)
            }

            is MainEvent.OnCompareImagesClicked -> {
                handleCompareImages()
            }

            is MainEvent.OnCloseError -> {
                closeError()
            }

            is MainEvent.OnCloseComparisonPopUp -> {
                onCloseComparisonPopUp()
            }

            is MainEvent.OnRetryInitialization -> {
                initSDK()
            }

            is MainEvent.OnResetImages -> {
                resetImages()
            }
        }
    }


    private fun presentFaceCapture() {

        val configuration = FaceCaptureConfiguration.Builder()
            .setCameraId(0)
            .setCameraSwitchEnabled(true)
            .build()

        FaceSDK.Instance().presentFaceCaptureActivity(appContext, configuration) { response ->
            response.exception?.let {
                val currentState = uiState.value as MainState.Idle
                updateState { currentState.copy(errorMessage = it.message, showErrorPopUp = true) }
            }

            val image = response.image
            val bitmap = image?.bitmap
            val currentState = uiState.value as MainState.Idle
            updateState {
                currentState.copy(capturedImage = bitmap)
            }
        }
    }

    private fun handleCompareImages() {
        val currentState = uiState.value as MainState.Idle
        if ( currentState.capturedImage != null && currentState.galleryImage != null) {
            compareImages(currentState, )
        } else {
            updateState { currentState.copy(showErrorPopUp = true, errorMessage = "Some of the images are missing to be able to compare") }
        }
    }

    private fun handleUri(uri: Uri?) {
        uri?.let {
            viewModelScope.launch {
                val bitmap = uriToBitmap(appContext, uri)
                val currentState = uiState.value as MainState.Idle
                updateState { currentState.copy(galleryImage = bitmap) }
            }
        }
    }

    private fun closeError() {
        val currentState = uiState.value as MainState.Idle
        updateState { currentState.copy(showErrorPopUp = false) }
    }

    private fun onCloseComparisonPopUp() {
        val currentState = uiState.value as MainState.Idle
        updateState { currentState.copy(showImageComparisonPopup = false, isComparisonLoading = true) }
    }

    private fun initSDK() {
        updateState { MainState.Loading }
        val license = appContext.assets.open("regula.license").use { it.readBytes() }
        val configuration = InitializationConfiguration.Builder(license).build()
        FaceSDK.Instance().initialize(
            appContext, configuration,
            FaceInitializationCompletion { status, exception ->
                if (exception != null ) {
                    updateState { MainState.Error(exception.message ?: "Something went wrong while initializing the SDK") }
                } else {
                    updateState { MainState.Idle() }
                }
            }
        )
    }

    private fun resetImages() {
        updateState { MainState.Idle() }
    }

    private fun compareImages(currentState: MainState.Idle) {
        updateState {
            currentState.copy(
                isComparisonLoading = true,
                showImageComparisonPopup = true
            )
        }
        val request = MatchFacesRequest(
            listOf(
                MatchFacesImage(currentState.capturedImage, ImageType.PRINTED),
                MatchFacesImage(currentState.galleryImage, ImageType.PRINTED),
            )
        )

        val configuration =
            MatchFacesConfiguration.Builder().setProcessingMode(ProcessingMode.OFFLINE).build()
        FaceSDK.Instance().matchFaces(
            appContext, request, configuration
        ) { response: MatchFacesResponse? ->
            if (response != null) {
                if (response.exception != null) {
                    updateState {
                        currentState.copy(
                            errorMessage = response.exception.message ?: "",
                            showErrorPopUp = true,
                            showImageComparisonPopup = false
                        )
                    }
                } else {
                    val percentage = response.results?.first()?.similarity
                    updateState {
                        currentState.copy(percentage = percentage, isComparisonLoading = false, showImageComparisonPopup = true)
                    }
                }
            } else {
                updateState {
                    currentState.copy(
                        errorMessage = "Empty response, try it later...",
                        showErrorPopUp = true
                    )
                }
            }
        }
    }

    private suspend fun uriToBitmap(context: Context, uri: Uri): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                val src = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(src)
            } catch (e: Exception) {
                null
            }
        }
}

sealed class MainState: ViewState() {
    data object Loading: MainState()

    data class Error(
        val message: String
    ): MainState()
    data class Idle(
        val capturedImage: Bitmap? = null,
        val galleryImage: Bitmap? = null,
        val percentage: Double? = null,
        val errorMessage: String? = null,
        val showErrorPopUp: Boolean = false,
        val showImageComparisonPopup: Boolean = false,
        val isComparisonLoading: Boolean = false,
    ): MainState()
}

sealed class MainEvent: ViewEvent() {
    data object OnFaceCaptureClick: MainEvent()
    data object OnSelectGalleryClick: MainEvent()
    data object OnCompareImagesClicked: MainEvent()
    data object OnCloseError: MainEvent()
    data object OnCloseComparisonPopUp: MainEvent()
    data object OnRetryInitialization: MainEvent()

    data object OnResetImages: MainEvent()
    data class OnGalleryImageSelected(
        val uri: Uri?
    ): MainEvent()

}