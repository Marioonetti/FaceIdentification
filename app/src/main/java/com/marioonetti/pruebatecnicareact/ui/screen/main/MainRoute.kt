package com.marioonetti.pruebatecnicareact.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
) {
    val viewModel: MainViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    MainScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}