package com.marioonetti.pruebatecnicareact.ui.screen.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marioonetti.pruebatecnicareact.R
import com.marioonetti.pruebatecnicareact.ui.screen.main.MainEvent

@Composable
fun FooterComposable(
    modifier: Modifier = Modifier,
    onEvent: (MainEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Button(
            modifier = Modifier.fillMaxWidth().weight(1f),
            onClick = { onEvent(MainEvent.OnResetImages) }
        ) {
            Text(
                text = stringResource(R.string.reset_images_button)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth().weight(1f),
            onClick = { onEvent(MainEvent.OnCompareImagesClicked) }
        ) {
            Text(
                text = stringResource(R.string.compare_images_button)
            )
        }
    }
    Spacer(modifier = modifier)
}

@Preview
@Composable
fun FooterComposablePreview() {
    FooterComposable(onEvent = {})
}