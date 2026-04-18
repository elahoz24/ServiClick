package com.serviclick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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
                ServiClickApp()
            }
        }
    }
}

@Composable
fun ServiClickApp() {
    // El navController es el que ejecuta las órdenes de navegar
    val navController = rememberNavController()

    // NavHost define el mapa: pantalla inicial y destinos posibles
    NavHost(
        navController = navController,
        startDestination = LoginDestination
    ) {
        // Destino 1: Login
        composable<LoginDestination> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(RegisterDestination)
                }
            )
        }

        // Destino 2: Registro
        composable<RegisterDestination> {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}