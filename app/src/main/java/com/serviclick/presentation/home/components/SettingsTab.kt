package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.BeigeSurface
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ForestGreen

/**
 * Pestaña de Ajustes generales para el perfil Empresa (y aplicable al cliente).
 * Aloja preferencias genéricas de la app (idioma) y acciones destructivas (logout, borrar cuenta).
 */
@Composable
fun SettingsTab(viewModel: HomeViewModel, onLogout: () -> Unit) {
    val language by viewModel.savedLanguage.collectAsState()
    var expandedLang by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        SectionTitle("APP")
        Box {
            SettingsItem("Idioma", language, Icons.Default.Language) { expandedLang = true }
            DropdownMenu(
                expanded = expandedLang,
                onDismissRequest = { expandedLang = false },
                modifier = Modifier.background(BeigeSurface)
            ) {
                viewModel.languages.forEach { l ->
                    DropdownMenuItem(
                        text = { Text(l, color = ForestGreen) },
                        onClick = {
                            viewModel.updateAccountField("language", l); expandedLang = false
                        })
                }
            }
        }

        // Delega la acción de resetear contraseña enviando el email asociado a la sesión de Firebase Auth
        SettingsItem(
            "Resetear contraseña",
            "Enviar email",
            Icons.Default.Lock
        ) { viewModel.sendPasswordReset() }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { viewModel.logout(); onLogout() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BeigeSurface)
        ) {
            Text("CERRAR SESIÓN", color = ForestGreen, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Acción crítica destructiva (Delete Account en cascada desde Firestore hasta Auth)
        TextButton(
            onClick = { viewModel.deleteAccount(onLogout) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Eliminar cuenta", color = Color.Red, fontWeight = FontWeight.SemiBold)
        }
    }
}