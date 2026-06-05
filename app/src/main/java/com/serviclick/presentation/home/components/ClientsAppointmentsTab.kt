package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
 * Pestaña para la gestión de las citas desde el lado del cliente.
 * Muestra las solicitudes activas e históricas, y gestionar la lógica de pagos y valoraciones.
 */
@Composable
fun ClientAppointmentsTab(viewModel: HomeViewModel) {
    val currentUserId by viewModel.userId.collectAsState()
    val allAppointments by viewModel.appointments.collectAsState()
    val mockCard by viewModel.mockCard.collectAsState()
    val companiesList by viewModel.companiesList.collectAsState()

    val savedCards = remember(mockCard) {
        if (mockCard.isBlank()) emptyList() else mockCard.split(";;")
    }

    // "Pendiente Efectivo" se queda en activas para que el usuario no pierda el rastro de la deuda
    val activeAppointments = allAppointments.filter {
        it.status in listOf(
            "Pendiente",
            "Presupuestada",
            "Aceptada",
            "Finalizada",
            "Pendiente Efectivo"
        )
    }
    val historyAppointments = allAppointments.filter { it.status in listOf("Rechazada", "Pagada") }

    var selectedTab by remember { mutableIntStateOf(0) }

    // Estados que controlan qué Diálogo se abre y con qué datos. Si son 'null', el diálogo se oculta.
    var reviewDialogAppt by remember { mutableStateOf<Appointment?>(null) }
    var reviewRating by remember { mutableDoubleStateOf(0.0) }
    var reviewComment by remember { mutableStateOf("") }
    var payDialogAppt by remember { mutableStateOf<Appointment?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(CreamBackground)) {

        // --- SUB-NAVEGACIÓN DE PESTAÑAS ---
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
                            "Próximas (${activeAppointments.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) SunsetOrange else ForestGreen.copy(0.6f)
                        )
                    })
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Historial (${historyAppointments.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) SunsetOrange else ForestGreen.copy(0.6f)
                        )
                    })
            }

            IconButton(onClick = { viewModel.fetchClientAppointments() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = SunsetOrange)
            }
        }

        val listToShow = if (selectedTab == 0) activeAppointments else historyAppointments

        if (listToShow.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (selectedTab == 0) "No tienes próximas reservas." else "Tu historial está vacío.",
                    color = ForestGreen.copy(0.6f)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listToShow) { appt ->
                    AppointmentCard(
                        appointment = appt,
                        onAcceptBudget = { viewModel.updateAppointmentStatus(appt.id, "Aceptada") },
                        onRejectBudget = {
                            viewModel.updateAppointmentStatus(
                                appt.id,
                                "Rechazada"
                            )
                        },
                        onOpenChat = { viewModel.openChatForAppointment(appt.id, currentUserId) },
                        onPayClick = { payDialogAppt = appt },
                        onReviewClick = { reviewDialogAppt = appt }
                    )
                }
            }
        }
    }

    // --- DIÁLOGO DE PASARELA DE PAGO SIMULADA ---
    if (payDialogAppt != null) {
        val appt = payDialogAppt!!
        var selectedCardIndex by remember { mutableIntStateOf(0) }

        // Cruzamos datos: Verificamos qué métodos de pago acepta el profesional de esta cita
        val company = companiesList.find { it.id == appt.companyId }
        val acceptedByCompany = company?.acceptedPayments ?: listOf("Tarjeta", "Efectivo")
        val acceptsCard = acceptedByCompany.contains("Tarjeta")
        val acceptsCash = acceptedByCompany.contains("Efectivo")

        AlertDialog(
            onDismissRequest = { payDialogAppt = null },
            containerColor = BeigeSurface,
            title = {
                Text(
                    "Finalizar y Abonar",
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Total a pagar por el servicio:", color = ForestGreen.copy(0.7f))
                    Text(
                        "${appt.price} €",
                        style = MaterialTheme.typography.headlineMedium,
                        color = SunsetOrange,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Selecciona tu método de pago:",
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (savedCards.isEmpty()) {
                        Text(
                            "⚠️ No tienes tarjetas vinculadas. Ve a 'Mi Cuenta' para configurar una tarjeta de crédito.",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        savedCards.forEachIndexed { index, cardData ->
                            val num = cardData.split("|").firstOrNull() ?: ""
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCardIndex = index }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedCardIndex == index,
                                    onClick = { selectedCardIndex = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = SunsetOrange)
                                )
                                Text("Tarjeta terminada en ${num.takeLast(4)}", color = ForestGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = ForestGreen.copy(0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.updateAppointmentStatus(appt.id, "Pagada", "Tarjeta")
                            payDialogAppt = null
                        },
                        enabled = savedCards.isNotEmpty() && acceptsCard,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (acceptsCard) ForestGreen else Color.Gray)
                    ) {
                        Text(
                            if (acceptsCard) "PAGAR AHORA CON TARJETA" else "LA EMPRESA NO ACEPTA TARJETA",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Permite el pago en mano, pasando a un estado de verificación manual por el profesional
                    OutlinedButton(
                        onClick = {
                            viewModel.updateAppointmentStatus(
                                appt.id,
                                "Pendiente Efectivo",
                                "Efectivo"
                            )
                            payDialogAppt = null
                        },
                        enabled = acceptsCash,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (acceptsCash) "Pagar en efectivo al profesional" else "La empresa no acepta efectivo",
                            color = if (acceptsCash) ForestGreen else Color.Gray
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { payDialogAppt = null }) {
                    Text(
                        "Cancelar",
                        color = ForestGreen
                    )
                }
            }
        )
    }

    // --- DIÁLOGO DE VALORACIÓN AL PROFESIONAL ---
    if (reviewDialogAppt != null) {
        AlertDialog(
            // Limpiamos los estados al cancelar para evitar bugs visuales persistentes
            onDismissRequest = { reviewDialogAppt = null; reviewRating = 0.0; reviewComment = "" },
            containerColor = BeigeSurface,
            title = { Text("Valorar Trabajo", color = ForestGreen, fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "¿Cómo valoras el servicio de ${reviewDialogAppt!!.companyName}?",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "(Toca dos veces una estrella para dar la mitad)",
                        style = MaterialTheme.typography.bodySmall,
                        color = ForestGreen.copy(0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            val icon = when {
                                reviewRating >= i -> Icons.Filled.Star
                                reviewRating >= i - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                                else -> Icons.Outlined.StarBorder
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        reviewRating =
                                            if (reviewRating == i.toDouble()) i - 0.5 else i.toDouble()
                                    })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Comentario (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = getTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitReview(reviewDialogAppt!!, reviewRating, reviewComment)
                        reviewDialogAppt = null
                        reviewRating = 0.0
                        reviewComment = ""
                    },
                    enabled = reviewRating > 0.0,
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                ) { Text("ENVIAR", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = {
                    reviewDialogAppt = null; reviewRating = 0.0; reviewComment = ""
                }) { Text("Cancelar", color = ForestGreen) }
            }
        )
    }
}

/**
 * Componente individual de Tarjeta de Cita.
 * Define la jerarquía visual de los datos y los botones (Call To Actions) dinámicos según el estado.
 */
@Composable
fun AppointmentCard(
    appointment: Appointment,
    onAcceptBudget: () -> Unit,
    onRejectBudget: () -> Unit,
    onOpenChat: () -> Unit,
    onPayClick: () -> Unit,
    onReviewClick: () -> Unit
) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val startStr = formatter.format(Date(appointment.startDateMillis))
    val endStr = formatter.format(Date(appointment.endDateMillis))
    val dateStr =
        if (appointment.endDateMillis > appointment.startDateMillis && startStr != endStr) "$startStr al $endStr" else startStr

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BeigeSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    appointment.companyName,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    style = MaterialTheme.typography.titleLarge
                )
                // Colorimetría del estado para feedback visual rápido
                val statusColor = when (appointment.status) {
                    "Pendiente" -> Color(0xFFFFA000)
                    "Presupuestada" -> Color(0xFF1976D2)
                    "Aceptada" -> Color(0xFF388E3C)
                    "Pendiente Efectivo" -> Color(0xFFE65100) // Naranja oscuro para destacar deuda
                    "Pagada" -> Color(0xFF388E3C)
                    "Rechazada" -> Color(0xFFD32F2F)
                    "Finalizada" -> Color(0xFF1976D2)
                    else -> Color.Gray
                }
                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        appointment.status,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Fecha: $dateStr", color = ForestGreen.copy(0.8f))
            Text("Hora/Tipo: ${appointment.time}", color = ForestGreen.copy(0.8f))

            if (appointment.price > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Precio: ${appointment.price} €",
                    fontWeight = FontWeight.ExtraBold,
                    color = SunsetOrange,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            if (appointment.status == "Pagada" && appointment.paymentMethod.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Método de pago: ${appointment.paymentMethod}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ForestGreen.copy(0.6f)
                )
            }

            // Aviso al cliente de que el profesional tiene que confirmar
            if (appointment.status == "Pendiente Efectivo") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Esperando a que el profesional confirme el cobro.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE65100)
                )
            }

            // Renderizado condicional de los Call To Actions según la máquina de estados
            if (appointment.status == "Presupuestada") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onRejectBudget) {
                        Text(
                            "RECHAZAR",
                            color = Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onAcceptBudget,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) { Text("ACEPTAR", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            } else if (appointment.status == "Aceptada") {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onOpenChat,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                ) { Text("CONTACTAR", color = Color.White, fontWeight = FontWeight.Bold) }
            } else if (appointment.status == "Finalizada") {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onPayClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) { Text("PAGAR SERVICIO", color = Color.White, fontWeight = FontWeight.ExtraBold) }
            } else if (appointment.status == "Pagada" && !appointment.hasReview) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onReviewClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("VALORAR SERVICIO", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}