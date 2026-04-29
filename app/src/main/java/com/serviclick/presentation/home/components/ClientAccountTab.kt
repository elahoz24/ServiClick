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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.*

@Composable
fun ClientAccountTab(viewModel: HomeViewModel, onLogout: () -> Unit) {
    val name by viewModel.userName.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val phone by viewModel.userPhone.collectAsState()
    val city by viewModel.savedCity.collectAsState()
    val address by viewModel.savedAddress.collectAsState()

    var showEditName by remember { mutableStateOf(false) }
    var showEditPhone by remember { mutableStateOf(false) }
    var showEditAddress by remember { mutableStateOf(false) }
    var expandedCity by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(CreamBackground).padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Mi Cuenta", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Text(email, color = ForestGreen.copy(0.6f))

        Spacer(modifier = Modifier.height(32.dp))

        SectionTitle("DATOS PERSONALES")
        SettingsItem("Nombre completo", name.ifEmpty { "Configurar" }, Icons.Default.AccountCircle) { showEditName = true }
        SettingsItem("Teléfono móvil", phone.ifEmpty { "Configurar" }, Icons.Default.Phone) { showEditPhone = true }

        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("DIRECCIÓN Y PAGOS")
        Box {
            SettingsItem("Ciudad (Para búsquedas)", city.ifEmpty { "Configurar" }, Icons.Default.Search) { expandedCity = true }
            DropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }, modifier = Modifier.background(BeigeSurface)) {
                viewModel.provinces.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p, color = ForestGreen) },
                        onClick = {
                            // AHORA LLAMA A LA FUNCIÓN CORRECTA
                            viewModel.updateAccountField("city", p)
                            expandedCity = false
                        }
                    )
                }
            }
        }
        SettingsItem("Dirección exacta", address.ifEmpty { "Añadir calle, portal, piso..." }, Icons.Default.LocationOn) { showEditAddress = true }

        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = { viewModel.logout(); onLogout() }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = BeigeSurface)) {
            Text("CERRAR SESIÓN", color = ForestGreen, fontWeight = FontWeight.Bold)
        }
    }

    // CORREGIDO: Llaman a updateAccountField en lugar del antiguo updateProfileField
    if (showEditName) EditNameDialog("Editar Nombre", name, false, { showEditName = false }) { viewModel.updateAccountField("name", it) }
    if (showEditPhone) EditPhoneDialog(phone, viewModel.phonePrefixes, { showEditPhone = false }) { viewModel.updateAccountField("phone", it) }
    if (showEditAddress) EditAddressDialog(address, { showEditAddress = false }) { viewModel.updateAccountField("address", it) }
}