package com.marioonetti.pruebatecnicareact.ui.screen.main

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marioonetti.pruebatecnicareact.R
import com.marioonetti.pruebatecnicareact.ui.screen.main.composables.ComparisonComposable
import com.marioonetti.pruebatecnicareact.ui.screen.main.composables.FooterComposable
import com.marioonetti.pruebatecnicareact.ui.screen.main.composables.ImageWithButtonComposable
import com.marioonetti.pruebatecnicareact.ui.shared.ErrorCardComposable
import com.marioonetti.pruebatecnicareact.ui.shared.FullScreenErrorComposable
import com.marioonetti.pruebatecnicareact.ui.shared.LoadingComposable

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainState,
    onEvent: (MainEvent) -> Unit = {}
) {
    when (state) {
        is MainState.Idle -> { MainBodyComposable(modifier, state, onEvent) }
        is MainState.Loading -> {
            LoadingComposable(stringResource(R.string.main_screen_full_loading_text))
        }
        is MainState.Error -> {
            FullScreenErrorComposable(
                state.message,
                { onEvent(MainEvent.OnRetryInitialization) }
            )
        }
    }
}

@Composable
fun MainBodyComposable(
    modifier: Modifier = Modifier,
    state: MainState.Idle,
    onEvent: (MainEvent) -> Unit = {}
) {

    val pickSingleMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        onEvent(MainEvent.OnGalleryImageSelected(uri))
    }

    val context = LocalContext.current

    val fadeInState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(R.string.main_screen_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            ImagesComposable(
                modifier = Modifier.weight(1f),
                state = state,
                onEvent = onEvent,
                pickSingleMedia = pickSingleMedia,
                context = context
            )

            FooterComposable(
                modifier = Modifier.weight(1f),
                onEvent = onEvent
            )

        }

        AnimatedVisibility(
            visibleState = fadeInState,
            enter = fadeIn(animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            if (state.showImageComparisonPopup) {
                ComparisonComposable(
                    percentage = state.percentage ?: 0.0,
                    loading = state.isComparisonLoading,
                    onClose = { onEvent(MainEvent.OnCloseComparisonPopUp) }
                )
            }
            if (state.showErrorPopUp) {
                ErrorCardComposable(
                    errorMessage = state.errorMessage ?: stringResource(R.string.default_error),
                    onClose = { onEvent(MainEvent.OnCloseError) }
                )
            }
        }
    }
}

@Composable
fun ImagesComposable(
    modifier: Modifier = Modifier,
    state: MainState.Idle,
    onEvent: (MainEvent) -> Unit = {},
    pickSingleMedia: ActivityResultLauncher<PickVisualMediaRequest>,
    context: Context
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ImageWithButtonComposable(
            image = state.capturedImage?.asImageBitmap(),
            buttonLabel = stringResource(R.string.capture_face_button),
            onButtonClick = { onEvent(MainEvent.OnFaceCaptureClick) },
            modifier = Modifier.weight(1f)
        )

        ImageWithButtonComposable(
            image = state.galleryImage?.asImageBitmap(),
            buttonLabel = stringResource(R.string.pick_image_from_galley_button),
            onButtonClick = {
                val isAvailable =
                    ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)
                if (isAvailable) {
                    pickSingleMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(state = MainState.Idle())
}