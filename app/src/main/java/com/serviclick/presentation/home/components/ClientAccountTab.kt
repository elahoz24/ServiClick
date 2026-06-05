package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.BeigeSurface
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ForestGreen
import com.serviclick.ui.theme.SunsetOrange

/**
 * Pestaña de configuración de la cuenta personal del cliente.
 * Edita los datos personales y gestionar las tarjetas de crédito simuladas.
 */
@Composable
fun ClientAccountTab(viewModel: HomeViewModel, onLogout: () -> Unit) {
    val name by viewModel.userName.collectAsState()
    val phone by viewModel.userPhone.collectAsState()
    val city by viewModel.savedCity.collectAsState()
    val address by viewModel.savedAddress.collectAsState()

    val userProfile by viewModel.uiState.collectAsState()
    val mockCard by viewModel.mockCard.collectAsState()

    // Pseudo-deserialización de la base de datos en un String
    val cardsList = remember(mockCard) {
        if (mockCard.isBlank()) emptyList() else mockCard.split(";;")
    }

    var showEditName by remember { mutableStateOf(false) }
    var showEditPhone by remember { mutableStateOf(false) }
    var showEditAddress by remember { mutableStateOf(false) }
    var showAddCard by remember { mutableStateOf(false) }
    var cardToDelete by remember { mutableStateOf<String?>(null) }
    var expandedCity by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        SectionTitle("DATOS PERSONALES")
        SettingsItem(
            "Nombre completo",
            name.ifEmpty { "Configurar" },
            Icons.Default.AccountCircle
        ) { showEditName = true }
        SettingsItem(
            "Teléfono móvil",
            phone.ifEmpty { "Configurar" },
            Icons.Default.Phone
        ) { showEditPhone = true }

        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("DIRECCIÓN")
        Box {
            SettingsItem(
                "Ciudad (Para búsquedas)",
                city.ifEmpty { "Configurar" },
                Icons.Default.Search
            ) { expandedCity = true }
            DropdownMenu(
                expanded = expandedCity,
                onDismissRequest = { expandedCity = false },
                modifier = Modifier.background(BeigeSurface)
            ) {
                viewModel.provinces.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p, color = ForestGreen) },
                        onClick = { viewModel.updateAccountField("city", p); expandedCity = false })
                }
            }
        }
        SettingsItem(
            "Dirección exacta",
            address.ifEmpty { "Añadir calle, portal, piso..." },
            Icons.Default.LocationOn
        ) { showEditAddress = true }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECCIÓN ESTILO GLOVO: MÚLTIPLES TARJETAS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "MÉTODOS DE PAGO",
                color = SunsetOrange,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { showAddCard = true }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = SunsetOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Añadir", color = SunsetOrange, fontWeight = FontWeight.Bold)
            }
        }

        if (cardsList.isEmpty()) {
            Text(
                "No tienes ninguna tarjeta guardada.",
                color = ForestGreen.copy(0.5f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        } else {
            cardsList.forEach { cardData ->
                val parts = cardData.split("|")
                val number = parts.getOrNull(0) ?: ""
                if (number.length == 16) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { cardToDelete = cardData }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CreditCard,
                            null,
                            tint = ForestGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Tarjeta Visa / MasterCard",
                                color = ForestGreen.copy(0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "**** **** **** ${number.takeLast(4)}",
                                color = ForestGreen,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Default.Delete,
                            null,
                            tint = Color.Red.copy(0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    HorizontalDivider(color = ForestGreen.copy(0.1f))
                }
            }
        }

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
    }

    // Componentes de diálogo reutilizables para inputs
    if (showEditName) EditNameDialog(
        "Editar Nombre",
        name,
        false,
        { showEditName = false }) { viewModel.updateAccountField("name", it) }
    if (showEditPhone) EditPhoneDialog(
        phone,
        viewModel.phonePrefixes,
        { showEditPhone = false }) { viewModel.updateAccountField("phone", it) }
    if (showEditAddress) EditAddressDialog(
        address,
        { showEditAddress = false }) { viewModel.updateAccountField("address", it) }

    // --- DIÁLOGO PARA AÑADIR NUEVA TARJETA ---
    if (showAddCard) {
        var inputNumber by remember { mutableStateOf("") }
        var inputName by remember { mutableStateOf("") }
        var inputExpiry by remember { mutableStateOf("") }
        var inputCvv by remember { mutableStateOf("") }

        // La validación ahora pide 4 números obligatorios en la caducidad (la barra no cuenta)
        val isValid =
            inputNumber.length == 16 && inputName.isNotEmpty() && inputExpiry.length == 4 && inputCvv.length == 3

        AlertDialog(
            onDismissRequest = { showAddCard = false },
            containerColor = BeigeSurface,
            title = { Text("Nueva Tarjeta", color = ForestGreen, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputNumber,
                        onValueChange = {
                            if (it.length <= 16 && it.all { char -> char.isDigit() }) inputNumber =
                                it
                        },
                        label = { Text("Número de Tarjeta") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = getTextFieldColors(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { if (it.length <= 30) inputName = it.uppercase() },
                        label = { Text("Titular de la Tarjeta") },
                        colors = getTextFieldColors(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = inputExpiry,
                            onValueChange = { newValue ->
                                // Guardamos SOLAMENTE NÚMEROS y máximo 4 (MMYY) previniendo que el usuario inserte letras
                                val digitsOnly = newValue.filter { it.isDigit() }.take(4)
                                inputExpiry = digitsOnly
                            },
                            label = { Text("MM/AA") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = ExpirationDateVisualTransformation(), // <--- MAGIA DEL CURSOR AQUÍ
                            colors = getTextFieldColors(),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = inputCvv,
                            onValueChange = {
                                if (it.length <= 3 && it.all { char -> char.isDigit() }) inputCvv =
                                    it
                            },
                            label = { Text("CVV") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = getTextFieldColors(),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Le añadimos la barra real para guardarlo unificado en la base de datos
                        val formattedExpiry = if (inputExpiry.length == 4) "${
                            inputExpiry.substring(
                                0,
                                2
                            )
                        }/${inputExpiry.substring(2, 4)}" else inputExpiry
                        val cardString = "$inputNumber|$inputName|$formattedExpiry|$inputCvv"
                        val updatedCards =
                            if (mockCard.isBlank()) cardString else "$mockCard;;$cardString"
                        viewModel.updateMockCard(updatedCards)
                        showAddCard = false
                    },
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                ) { Text("AÑADIR", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showAddCard = false }) {
                    Text(
                        "Cancelar",
                        color = ForestGreen
                    )
                }
            }
        )
    }

    // --- DIÁLOGO PARA ELIMINAR TARJETA SELECCIONADA ---
    if (cardToDelete != null) {
        val last4 = cardToDelete!!.split("|").firstOrNull()?.takeLast(4) ?: ""
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            containerColor = BeigeSurface,
            title = {
                Text(
                    "Eliminar método de pago",
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text("¿Seguro que quieres eliminar tu tarjeta terminada en $last4?") },
            confirmButton = {
                Button(
                    onClick = {
                        val currentList = mockCard.split(";;").toMutableList()
                        currentList.remove(cardToDelete)
                        viewModel.updateMockCard(currentList.joinToString(";;"))
                        cardToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("ELIMINAR", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) {
                    Text(
                        "Cancelar",
                        color = ForestGreen
                    )
                }
            }
        )
    }
}

/**
 * Transformación Visual nativa de Compose para formatear fechas de caducidad.
 * Engaña al renderizador para dibujar una barra "/" en la posición 2,
 * sin modificar el `String` real que se guarda en la variable de estado.
 */
class ExpirationDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Solo cogemos los 4 primeros números
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1) out += "/" // Ponemos la barra visual después del mes (MM/AA)
        }

        // Mapeo dinámico del cursor para que el usuario no se pierda al escribir o borrar a través de la barra visual
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}