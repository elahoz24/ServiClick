package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serviclick.domain.model.Appointment
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.BeigeSurface
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ForestGreen
import com.serviclick.ui.theme.SunsetOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pestaña "Bandeja de Entrada" o Solicitudes para el Profesional.
 * Muestra los requerimientos de los clientes y permite a la empresa emitir un presupuesto.
 */
@Composable
fun CompanyRequestsTab(viewModel: HomeViewModel) {
    val currentUserId by viewModel.userId.collectAsState()
    val allRequests by viewModel.companyAppointments.collectAsState()

    // "Pendiente Efectivo" se queda en activas para la empresa para exigirle que valide el pago
    val activeRequests = allRequests.filter {
        it.status in listOf(
            "Pendiente",
            "Presupuestada",
            "Aceptada",
            "Finalizada",
            "Pendiente Efectivo"
        )
    }
    val historyRequests = allRequests.filter { it.status in listOf("Rechazada", "Pagada") }

    var selectedTab by remember { mutableIntStateOf(0) }

    // Controladores de estado local para el Diálogo de enviar Presupuesto
    var showBudgetDialog by remember { mutableStateOf(false) }
    var selectedRequestId by remember { mutableStateOf<String?>(null) }
    var budgetPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(CreamBackground)) {

        // --- CABECERA DE PESTAÑAS (Activas e Historial) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CreamBackground,
                contentColor = SunsetOrange,
                modifier = Modifier.weight(1f),
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Activas (${activeRequests.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) SunsetOrange else ForestGreen.copy(0.6f)
                        )
                    })
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Historial (${historyRequests.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) SunsetOrange else ForestGreen.copy(0.6f)
                        )
                    })
            }
            IconButton(onClick = { viewModel.fetchCompanyAppointments() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = SunsetOrange)
            }
        }

        val listToShow = if (selectedTab == 0) activeRequests else historyRequests

        // Renderizado del contenido dinámico
        if (listToShow.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (selectedTab == 0) "No tienes solicitudes activas." else "Tu historial está vacío.",
                    color = ForestGreen.copy(0.6f)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listToShow) { req ->
                    RequestCard(
                        request = req,
                        // Callbacks inyectados desde el ViewModel
                        onReject = { viewModel.respondToRequest(req.id, 0.0, "Rechazada") },
                        onAccept = {
                            selectedRequestId = req.id
                            budgetPrice = "" // Se limpia el estado antes de abrir el diálogo
                            showBudgetDialog = true
                        },
                        onFinish = { viewModel.updateAppointmentStatus(req.id, "Finalizada") },
                        onOpenChat = { viewModel.openChatForAppointment(req.id, currentUserId) },
                        onMarkPaid = {
                            viewModel.updateAppointmentStatus(
                                req.id,
                                "Pagada",
                                "Efectivo"
                            )
                        }
                    )
                }
            }
        }
    }

    // --- DIÁLOGO PARA GENERAR PRESUPUESTO COMERCIAL ---
    if (showBudgetDialog && selectedRequestId != null) {
        val isValid = budgetPrice.toDoubleOrNull() != null && budgetPrice.toDouble() > 0

        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            containerColor = BeigeSurface,
            title = {
                Text(
                    "Enviar Presupuesto",
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Indica el precio estimado para este trabajo. El cliente tendrá que aceptarlo para confirmar la cita.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ForestGreen.copy(0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = budgetPrice,
                        onValueChange = { budgetPrice = it },
                        label = { Text("Precio en €") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = getTextFieldColors(),
                        singleLine = true,
                        isError = !isValid, // Feedback visual en caso de introducir letras en vez de números
                        supportingText = { if (!isValid) Text("Introduce un precio válido") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.respondToRequest(
                            selectedRequestId!!,
                            budgetPrice.toDouble(),
                            "Presupuestada" // Cambio de estado fundamental
                        )
                        showBudgetDialog = false
                    },
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                ) { Text("ENVIAR", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text(
                        "CANCELAR",
                        color = ForestGreen
                    )
                }
            }
        )
    }
}

/**
 * Componente Tarjeta que muestra la información de la solicitud al profesional.
 * Contiene los botones de acción (Rechazar, Dar Presupuesto, Finalizar, etc.) dependiendo del estado.
 */
@Composable
fun RequestCard(
    request: Appointment,
    onReject: () -> Unit,
    onAccept: () -> Unit,
    onFinish: () -> Unit,
    onOpenChat: () -> Unit,
    onMarkPaid: () -> Unit
) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = formatter.format(Date(request.startDateMillis))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BeigeSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = request.clientName,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                // Identificador visual de estado
                val statusColor = when (request.status) {
                    "Pendiente" -> Color(0xFFFFA000)
                    "Presupuestada" -> Color(0xFF1976D2)
                    "Aceptada" -> Color(0xFF388E3C)
                    "Pendiente Efectivo" -> Color(0xFFE65100)
                    "Pagada" -> Color(0xFF388E3C)
                    "Rechazada" -> Color(0xFFD32F2F)
                    "Finalizada" -> Color(0xFF1976D2)
                    else -> Color.Gray
                }
                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        request.status,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Fecha: $dateStr  |  Hora: ${request.time}",
                color = ForestGreen.copy(0.8f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Descripción:",
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                request.description,
                color = ForestGreen.copy(0.7f),
                style = MaterialTheme.typography.bodyMedium
            )

            if (request.status != "Pendiente" && request.status != "Rechazada") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Presupuesto: ${request.price} €",
                    fontWeight = FontWeight.Bold,
                    color = SunsetOrange
                )
            }

            if (request.status == "Pagada" && request.paymentMethod.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Método de pago: ${request.paymentMethod}",
                    fontWeight = FontWeight.Bold,
                    color = SunsetOrange
                )
            }

            // --- LÓGICA DE CONDICIONAL DE BOTONES ---
            if (request.status == "Pendiente") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onReject) {
                        Text(
                            "RECHAZAR",
                            color = Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) { Text("DAR PRESUPUESTO", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            } else if (request.status == "Aceptada") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenChat,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SunsetOrange)
                    ) { Text("CHAT", fontWeight = FontWeight.Bold) }
                    Button(
                        onClick = onFinish,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier.weight(1f)
                    ) { Text("FINALIZAR", fontWeight = FontWeight.ExtraBold) }
                }
            } else if (request.status == "Finalizada" || request.status == "Pendiente Efectivo") {
                Spacer(modifier = Modifier.height(12.dp))
                val btnText =
                    if (request.status == "Pendiente Efectivo") "CONFIRMAR COBRO EN EFECTIVO" else "MARCAR COMO PAGADA"
                OutlinedButton(
                    onClick = onMarkPaid,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen)
                ) {
                    Text(btnText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}