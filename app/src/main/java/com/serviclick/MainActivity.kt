package com.serviclick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.serviclick.core.navigation.CompanyDetailDestination
import com.serviclick.core.navigation.HomeDestination
import com.serviclick.core.navigation.LoginDestination
import com.serviclick.core.navigation.RegisterDestination
import com.serviclick.presentation.auth.LoginScreen
import com.serviclick.presentation.auth.RegisterScreen
import com.serviclick.presentation.company_detail.CompanyDetailScreen
import com.serviclick.presentation.home.HomeScreen
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ServiClickTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

/**
 * Activity principal y único de la aplicación.
 * Levanta el entorno de Jetpack Compose, controlar el Splash Screen y definir el Grafo de Navegación.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permite que la app dibuje detrás de las barras del sistema (navegación y estado) para un look moderno
        enableEdgeToEdge()

        setContent {
            ServiClickTheme {
                // Estado que controla si se muestra la pantalla de carga (Splash) o la app real
                var showSplash by remember { mutableStateOf(true) }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding() // Respeta los márgenes de las barras del sistema
                        .imePadding(),       // Sube la pantalla dinámicamente si aparece el teclado virtual
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSplash) {
                        SplashScreen { showSplash = false }
                    } else {
                        ServiClickApp()
                    }
                }
            }
        }
    }
}

/**
 * Pantalla de presentación simulada (Splash Screen).
 * Utiliza `LaunchedEffect(Unit)` para ejecutar una corrutina que espera 2 segundos (`delay`)
 * y luego invoca el callback `onTimeout` para avisar al MainActivity de que ya puede mostrar la navegación.
 */
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Muestra el logo durante 2 segundos
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_sin_texto),
            contentDescription = "Logo",
            modifier = Modifier.size(250.dp)
        )
    }
}

/**
 * Grafo de Navegación de la Aplicación.
 * Enruta al usuario a la pantalla adecuada asegurando el Type-Safety.
 * Revisa si `FirebaseAuth` tiene un usuario activo para saltarse el Login.
 * Configura los destinos pasando los callbacks de navegación a cada pantalla, desacoplando
 * la navegación de la propia UI de la pantalla.
 */
@Composable
fun ServiClickApp() {
    val navController = rememberNavController()

    // Lógica de enrutamiento inicial dinámico
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
                        // Limpia el historial para que no se pueda volver al Login dándole a "Atrás"
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
                onNavigateToHome = {
                    navController.navigate(HomeDestination) {
                        popUpTo(LoginDestination) { inclusive = true }
                    }
                }
            )
        }
        composable<HomeDestination> {
            HomeScreen(
                onNavigateToCompany = { companyId ->
                    navController.navigate(CompanyDetailDestination(companyId))
                },
                onLogout = {
                    navController.navigate(LoginDestination) {
                        popUpTo(HomeDestination) { inclusive = true }
                    }
                }
            )
        }

        // Navegación que recibe argumentos (el ID de la empresa)
        composable<CompanyDetailDestination> {
            CompanyDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}