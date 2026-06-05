package com.serviclick.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Esquema de colores inyectado en los componentes de Material 3.
 */
private val ServiClickColorScheme = lightColorScheme(
    primary = SunsetOrange,
    onPrimary = CreamBackground,
    secondary = ForestGreen,
    background = CreamBackground,
    surface = BeigeSurface
)

/**
 * Tema principal de Jetpack Compose que envuelve toda la aplicación.
 * Aplica los colores, tipografías y configuración de la barra de estado del sistema.
 * Utiliza `SideEffect` para comunicarse con la API de Android clásica (Window) desde el entorno declarativo de Compose.
 */
@Composable
fun ServiClickTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CreamBackground.toArgb()
            // Como el fondo es claro, los iconos de la batería/hora deben ser oscuros (true)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = ServiClickColorScheme,
        typography = Typography,
        content = content
    )
}