package com.marioonetti.pruebatecnicareact.ui.screen.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marioonetti.pruebatecnicareact.ui.shared.LoadingComposable

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainState,
    onEvent: (MainEvent) -> Unit = {}
) {
    when (state) {
        is MainState.Idle -> { MainBodyComposable(state, onEvent) }
        is MainState.Loading -> {
            LoadingComposable()
        }
    }
}

@Composable
fun MainBodyComposable(
    state: MainState.Idle,
    onEvent: (MainEvent) -> Unit = {}
) {

    val pickSingleMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        onEvent(MainEvent.OnGalleryImageSelected(uri))
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.showErrorPopUp) {
            ElevatedCard {
                Column {
                    Text(state.errorMessage ?: "Error")
                    Button(
                        onClick = {
                            onEvent(MainEvent.OnCloseError)
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            }
        } else {
            Row {
                state.capturedImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "captured Image",
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                state.galleryImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Gallery image",
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            state.percentage?.let {
                Text("El porcentaje de similitud es: $it")
            }

            Row {
                Button(
                    onClick = { onEvent(MainEvent.OnFaceCaptureClick) }
                ) {
                    Text("Capturar rostro")
                }

                Button(
                    onClick = {
                        val isAvailable =
                            ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)
                        if (isAvailable) {
                            pickSingleMedia.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    }
                ) {
                    Text("Seleccionar imagen")
                }
            }

            Button(
                onClick = { onEvent(MainEvent.OnCompareImagesClicked) }
            ) {
                Text("Comparar imagenes")
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(state = MainState.Idle())
}