package com.marioonetti.pruebatecnicareact.ui.screen.main.composables

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marioonetti.pruebatecnicareact.R

@Composable
fun ImageWithButtonComposable(
    modifier: Modifier = Modifier,
    image: ImageBitmap?,
    buttonLabel: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = "Image",
                    modifier = Modifier.height(150.dp),
                    contentScale = ContentScale.FillWidth
                )
            } else {
                Spacer(
                    modifier.weight(1f)
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                onClick = {
                    onButtonClick()
                }
            ) {
                Text(
                    text = buttonLabel
                )
            }
        }
    }
}

@Preview
@Composable
fun ImageWithButtonComposablePreview() {
    ImageWithButtonComposable(
        image = null,
        buttonLabel = "Click me",
        onButtonClick = {}
    )
}