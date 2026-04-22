package com.serviclick.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val phonePrefix by viewModel.setupPhonePrefix.collectAsState()
    val phone by viewModel.setupPhone.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    var expandedCity by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("¡Bienvenido!", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = name, onValueChange = { viewModel.onSetupNameChanged(it) }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors())
        Spacer(modifier = Modifier.height(16.dp))
        PhoneInputField(phonePrefix, { viewModel.onSetupPhonePrefixChanged(it) }, phone, { viewModel.onSetupPhoneChanged(it) }, viewModel.phonePrefixes)
        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(expanded = expandedCity, onExpandedChange = { expandedCity = !expandedCity }) {
            OutlinedTextField(value = selectedCity, onValueChange = {}, readOnly = true, label = { Text("Tu Ciudad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCity) }, colors = getTextFieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }) {
                viewModel.provinces.forEach { city -> DropdownMenuItem(text = { Text(city) }, onClick = { viewModel.onCityChanged(city); expandedCity = false }) }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { viewModel.saveClientProfile() }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)) { Text("COMENZAR", fontWeight = FontWeight.Bold) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySetupSection(viewModel: HomeViewModel) {
    val companyName by viewModel.setupName.collectAsState()
    val phonePrefix by viewModel.setupPhonePrefix.collectAsState()
    val phone by viewModel.setupPhone.collectAsState()
    val description by viewModel.description.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var expandedCity by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Tu Negocio", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = companyName, onValueChange = { viewModel.onSetupCompanyNameChanged(it) }, label = { Text("Nombre de Empresa") }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors())
        Spacer(modifier = Modifier.height(16.dp))
        PhoneInputField(phonePrefix, { viewModel.onSetupPhonePrefixChanged(it) }, phone, { viewModel.onSetupPhoneChanged(it) }, viewModel.phonePrefixes)
        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(expanded = expandedCategory, onExpandedChange = { expandedCategory = !expandedCategory }) {
            OutlinedTextField(value = selectedCategory, onValueChange = {}, readOnly = true, label = { Text("Categoría") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) }, colors = getTextFieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                viewModel.categories.forEach { cat -> DropdownMenuItem(text = { Text(cat) }, onClick = { viewModel.onCategoryChanged(cat); expandedCategory = false }) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(expanded = expandedCity, onExpandedChange = { expandedCity = !expandedCity }) {
            OutlinedTextField(value = selectedCity, onValueChange = {}, readOnly = true, label = { Text("Ciudad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCity) }, colors = getTextFieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }) {
                viewModel.provinces.forEach { city -> DropdownMenuItem(text = { Text(city) }, onClick = { viewModel.onCityChanged(city); expandedCity = false }) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = description, onValueChange = { viewModel.onDescriptionChanged(it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth().height(120.dp), colors = getTextFieldColors())
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { viewModel.saveCompanyProfile() }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)) { Text("GUARDAR", fontWeight = FontWeight.Bold) }
    }
}