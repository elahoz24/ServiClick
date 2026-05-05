package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.serviclick.domain.model.Appointment
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CompanyRequestsTab(viewModel: HomeViewModel) {
    val allRequests by viewModel.companyAppointments.collectAsState()

    // Filtramos las listas según el estado
    val activeRequests = allRequests.filter { it.status in listOf("Pendiente", "Presupuestada", "Aceptada") }
    val historyRequests = allRequests.filter { it.status in listOf("Rechazada", "Finalizada") }

    // Control de la pestaña seleccionada (0 = Activas, 1 = Historial)
    var selectedTab by remember { mutableIntStateOf(0) }

    // Para el diálogo de presupuesto
    var showBudgetDialog by remember { mutableStateOf(false) }
    var selectedRequestId by remember { mutableStateOf<String?>(null) }
    var budgetPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(CreamBackground)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gestión de Solicitudes", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = { viewModel.fetchCompanyAppointments() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = SunsetOrange)
            }
        }

        // LAS PESTAÑAS (TABS)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CreamBackground,
            contentColor = SunsetOrange
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Activas (${activeRequests.size})", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) SunsetOrange else ForestGreen.copy(0.6f)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Historial (${historyRequests.size})", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) SunsetOrange else ForestGreen.copy(0.6f)) }
            )
        }

        val listToShow = if (selectedTab == 0) activeRequests else historyRequests

        if (listToShow.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(if (selectedTab == 0) "No tienes solicitudes activas." else "Tu historial está vacío.", color = ForestGreen.copy(0.6f))
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(listToShow) { req ->
                    RequestCard(
                        request = req,
                        onReject = { viewModel.respondToRequest(req.id, 0.0, "Rechazada") },
                        onAccept = {
                            selectedRequestId = req.id
                            budgetPrice = ""
                            showBudgetDialog = true
                        }
                    )
                }
            }
        }
    }

    // DIÁLOGO PARA ENVIAR PRESUPUESTO
    if (showBudgetDialog && selectedRequestId != null) {
        val isValid = budgetPrice.toDoubleOrNull() != null && budgetPrice.toDouble() > 0

        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            containerColor = BeigeSurface,
            title = { Text("Enviar Presupuesto", color = ForestGreen, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Indica el precio estimado para este trabajo. El cliente tendrá que aceptarlo para confirmar la cita.", style = MaterialTheme.typography.bodySmall, color = ForestGreen.copy(0.8f))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = budgetPrice,
                        onValueChange = { budgetPrice = it },
                        label = { Text("Precio en €") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = getTextFieldColors(),
                        singleLine = true,
                        isError = !isValid,
                        supportingText = { if (!isValid) Text("Introduce un precio válido") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.respondToRequest(selectedRequestId!!, budgetPrice.toDouble(), "Presupuestada")
                        showBudgetDialog = false
                    },
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                ) {
                    Text("ENVIAR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) { Text("CANCELAR", color = ForestGreen) }
            }
        )
    }
}

@Composable
fun RequestCard(request: Appointment, onReject: () -> Unit, onAccept: () -> Unit) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = formatter.format(Date(request.startDateMillis))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BeigeSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(request.clientName, fontWeight = FontWeight.Bold, color = ForestGreen, style = MaterialTheme.typography.titleLarge)

                val statusColor = when(request.status) {
                    "Pendiente" -> Color(0xFFFFA000)
                    "Presupuestada" -> Color(0xFF1976D2)
                    "Aceptada" -> Color(0xFF388E3C)
                    "Rechazada" -> Color(0xFFD32F2F)
                    "Finalizada" -> Color.Gray
                    else -> Color.Gray
                }
                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(request.status, color = statusColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Fecha: $dateStr", color = ForestGreen.copy(0.8f))
            Text("Hora: ${request.time}", color = ForestGreen.copy(0.8f))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Descripción:", fontWeight = FontWeight.Bold, color = ForestGreen)
            Text(request.description, color = ForestGreen.copy(0.7f), style = MaterialTheme.typography.bodyMedium)

            if (request.status != "Pendiente" && request.status != "Rechazada") {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Presupuesto enviado: ${request.price} €", fontWeight = FontWeight.Bold, color = SunsetOrange)
            }

            if (request.status == "Pendiente") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onReject) {
                        Text("RECHAZAR", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)) {
                        Text("DAR PRESUPUESTO", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}