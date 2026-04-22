package com.serviclick.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ServiClickColorScheme = lightColorScheme(
    primary = SunsetOrange,
    onPrimary = CreamBackground,
    secondary = ForestGreen,
    background = CreamBackground,
    surface = BeigeSurface
)

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