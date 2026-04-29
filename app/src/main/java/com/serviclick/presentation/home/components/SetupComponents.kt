package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSetupSection(viewModel: HomeViewModel) {
    val name by viewModel.setupName.collectAsState()
    val phone by viewModel.setupPhone.collectAsState()
    val phonePrefix by viewModel.setupPhonePrefix.collectAsState()
    val city by viewModel.selectedCity.collectAsState()
    val address by viewModel.setupAddress.collectAsState()

    var expandedCity by remember { mutableStateOf(false) }
    var expandedPrefix by remember { mutableStateOf(false) }

    // VALIDACIONES EN TIEMPO REAL
    val isNameValid = name.trim().isNotEmpty()
    val isPhoneValid = phone.replace(" ", "").length >= 9
    val isCityValid = city.isNotEmpty()
    val isAddressValid = address.trim().isNotEmpty()
    val isFormValid = isNameValid && isPhoneValid && isCityValid && isAddressValid

    Column(
        modifier = Modifier.fillMaxSize().background(CreamBackground).padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = SunsetOrange)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Completa tu perfil", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Text("Necesitamos algunos datos para empezar", color = ForestGreen.copy(0.6f))

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.onSetupNameChanged(it) },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            isError = !isNameValid,
            supportingText = { if (!isNameValid) Text("Este campo es obligatorio") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            ExposedDropdownMenuBox(
                expanded = expandedPrefix,
                onExpandedChange = { expandedPrefix = !expandedPrefix },
                modifier = Modifier.weight(0.4f)
            ) {
                OutlinedTextField(
                    value = phonePrefix,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrefix) },
                    modifier = Modifier.menuAnchor(),
                    supportingText = { if (!isPhoneValid) Text(" ") } // Espacio vacío para alinear con el teléfono
                )
                ExposedDropdownMenu(expanded = expandedPrefix, onDismissRequest = { expandedPrefix = false }) {
                    viewModel.phonePrefixes.forEach { p ->
                        DropdownMenuItem(text = { Text(p) }, onClick = { viewModel.onSetupPhonePrefixChanged(p); expandedPrefix = false })
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { viewModel.onSetupPhoneChanged(it) },
                label = { Text("Teléfono") },
                modifier = Modifier.weight(0.6f),
                isError = !isPhoneValid,
                supportingText = { if (!isPhoneValid) Text("Mínimo 9 dígitos") },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandedCity,
            onExpandedChange = { expandedCity = !expandedCity },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = city.ifEmpty { "Selecciona tu ciudad" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Ciudad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                isError = !isCityValid,
                supportingText = { if (!isCityValid) Text("La ciudad es obligatoria") }
            )
            ExposedDropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }) {
                viewModel.provinces.forEach { p ->
                    DropdownMenuItem(text = { Text(p) }, onClick = { viewModel.onCityChanged(p); expandedCity = false })
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { viewModel.onSetupAddressChanged(it) },
            label = { Text("Dirección exacta (Calle, número...)") },
            modifier = Modifier.fillMaxWidth(),
            isError = !isAddressValid,
            supportingText = { if (!isAddressValid) Text("La dirección es obligatoria") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveClientProfile() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = isFormValid, // BLOQUEO DEL BOTÓN
            colors = ButtonDefaults.buttonColors(
                containerColor = SunsetOrange,
                disabledContainerColor = SunsetOrange.copy(alpha = 0.4f),
                disabledContentColor = CreamBackground.copy(alpha = 0.7f)
            )
        ) {
            Text("GUARDAR Y CONTINUAR", fontWeight = FontWeight.Bold, color = CreamBackground)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySetupSection(viewModel: HomeViewModel) {
    val name by viewModel.setupName.collectAsState()
    val phone by viewModel.setupPhone.collectAsState()
    val phonePrefix by viewModel.setupPhonePrefix.collectAsState()
    val city by viewModel.selectedCity.collectAsState()
    val category by viewModel.selectedCategory.collectAsState()
    val description by viewModel.description.collectAsState()
    val address by viewModel.setupAddress.collectAsState()

    var expandedCity by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }
    var expandedPrefix by remember { mutableStateOf(false) }

    // VALIDACIONES EN TIEMPO REAL
    val isNameValid = name.trim().isNotEmpty()
    val isPhoneValid = phone.replace(" ", "").length >= 9
    val isCityValid = city.isNotEmpty()
    val isAddressValid = address.trim().isNotEmpty()
    val isCategoryValid = category.isNotEmpty()
    val isDescValid = description.trim().isNotEmpty()
    val isFormValid = isNameValid && isPhoneValid && isCityValid && isAddressValid && isCategoryValid && isDescValid

    Column(
        modifier = Modifier.fillMaxSize().background(CreamBackground).padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(64.dp), tint = SunsetOrange)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Perfil de Empresa", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Text("Configura tu escaparate comercial", color = ForestGreen.copy(0.6f))

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.onSetupCompanyNameChanged(it) },
            label = { Text("Nombre comercial") },
            modifier = Modifier.fillMaxWidth(),
            isError = !isNameValid,
            supportingText = { if (!isNameValid) Text("El nombre de la empresa es obligatorio") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            ExposedDropdownMenuBox(
                expanded = expandedPrefix,
                onExpandedChange = { expandedPrefix = !expandedPrefix },
                modifier = Modifier.weight(0.4f)
            ) {
                OutlinedTextField(
                    value = phonePrefix,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrefix) },
                    modifier = Modifier.menuAnchor(),
                    supportingText = { if (!isPhoneValid) Text(" ") }
                )
                ExposedDropdownMenu(expanded = expandedPrefix, onDismissRequest = { expandedPrefix = false }) {
                    viewModel.phonePrefixes.forEach { p ->
                        DropdownMenuItem(text = { Text(p) }, onClick = { viewModel.onSetupPhonePrefixChanged(p); expandedPrefix = false })
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { viewModel.onSetupPhoneChanged(it) },
                label = { Text("Teléfono") },
                modifier = Modifier.weight(0.6f),
                isError = !isPhoneValid,
                supportingText = { if (!isPhoneValid) Text("Mínimo 9 dígitos") },
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandedCity,
            onExpandedChange = { expandedCity = !expandedCity },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = city.ifEmpty { "Selecciona tu ciudad" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Ciudad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                isError = !isCityValid,
                supportingText = { if (!isCityValid) Text("La ciudad es obligatoria") }
            )
            ExposedDropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }) {
                viewModel.provinces.forEach { p ->
                    DropdownMenuItem(text = { Text(p) }, onClick = { viewModel.onCityChanged(p); expandedCity = false })
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { viewModel.onSetupAddressChanged(it) },
            label = { Text("Dirección exacta (Calle, número...)") },
            modifier = Modifier.fillMaxWidth(),
            isError = !isAddressValid,
            supportingText = { if (!isAddressValid) Text("La dirección es obligatoria") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandedCat,
            onExpandedChange = { expandedCat = !expandedCat },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = category.ifEmpty { "Selecciona tu categoría" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                isError = !isCategoryValid,
                supportingText = { if (!isCategoryValid) Text("Selecciona una categoría") }
            )
            ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                viewModel.categories.forEach { c ->
                    DropdownMenuItem(text = { Text(c) }, onClick = { viewModel.onCategoryChanged(c); expandedCat = false })
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.onDescriptionChanged(it) },
            label = { Text("Descripción de tus servicios") },
            modifier = Modifier.fillMaxWidth(),
            isError = !isDescValid,
            supportingText = { if (!isDescValid) Text("Escribe una breve descripción") },
            minLines = 3
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveCompanyProfile() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = isFormValid, // BLOQUEO DEL BOTÓN
            colors = ButtonDefaults.buttonColors(
                containerColor = SunsetOrange,
                disabledContainerColor = SunsetOrange.copy(alpha = 0.4f),
                disabledContentColor = CreamBackground.copy(alpha = 0.7f)
            )
        ) {
            Text("GUARDAR Y CONTINUAR", fontWeight = FontWeight.Bold, color = CreamBackground)
        }
    }
}