package com.marioonetti.pruebatecnicareact.ui.screen.main.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marioonetti.pruebatecnicareact.R
import java.text.DecimalFormat


@Composable
fun ComparisonComposable(
    percentage: Double,
    loading: Boolean,
    onClose: () -> Unit,
) {
    val percentageColor = if (percentage >= 0.8) Color.Green else Color.Red
    val percentageFormat = DecimalFormat("0.00")
    val cardTitle = if (loading) stringResource(R.string.images_comparison_popup_loading_title)
    else stringResource(R.string.images_comparison_popup_title)

    Column (
        modifier = Modifier.fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = cardTitle,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    Text(
                        text = "${percentageFormat.format(percentage)} %",
                        style = MaterialTheme.typography.headlineLarge,
                        color = percentageColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { onClose() }) {
                        Text(text = stringResource(R.string.images_comparison_popup_button))
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun ComparisonComposablePreview() {
    ComparisonComposable(
        percentage = 0.82,
        loading = false,
        onClose = {}
    )
}