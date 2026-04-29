package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.*

@Composable
fun SettingsTab(viewModel: HomeViewModel, onLogout: () -> Unit) {
    val language by viewModel.savedLanguage.collectAsState()
    var expandedLang by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(CreamBackground).padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Configuración", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        SectionTitle("APP")
        Box {
            SettingsItem("Idioma", language, Icons.Default.Language) { expandedLang = true }
            DropdownMenu(expanded = expandedLang, onDismissRequest = { expandedLang = false }, modifier = Modifier.background(BeigeSurface)) {
                viewModel.languages.forEach { l ->
                    DropdownMenuItem(
                        text = { Text(l, color = ForestGreen) },
                        onClick = {
                            // CORRECCIÓN: Llamamos a updateAccountField
                            viewModel.updateAccountField("language", l)
                            expandedLang = false
                        }
                    )
                }
            }
        }

        SettingsItem("Resetear contraseña", "Enviar email", Icons.Default.Lock) { viewModel.sendPasswordReset() }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { viewModel.logout(); onLogout() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BeigeSurface)
        ) {
            Text("CERRAR SESIÓN", color = ForestGreen, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { viewModel.deleteAccount(onLogout) }, modifier = Modifier.fillMaxWidth()) {
            Text("Eliminar cuenta", color = Color.Red, fontWeight = FontWeight.SemiBold)
        }
    }
}