package com.serviclick.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serviclick.ui.theme.*

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(), onLogout: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearErrorMessage() },
            title = { Text("Aviso", color = ForestGreen, fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage!!, color = ForestGreen) },
            confirmButton = { TextButton(onClick = { viewModel.clearErrorMessage() }) { Text("OK", color = SunsetOrange, fontWeight = FontWeight.Bold) } },
            containerColor = BeigeSurface
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(CreamBackground), contentAlignment = Alignment.Center) {
        when (uiState) {
            HomeState.LOADING -> CircularProgressIndicator(color = SunsetOrange, modifier = Modifier.size(60.dp))
            HomeState.NEEDS_CLIENT_INFO -> ClientSetupSection(viewModel)
            HomeState.NEEDS_COMPANY_INFO -> CompanySetupSection(viewModel)
            HomeState.DASHBOARD -> DashboardSection(viewModel, onLogout)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getTextFieldColors() = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
    focusedBorderColor = SunsetOrange, unfocusedBorderColor = ForestGreen.copy(alpha = 0.3f),
    focusedTextColor = ForestGreen, unfocusedTextColor = ForestGreen,
    focusedLabelColor = SunsetOrange, unfocusedLabelColor = ForestGreen.copy(alpha = 0.6f),
    cursorColor = SunsetOrange, errorBorderColor = Color(0xFFFF5252)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneInputField(
    prefixValue: String, onPrefixChange: (String) -> Unit,
    phoneValue: String, onPhoneChange: (String) -> Unit,
    prefixes: List<String>
) {
    var expandedPrefix by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        ExposedDropdownMenuBox(
            expanded = expandedPrefix, onExpandedChange = { expandedPrefix = !expandedPrefix },
            modifier = Modifier.weight(0.35f)
        ) {
            OutlinedTextField(
                value = prefixValue.substringBefore(" "),
                onValueChange = { }, readOnly = true, label = { Text("Prefijo") },
                colors = getTextFieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth(), singleLine = true
            )
            ExposedDropdownMenu(expanded = expandedPrefix, onDismissRequest = { expandedPrefix = false }, modifier = Modifier.background(BeigeSurface)) {
                prefixes.forEach { option ->
                    DropdownMenuItem(text = { Text(option, color = ForestGreen) }, onClick = { onPrefixChange(option); expandedPrefix = false })
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = phoneValue, onValueChange = onPhoneChange,
            label = { Text("Teléfono") }, modifier = Modifier.weight(0.65f), colors = getTextFieldColors(),
            singleLine = true, isError = phoneValue.isNotEmpty() && phoneValue.length < 9,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSetupSection(viewModel: HomeViewModel) {
    val name by viewModel.setupName.collectAsState()
    val phonePrefix by viewModel.setupPhonePrefix.collectAsState()
    val phone by viewModel.setupPhone.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    var expandedCity by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("¡Bienvenido a ServiClick!", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name, onValueChange = { viewModel.onSetupNameChanged(it) },
            label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words)
        )
        Spacer(modifier = Modifier.height(16.dp))
        PhoneInputField(phonePrefix, { viewModel.onSetupPhonePrefixChanged(it) }, phone, { viewModel.onSetupPhoneChanged(it) }, viewModel.phonePrefixes)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expandedCity, onExpandedChange = { expandedCity = !expandedCity }) {
            OutlinedTextField(value = selectedCity, onValueChange = { }, readOnly = true, label = { Text("Tu Ciudad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity) }, colors = getTextFieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }, modifier = Modifier.background(BeigeSurface)) {
                viewModel.provinces.forEach { option -> DropdownMenuItem(text = { Text(option, color = ForestGreen) }, onClick = { viewModel.onCityChanged(option); expandedCity = false }) }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveClientProfile() }, modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = name.length >= 3 && phone.length >= 9 && selectedCity.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange, disabledContainerColor = SunsetOrange.copy(alpha = 0.3f))
        ) { Text("COMENZAR", fontWeight = FontWeight.ExtraBold, color = CreamBackground) }
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
        Spacer(modifier = Modifier.height(24.dp))
        Text("Configura tu Negocio", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = companyName, onValueChange = { viewModel.onSetupCompanyNameChanged(it) },
            label = { Text("Nombre de la Empresa") }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words)
        )
        Spacer(modifier = Modifier.height(16.dp))
        PhoneInputField(phonePrefix, { viewModel.onSetupPhonePrefixChanged(it) }, phone, { viewModel.onSetupPhoneChanged(it) }, viewModel.phonePrefixes)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expandedCategory, onExpandedChange = { expandedCategory = !expandedCategory }) {
            OutlinedTextField(value = selectedCategory, onValueChange = { }, readOnly = true, label = { Text("Categoría Profesional") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) }, colors = getTextFieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }, modifier = Modifier.background(BeigeSurface)) {
                viewModel.categories.forEach { option -> DropdownMenuItem(text = { Text(option, color = ForestGreen) }, onClick = { viewModel.onCategoryChanged(option); expandedCategory = false }) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expandedCity, onExpandedChange = { expandedCity = !expandedCity }) {
            OutlinedTextField(value = selectedCity, onValueChange = { }, readOnly = true, label = { Text("Provincia de Trabajo") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity) }, colors = getTextFieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }, modifier = Modifier.background(BeigeSurface)) {
                viewModel.provinces.forEach { option -> DropdownMenuItem(text = { Text(option, color = ForestGreen) }, onClick = { viewModel.onCityChanged(option); expandedCity = false }) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description, onValueChange = { viewModel.onDescriptionChanged(it) },
            label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth().height(120.dp), colors = getTextFieldColors(), maxLines = 5,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences)
        )
        Text(text = "${description.length}/300", color = ForestGreen.copy(0.5f), style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.End))

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveCompanyProfile() }, modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = companyName.length >= 3 && phone.length >= 9 && selectedCity.isNotEmpty() && selectedCategory.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange, disabledContainerColor = SunsetOrange.copy(alpha = 0.3f))
        ) { Text("GUARDAR Y ENTRAR", fontWeight = FontWeight.ExtraBold, color = CreamBackground) }
    }
}

