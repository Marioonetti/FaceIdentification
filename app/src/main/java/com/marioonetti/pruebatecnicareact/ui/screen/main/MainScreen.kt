package com.marioonetti.pruebatecnicareact.ui.screen.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
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
        is MainState.Error -> {
            Column(modifier = modifier) {}
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
        state.image?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Selfie",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Crop
            )
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

    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(state = MainState.Idle())
}