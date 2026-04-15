package com.serviclick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.serviclick.presentation.auth.LoginScreen
import com.serviclick.ui.theme.ServiClickTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Este es el punto de entrada de Jetpack Compose
            ServiClickTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "¡Hola Compose y Firebase!")
                    }
                }
            }
        }
    }

    @Composable
    fun MiBotonPersonalizado() {
        Button(onClick = { /* Hacer algo */ }) {
            Text("Púlsame")
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun PreviewComponents(){
        ServiClickTheme() {
            LoginScreen()
        }

    }
}