data class BottomNavItem(val title: String, val icon: ImageVector)

@Composable
fun DashboardSection(viewModel: HomeViewModel, onLogout: () -> Unit) {
    val role by viewModel.userRole.collectAsState()
    var currentTab by remember { mutableStateOf(0) }

    val tabs = if (role == "cliente") {
        listOf(BottomNavItem("Inicio", Icons.Default.Home), BottomNavItem("Citas", Icons.Default.DateRange), BottomNavItem("Mensajes", Icons.Default.Email), BottomNavItem("Perfil", Icons.Default.Person))
    } else {
        listOf(BottomNavItem("Solicitudes", Icons.Default.List), BottomNavItem("Agenda", Icons.Default.DateRange), BottomNavItem("Mensajes", Icons.Default.Email), BottomNavItem("Mi Negocio", Icons.Default.Person))
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = BeigeSurface, contentColor = ForestGreen) {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = currentTab == index, onClick = { currentTab = index },
                        icon = { Icon(item.icon, contentDescription = null) }, label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CreamBackground,
                            selectedTextColor = ForestGreen,
                            indicatorColor = SunsetOrange,
                            unselectedIconColor = ForestGreen.copy(alpha = 0.5f),
                            unselectedTextColor = ForestGreen.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(CreamBackground).padding(paddingValues), contentAlignment = Alignment.Center) {
            when (currentTab) {
                0 -> PlaceholderScreen("Pantalla de ${tabs[0].title}", "Aquí programaremos la pantalla de inicio.")
                1 -> PlaceholderScreen("Pantalla de ${tabs[1].title}", "Aquí irá el calendario.")
                2 -> PlaceholderScreen("Pantalla de ${tabs[2].title}", "Aquí irá el chat.")
                3 -> ProfileTab(viewModel, onLogout)
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodyLarge, color = SunsetOrange, textAlign = TextAlign.Center)
    }
}

