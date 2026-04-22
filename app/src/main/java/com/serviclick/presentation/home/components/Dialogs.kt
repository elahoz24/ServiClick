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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.serviclick.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SunsetOrange, unfocusedBorderColor = ForestGreen.copy(alpha = 0.3f),
    focusedTextColor = ForestGreen, unfocusedTextColor = ForestGreen,
    focusedLabelColor = SunsetOrange, unfocusedLabelColor = ForestGreen.copy(alpha = 0.6f),
    cursorColor = SunsetOrange, errorBorderColor = Color(0xFFFF5252)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneInputField(prefixValue: String, onPrefixChange: (String) -> Unit, phoneValue: String, onPhoneChange: (String) -> Unit, prefixes: List<String>) {
    var expandedPrefix by remember { mutableStateOf(false) }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        ExposedDropdownMenuBox(expanded = expandedPrefix, onExpandedChange = { expandedPrefix = !expandedPrefix }, modifier = Modifier.weight(0.35f)) {
            OutlinedTextField(value = prefixValue.substringBefore(" "), onValueChange = { }, readOnly = true, label = { Text("Prefijo") }, colors = getTextFieldColors(), modifier = Modifier.menuAnchor().fillMaxWidth(), singleLine = true)
            ExposedDropdownMenu(expanded = expandedPrefix, onDismissRequest = { expandedPrefix = false }, modifier = Modifier.background(BeigeSurface)) {
                prefixes.forEach { option -> DropdownMenuItem(text = { Text(option, color = ForestGreen) }, onClick = { onPrefixChange(option); expandedPrefix = false }) }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(value = phoneValue, onValueChange = onPhoneChange, label = { Text("Teléfono") }, modifier = Modifier.weight(0.65f), colors = getTextFieldColors(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
    }
}

@Composable
fun EditNameDialog(title: String, initialValue: String, isCompany: Boolean, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(onDismissRequest = onDismiss, containerColor = BeigeSurface, title = { Text(title, color = ForestGreen, fontWeight = FontWeight.Bold) }, text = { OutlinedTextField(value = text, onValueChange = { text = it }, singleLine = true, colors = getTextFieldColors()) }, confirmButton = { TextButton(onClick = { onSave(text.trim()); onDismiss() }) { Text("GUARDAR", color = SunsetOrange, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(0.6f)) } })
}

@Composable
fun EditPhoneDialog(initialPhoneStr: String, prefixes: List<String>, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    val parts = initialPhoneStr.split(" ")
    var tempPrefix by remember { mutableStateOf(if (parts.size > 1) prefixes.find { it.startsWith(parts[0]) } ?: prefixes[0] else prefixes[0]) }
    var tempPhone by remember { mutableStateOf(if(parts.size > 1) parts.last() else "") }
    AlertDialog(onDismissRequest = onDismiss, containerColor = BeigeSurface, title = { Text("Teléfono", color = ForestGreen, fontWeight = FontWeight.Bold) }, text = { PhoneInputField(tempPrefix, { tempPrefix = it }, tempPhone, { tempPhone = it }, prefixes) }, confirmButton = { TextButton(onClick = { onSave("${tempPrefix.substringBefore(" ")} ${tempPhone.trim()}"); onDismiss() }) { Text("GUARDAR", color = SunsetOrange, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(0.6f)) } })
}

@Composable
fun EditAddressDialog(initialValue: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(onDismissRequest = onDismiss, containerColor = BeigeSurface, title = { Text("Dirección", color = ForestGreen, fontWeight = FontWeight.Bold) }, text = { OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth(), colors = getTextFieldColors()) }, confirmButton = { TextButton(onClick = { onSave(text.trim()); onDismiss() }) { Text("GUARDAR", color = SunsetOrange, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(0.6f)) } })
}

@Composable
fun EditDescriptionDialog(initialValue: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(onDismissRequest = onDismiss, containerColor = BeigeSurface, title = { Text("Descripción", color = ForestGreen, fontWeight = FontWeight.Bold) }, text = { OutlinedTextField(value = text, onValueChange = { if(it.length <= 300) text = it }, modifier = Modifier.fillMaxWidth().height(140.dp), colors = getTextFieldColors()) }, confirmButton = { TextButton(onClick = { onSave(text.trim()); onDismiss() }) { Text("GUARDAR", color = SunsetOrange, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("CANCELAR", color = ForestGreen.copy(0.6f)) } })
}