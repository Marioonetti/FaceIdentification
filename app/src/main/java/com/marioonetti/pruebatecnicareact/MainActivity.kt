package com.marioonetti.pruebatecnicareact

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.marioonetti.pruebatecnicareact.ui.screen.main.MainRoute
import com.marioonetti.pruebatecnicareact.ui.screen.main.MainScreen
import com.marioonetti.pruebatecnicareact.ui.theme.PruebaTecnicaReactTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PruebaTecnicaReactTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainRoute(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

