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
    val language by viewModel.savedLanguage.collectAsState()

    var showEditName by remember { mutableStateOf(false) }
    var showEditPhone by remember { mutableStateOf(false) }
    var showEditAddress by remember { mutableStateOf(false) }
    var expandedCity by remember { mutableStateOf(false) }
    var expandedLang by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(CreamBackground).padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Mi Cuenta", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Text(email, color = ForestGreen.copy(0.6f))
        Spacer(modifier = Modifier.height(32.dp))
        SectionTitle("DATOS PERSONALES")
        SettingsItem("Nombre", name, Icons.Default.AccountCircle) { showEditName = true }
        SettingsItem("Teléfono", phone, Icons.Default.Phone) { showEditPhone = true }
        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("DIRECCIÓN")
        Box {
            SettingsItem("Ciudad", city, Icons.Default.Search) { expandedCity = true }
            DropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }) {
                viewModel.provinces.forEach { p -> DropdownMenuItem(text = { Text(p) }, onClick = { viewModel.updateProfileField("city", p); expandedCity = false }) }
            }
        }
        SettingsItem("Calle y portal", address, Icons.Default.LocationOn) { showEditAddress = true }
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = { viewModel.logout(); onLogout() }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = BeigeSurface)) { Text("CERRAR SESIÓN", color = ForestGreen, fontWeight = FontWeight.Bold) }
    }
    if (showEditName) EditNameDialog("Editar Nombre", name, false, { showEditName = false }) { viewModel.updateProfileField("name", it) }
    if (showEditPhone) EditPhoneDialog(phone, viewModel.phonePrefixes, { showEditPhone = false }) { viewModel.updateProfileField("phone", it) }
    if (showEditAddress) EditAddressDialog(address, { showEditAddress = false }) { viewModel.updateProfileField("address", it) }
}