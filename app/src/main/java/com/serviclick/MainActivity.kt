package com.serviclick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.serviclick.core.navigation.HomeDestination
import com.serviclick.core.navigation.LoginDestination
import com.serviclick.core.navigation.RegisterDestination
import com.serviclick.presentation.auth.LoginScreen
import com.serviclick.presentation.auth.RegisterScreen
import com.serviclick.presentation.home.HomeScreen
import com.serviclick.ui.theme.ServiClickTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServiClickTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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

    // LA MAGIA DEL AUTO-LOGIN
    val startingScreen: Any = if (FirebaseAuth.getInstance().currentUser != null) {
        HomeDestination
    } else {
        LoginDestination
    }

    NavHost(
        navController = navController,
        startDestination = startingScreen
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(RegisterDestination)
                },
                onNavigateToHome = {
                    navController.navigate(HomeDestination) {
                        popUpTo(LoginDestination) { inclusive = true }
                    }
                }
            )
        }
        composable<RegisterDestination> {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                // --- ¡NUEVO! Le decimos cómo ir al Home ---
                onNavigateToHome = {
                    navController.navigate(HomeDestination) {
                        popUpTo(LoginDestination) { inclusive = true }
                    }
                }
            )
        }
        composable<HomeDestination> {
            HomeScreen(
                onLogout = {
                    navController.navigate(LoginDestination) {
                        popUpTo(HomeDestination) { inclusive = true }
                    }
                }
            )
        }
    }
}