@Composable
fun ProfileTab(viewModel: HomeViewModel, onLogout: () -> Unit) {
    val email by viewModel.userEmail.collectAsState()
    val role by viewModel.userRole.collectAsState()
    val name by viewModel.userName.collectAsState()
    val phone by viewModel.userPhone.collectAsState()
    val city by viewModel.savedCity.collectAsState()
    val language by viewModel.savedLanguage.collectAsState()

    // Campos de empresa
    val category by viewModel.savedCategory.collectAsState()
    val description by viewModel.savedDescription.collectAsState()

    var showEditName by remember { mutableStateOf(false) }
    var showEditPhone by remember { mutableStateOf(false) }
    var showEditDescription by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    var expandedCity by remember { mutableStateOf(false) }
    var expandedLang by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Surface(modifier = Modifier.size(90.dp), shape = CircleShape, color = SunsetOrange.copy(alpha = 0.2f)) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(20.dp), tint = SunsetOrange)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = if (name.isEmpty()) "Usuario ServiClick" else name, color = ForestGreen, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = email, color = ForestGreen.copy(alpha = 0.6f), style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(32.dp))

        SectionTitle("DATOS PERSONALES")
        SettingsItem(title = if(role == "cliente") "Nombre completo" else "Nombre comercial", value = name.ifEmpty { "Toca para configurar" }, icon = Icons.Default.AccountCircle) { showEditName = true }
        SettingsItem(title = "Teléfono móvil", value = phone.ifEmpty { "Toca para configurar" }, icon = Icons.Default.Phone) { showEditPhone = true }

        Box {
            SettingsItem(title = "Ubicación / Ciudad", value = city.ifEmpty { "No configurada" }, icon = Icons.Default.LocationOn) { expandedCity = true }
            DropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }, modifier = Modifier.background(BeigeSurface)) {
                viewModel.provinces.forEach { p -> DropdownMenuItem(text = { Text(p, color = ForestGreen) }, onClick = { viewModel.updateProfileField("city", p); expandedCity = false }) }
            }
        }

        // --- SECCIÓN EXCLUSIVA PARA EMPRESAS ---
        if (role == "empresa") {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("ESCAPARATE COMERCIAL")
            Box {
                SettingsItem(title = "Categoría Profesional", value = category.ifEmpty { "Sin categoría" }, icon = Icons.Default.Build) { expandedCategory = true }
                DropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }, modifier = Modifier.background(BeigeSurface)) {
                    viewModel.categories.forEach { c ->
                        DropdownMenuItem(text = { Text(c, color = ForestGreen) }, onClick = {
                            viewModel.updateProfileField("category", c)
                            expandedCategory = false
                        })
                    }
                }
            }
            SettingsItem(
                title = "Descripción",
                value = if (description.length > 25) description.take(25) + "..." else description.ifEmpty { "Añadir descripción" },
                icon = Icons.Default.Edit
            ) { showEditDescription = true }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("SEGURIDAD Y AJUSTES")
        Box {
            SettingsItem(title = "Idioma", value = language, icon = Icons.Default.Info) { expandedLang = true }
            DropdownMenu(expanded = expandedLang, onDismissRequest = { expandedLang = false }, modifier = Modifier.background(BeigeSurface)) {
                viewModel.languages.forEach { l -> DropdownMenuItem(text = { Text(l, color = ForestGreen) }, onClick = { viewModel.updateProfileField("language", l); expandedLang = false }) }
            }
        }
        SettingsItem(title = "Cambiar contraseña", value = "Recibirás un email", icon = Icons.Default.Lock) { viewModel.sendPasswordReset() }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("SOBRE SERVICLICK")
        SettingsItem(title = "Términos y condiciones", value = "Leer documento", icon = Icons.Default.Menu) { showTerms = true }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = BeigeSurface)) {
            Text("CERRAR SESIÓN", color = ForestGreen, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { viewModel.deleteAccount(onLogout) }, modifier = Modifier.fillMaxWidth()) {
            Text("Eliminar cuenta definitivamente", color = Color(0xFFFF5252))
        }
    }

    if (showEditName) {
        EditNameDialog(
            title = if(role == "cliente") "Editar nombre" else "Editar nombre comercial",
            initialValue = name, isCompany = (role == "empresa"), onDismiss = { showEditName = false }
        ) { newValue -> viewModel.updateProfileField(if(role == "cliente") "name" else "companyName", newValue) }
    }

    if (showEditPhone) {
        EditPhoneDialog(initialPhoneStr = phone, prefixes = viewModel.phonePrefixes, onDismiss = { showEditPhone = false }) { newValue ->
            viewModel.updateProfileField("phone", newValue)
        }
    }

    if (showEditDescription) {
        EditDescriptionDialog(initialValue = description, onDismiss = { showEditDescription = false }) { newValue ->
            viewModel.updateProfileField("description", newValue)
        }
    }

    if (showTerms) {
        AlertDialog(
            onDismissRequest = { showTerms = false },
            confirmButton = { TextButton(onClick = { showTerms = false }) { Text("ENTENDIDO", color = SunsetOrange, fontWeight = FontWeight.Bold) } },
            title = { Text("Términos y Privacidad", color = ForestGreen, fontWeight = FontWeight.Bold) },
            text = { Text("Aquí iría el texto legal oficial de ServiClick. Al usar esta aplicación, aceptas que tus datos sean utilizados para conectar profesionales con clientes, cumpliendo con la normativa vigente de protección de datos (RGPD).", color = ForestGreen.copy(alpha = 0.8f)) },
            containerColor = BeigeSurface
        )
    }
}

