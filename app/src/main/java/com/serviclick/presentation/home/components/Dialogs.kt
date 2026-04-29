package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.serviclick.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SunsetOrange, unfocusedBorderColor = ForestGreen.copy(alpha = 0.3f),
    focusedTextColor = ForestGreen, unfocusedTextColor = ForestGreen,
    focusedLabelColor = SunsetOrange, unfocusedLabelColor = ForestGreen.copy(alpha = 0.6f),
    cursorColor = SunsetOrange, errorBorderColor = Color(0xFFFF5252),
    errorLabelColor = Color(0xFFFF5252), errorSupportingTextColor = Color(0xFFFF5252)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneInputField(
    prefixValue: String,
    onPrefixChange: (String) -> Unit,
    phoneValue: String,
    onPhoneChange: (String) -> Unit,
    prefixes: List<String>,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    var expandedPrefix by remember { mutableStateOf(false) }
    // Alineación 'Top' para que si sale el mensaje de error debajo del teléfono, el prefijo no se mueva raro
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        ExposedDropdownMenuBox(expanded = expandedPrefix, onExpandedChange = { expandedPrefix = !expandedPrefix }, modifier = Modifier.weight(0.35f)) {
            OutlinedTextField(
                value = prefixValue.substringBefore(" "),
                onValueChange = { },
                readOnly = true,
                label = { Text("Prefijo") },
                colors = getTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                singleLine = true,
                supportingText = { if (isError) Text(" ") } // Mantiene la caja a la misma altura que el teléfono
            )
            ExposedDropdownMenu(expanded = expandedPrefix, onDismissRequest = { expandedPrefix = false }, modifier = Modifier.background(BeigeSurface)) {
                prefixes.forEach { option -> DropdownMenuItem(text = { Text(option, color = ForestGreen) }, onClick = { onPrefixChange(option); expandedPrefix = false }) }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = phoneValue,
            onValueChange = onPhoneChange,
            label = { Text("Teléfono") },
            modifier = Modifier.weight(0.65f),
            colors = getTextFieldColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            isError = isError,
            supportingText = supportingText
        )
    }
}

@Composable
fun EditNameDialog(title: String, initialValue: String, isCompany: Boolean, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    val isValid = text.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BeigeSurface,
        title = { Text(title, color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                colors = getTextFieldColors(),
                isError = !isValid,
                supportingText = { if (!isValid) Text("Este campo es obligatorio") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(text.trim()); onDismiss() },
                enabled = isValid,
                colors = ButtonDefaults.textButtonColors(contentColor = SunsetOrange, disabledContentColor = SunsetOrange.copy(alpha = 0.4f))
            ) {
                Text("GUARDAR", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(0.6f)) }
        }
    )
}

@Composable
fun EditPhoneDialog(initialPhoneStr: String, prefixes: List<String>, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    val parts = initialPhoneStr.split(" ")
    var tempPrefix by remember { mutableStateOf(if (parts.size > 1) prefixes.find { it.startsWith(parts[0]) } ?: prefixes[0] else prefixes[0]) }
    var tempPhone by remember { mutableStateOf(if(parts.size > 1) parts.last() else "") }

    val isValid = tempPhone.replace(" ", "").length >= 9

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BeigeSurface,
        title = { Text("Teléfono", color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            PhoneInputField(
                prefixValue = tempPrefix,
                onPrefixChange = { tempPrefix = it },
                phoneValue = tempPhone,
                onPhoneChange = { tempPhone = it },
                prefixes = prefixes,
                isError = !isValid,
                supportingText = { if (!isValid) Text("Mínimo 9 dígitos") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave("${tempPrefix.substringBefore(" ")} ${tempPhone.trim()}"); onDismiss() },
                enabled = isValid,
                colors = ButtonDefaults.textButtonColors(contentColor = SunsetOrange, disabledContentColor = SunsetOrange.copy(alpha = 0.4f))
            ) {
                Text("GUARDAR", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(0.6f)) }
        }
    )
}

@Composable
fun EditAddressDialog(initialValue: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    val isValid = text.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BeigeSurface,
        title = { Text("Dirección", color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                colors = getTextFieldColors(),
                isError = !isValid,
                supportingText = { if (!isValid) Text("La dirección es obligatoria") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(text.trim()); onDismiss() },
                enabled = isValid,
                colors = ButtonDefaults.textButtonColors(contentColor = SunsetOrange, disabledContentColor = SunsetOrange.copy(alpha = 0.4f))
            ) {
                Text("GUARDAR", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(0.6f)) }
        }
    )
}

@Composable
fun EditDescriptionDialog(initialValue: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    val isValid = text.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BeigeSurface,
        title = { Text("Descripción", color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if(it.length <= 300) text = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 5,
                colors = getTextFieldColors(),
                isError = !isValid,
                supportingText = { if (!isValid) Text("Escribe una breve descripción") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(text.trim()); onDismiss() },
                enabled = isValid,
                colors = ButtonDefaults.textButtonColors(contentColor = SunsetOrange, disabledContentColor = SunsetOrange.copy(alpha = 0.4f))
            ) {
                Text("GUARDAR", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(0.6f)) }
        }
    )
}