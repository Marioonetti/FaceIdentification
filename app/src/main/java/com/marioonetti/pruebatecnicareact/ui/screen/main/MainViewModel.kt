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
                event.uri?.let {
                    viewModelScope.launch {
                        val bitmap = uriToBitmap(appContext, event.uri)
                        updateState { MainState.Idle(image = bitmap) }
                    }
                }
            }
        }
    }


    fun presentFaceCapture() {

        val configuration = FaceCaptureConfiguration.Builder()
            .setCameraId(0)
            .setCameraSwitchEnabled(true)
            .build()

        FaceSDK.Instance().presentFaceCaptureActivity(appContext, configuration) { response ->
            response.exception?.let {
                updateState { MainState.Error(it.message ?: "") }
            }

            val image = response.image
            val bitmap = image?.bitmap

            updateState {
                MainState.Idle(image = bitmap)
            }
        }
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
        val image: Bitmap? = null,
        val galleryUri: Uri? = null
    ): MainState()
    data class Error(val message: String): MainState()
}

sealed class MainEvent: ViewEvent() {
    data object OnFaceCaptureClick: MainEvent()
    data object OnSelectGalleryClick: MainEvent()

    data class OnGalleryImageSelected(
        val uri: Uri?
    ): MainEvent()

}