@Composable
fun EditDescriptionDialog(initialValue: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = BeigeSurface,
        title = { Text("Editar Descripción", color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { if(it.length <= 300) text = it },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    colors = getTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences)
                )
                Text(
                    text = "${text.length}/300",
                    color = ForestGreen.copy(0.5f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        },
        confirmButton = { TextButton(onClick = { onSave(text.trim()); onDismiss() }) { Text("GUARDAR", color = SunsetOrange, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(0.6f)) } }
    )
}

@Composable
fun EditNameDialog(title: String, initialValue: String, isCompany: Boolean, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss, title = { Text(title, color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    val isValid = if(isCompany) it.matches(Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s&'-]*$")) && it.length <= 50
                    else it.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$")) && it.length <= 40
                    if (isValid) text = it
                },
                singleLine = true, label = { Text("Nombre") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words),
                colors = getTextFieldColors()
            )
        },
        confirmButton = { TextButton(onClick = { onSave(text.trim()); onDismiss() }, enabled = text.length >= 3) { Text("GUARDAR", color = SunsetOrange, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(alpha = 0.7f)) } },
        containerColor = BeigeSurface
    )
}

@Composable
fun EditPhoneDialog(initialPhoneStr: String, prefixes: List<String>, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    val parts = initialPhoneStr.split(" ")
    val initialPrefix = if (parts.size > 1) prefixes.find { it.startsWith(parts[0]) } ?: prefixes[0] else prefixes[0]
    val initialNumber = if (parts.size > 1) parts.last() else ""

    var tempPrefix by remember { mutableStateOf(initialPrefix) }
    var tempPhone by remember { mutableStateOf(initialNumber) }

    AlertDialog(
        onDismissRequest = onDismiss, title = { Text("Editar teléfono", color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            PhoneInputField(
                prefixValue = tempPrefix, onPrefixChange = { tempPrefix = it },
                phoneValue = tempPhone, onPhoneChange = { if (it.all { char -> char.isDigit() } && it.length <= 15) tempPhone = it },
                prefixes = prefixes
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cleanPrefix = tempPrefix.substringBefore(" ")
                    onSave("$cleanPrefix ${tempPhone.trim()}")
                    onDismiss()
                },
                enabled = tempPhone.length >= 9
            ) { Text("GUARDAR", color = SunsetOrange, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(alpha = 0.7f)) } },
        containerColor = BeigeSurface
    )
}

@Composable
fun SectionTitle(title: String) { Text(title, color = SunsetOrange, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)) }

@Composable
fun SettingsItem(title: String, value: String, icon: ImageVector, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column { Text(title, color = ForestGreen.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall); Text(value, color = ForestGreen, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold) }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = ForestGreen.copy(alpha = 0.3f))
    }
    HorizontalDivider(color = ForestGreen.copy(alpha = 0.1f))
}