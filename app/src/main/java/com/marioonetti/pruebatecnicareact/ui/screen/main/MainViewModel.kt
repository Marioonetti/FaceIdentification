package com.marioonetti.pruebatecnicareact.ui.screen.main


import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marioonetti.pruebatecnicareact.ui.screen.RootViewModel
import com.marioonetti.pruebatecnicareact.ui.screen.ViewEvent
import com.marioonetti.pruebatecnicareact.ui.screen.ViewState
import com.regula.facesdk.FaceSDK
import com.regula.facesdk.configuration.FaceCaptureConfiguration
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
): RootViewModel<MainState, MainEvent>(MainState.Idle()) {


    override fun onEvent(event: MainEvent) {
        when(event) {
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
                compareImages()
            }

            is MainEvent.OnCloseError -> {
                closeError()
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

    private fun compareImages() {
        val currentState = uiState.value as MainState.Idle
        val request = MatchFacesRequest(
            listOf(
                MatchFacesImage(currentState.capturedImage, ImageType.PRINTED),
                MatchFacesImage(currentState.galleryImage, ImageType.PRINTED),
            )
        )

        val configuration =
            MatchFacesConfiguration.Builder().setProcessingMode(ProcessingMode. OFFLINE).build()
        FaceSDK.Instance().matchFaces(
            appContext, request, configuration
        ) { response: MatchFacesResponse? ->
            if (response != null) {
                if (response.exception != null) {
                    updateState { currentState.copy(errorMessage = response.exception.message ?: "", showErrorPopUp = true) }
                } else {
                    val percentage = response.results?.first()?.similarity
                    updateState {
                        currentState.copy(percentage = percentage)
                    }
                }
            } else {
                updateState { currentState.copy(errorMessage = "Empty response, try it later...", showErrorPopUp = true) }
            }
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

    private suspend fun uriToBitmap(context: Context, uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val src = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(src)
        } catch (e: Exception) { null }
    }


}

sealed class MainState: ViewState() {
    data object Loading: MainState()
    data class Idle(
        val capturedImage: Bitmap? = null,
        val galleryImage: Bitmap? = null,
        val percentage: Double? = null,
        val errorMessage: String? = null,
        val showErrorPopUp: Boolean = false,
    ): MainState()
}

sealed class MainEvent: ViewEvent() {
    data object OnFaceCaptureClick: MainEvent()
    data object OnSelectGalleryClick: MainEvent()
    data object OnCompareImagesClicked: MainEvent()
    data object OnCloseError: MainEvent()
    data class OnGalleryImageSelected(
        val uri: Uri?
    ): MainEvent()

}