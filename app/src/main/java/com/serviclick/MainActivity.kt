package com.serviclick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface // IMPORTANTE
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.serviclick.core.navigation.LoginDestination
import com.serviclick.core.navigation.RegisterDestination
import com.serviclick.presentation.auth.LoginScreen
import com.serviclick.presentation.auth.RegisterScreen
import com.serviclick.ui.theme.ServiClickTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServiClickTheme {
                // EL ARREGLO ESTÁ AQUÍ: Añadimos el Surface (El lienzo)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Aplica el fondo del Tema
                ) {
                    ServiClickApp()
                }
            }
        }
    }
}

@Composable
fun ServiClickApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginDestination
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(RegisterDestination)
                }
            )
        }
        composable<RegisterDestination> {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}