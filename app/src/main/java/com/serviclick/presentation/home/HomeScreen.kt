package com.serviclick.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serviclick.ui.theme.MidnightBlue
import com.serviclick.ui.theme.MintVibrant

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBlue),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            HomeState.LOADING -> {
                CircularProgressIndicator(color = MintVibrant, modifier = Modifier.size(60.dp))
            }
            HomeState.NEEDS_CITY -> {
                CitySelectionSection(viewModel)
            }
            HomeState.NEEDS_COMPANY_INFO -> {
                CompanySetupSection(viewModel)
            }
            HomeState.DASHBOARD -> {
                DashboardSection(viewModel, onLogout)
            }
        }
    }
}

// --- SUB-PANTALLA: CONFIGURACIÓN DE EMPRESA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySetupSection(viewModel: HomeViewModel) {
    val companyName by viewModel.companyName.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val description by viewModel.description.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var expandedCity by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val textFieldColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
        focusedBorderColor = MintVibrant,
        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = MintVibrant,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = MintVibrant
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Configura tu Negocio",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Crea el escaparate perfecto para atraer clientes.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = companyName,
            onValueChange = { viewModel.onCompanyNameChanged(it) },
            label = { Text("Nombre de la Empresa / Profesional") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { viewModel.onPhoneChanged(it) },
            label = { Text("Teléfono de Contacto") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Desplegable de Categoría
        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = !expandedCategory }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = { },
                readOnly = true,
                label = { Text("Categoría Profesional") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                colors = textFieldColors,
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false },
                modifier = Modifier.background(Color.White)
            ) {
                viewModel.categories.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = MidnightBlue) },
                        onClick = {
                            viewModel.onCategoryChanged(option)
                            expandedCategory = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Desplegable de Provincia
        ExposedDropdownMenuBox(
            expanded = expandedCity,
            onExpandedChange = { expandedCity = !expandedCity }
        ) {
            OutlinedTextField(
                value = selectedCity,
                onValueChange = { },
                readOnly = true,
                label = { Text("Provincia de Trabajo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity) },
                colors = textFieldColors,
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedCity,
                onDismissRequest = { expandedCity = false },
                modifier = Modifier.background(Color.White)
            ) {
                viewModel.provinces.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = MidnightBlue) },
                        onClick = {
                            viewModel.onCityChanged(option)
                            expandedCity = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.onDescriptionChanged(it) },
            label = { Text("Descripción (Tus servicios, experiencia...)") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = textFieldColors,
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveCompanyProfile() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = companyName.isNotEmpty() && selectedCity.isNotEmpty() && selectedCategory.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MintVibrant,
                disabledContainerColor = MintVibrant.copy(alpha = 0.3f)
            )
        ) {
            Text("GUARDAR Y ENTRAR", fontWeight = FontWeight.ExtraBold, color = MidnightBlue)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- SUB-PANTALLA: CONFIGURACIÓN DE CLIENTE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectionSection(viewModel: HomeViewModel) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val textFieldColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
        focusedBorderColor = MintVibrant,
        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = MintVibrant,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = MintVibrant
    )

    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Bienvenido a ServiClick!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Para mostrarte a los mejores profesionales cerca de ti, selecciona tu provincia.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCity,
                onValueChange = { },
                readOnly = true,
                label = { Text("Provincia") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = textFieldColors,
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                viewModel.provinces.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption, color = MidnightBlue) },
                        onClick = {
                            viewModel.onCityChanged(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.saveClientCity() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = selectedCity.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MintVibrant,
                disabledContainerColor = MintVibrant.copy(alpha = 0.3f)
            )
        ) {
            Text("COMENZAR", fontWeight = FontWeight.ExtraBold, color = MidnightBlue)
        }
    }
}

// --- SUB-PANTALLA: EL DASHBOARD PRINCIPAL ---
@Composable
fun DashboardSection(viewModel: HomeViewModel, onLogout: () -> Unit) {
    val role by viewModel.userRole.collectAsState()
    val savedCity by viewModel.savedCity.collectAsState()

    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Panel de $role",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sede / Ubicación: $savedCity",
            style = MaterialTheme.typography.bodyLarge,
            color = MintVibrant,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MintVibrant)
        ) {
            Text("Cerrar Sesión", color = MidnightBlue, fontWeight = FontWeight.Bold)
        }
    }
}