package com.serviclick.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ServiClickColorScheme = lightColorScheme(
    primary = MintVibrant,      // Botones en Verde Menta
    onPrimary = MidnightBlue,   // Texto del botón en azul oscuro para contraste
    secondary = MidnightBlue,
    background = MidnightBlue,  // Fondo de la app por defecto
    surface = SoftWhite
)

@Composable
fun ServiClickTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MidnightBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = ServiClickColorScheme,
        typography = Typography,
        content = content
    )
}