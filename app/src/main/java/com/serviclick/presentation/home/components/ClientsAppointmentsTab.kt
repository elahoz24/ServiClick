package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serviclick.domain.model.Appointment
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClientAppointmentsTab(viewModel: HomeViewModel) {
    val allAppointments by viewModel.appointments.collectAsState()

    // Filtramos las listas
    val activeAppointments = allAppointments.filter { it.status in listOf("Pendiente", "Presupuestada", "Aceptada") }
    val historyAppointments = allAppointments.filter { it.status in listOf("Rechazada", "Finalizada") }

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(CreamBackground)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mis Reservas", style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = { viewModel.fetchClientAppointments() }) {
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
                text = { Text("Próximas (${activeAppointments.size})", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) SunsetOrange else ForestGreen.copy(0.6f)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Historial (${historyAppointments.size})", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) SunsetOrange else ForestGreen.copy(0.6f)) }
            )
        }

        val listToShow = if (selectedTab == 0) activeAppointments else historyAppointments

        if (listToShow.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (selectedTab == 0) "No tienes próximas reservas." else "Tu historial está vacío.", color = ForestGreen.copy(0.6f))
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(listToShow) { appt ->
                    AppointmentCard(
                        appointment = appt,
                        onAcceptBudget = { viewModel.updateAppointmentStatus(appt.id, "Aceptada") },
                        onRejectBudget = { viewModel.updateAppointmentStatus(appt.id, "Rechazada") }
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment, onAcceptBudget: () -> Unit, onRejectBudget: () -> Unit) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = formatter.format(Date(appointment.startDateMillis))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BeigeSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(appointment.companyName, fontWeight = FontWeight.Bold, color = ForestGreen, style = MaterialTheme.typography.titleLarge)

                val statusColor = when(appointment.status) {
                    "Pendiente" -> Color(0xFFFFA000)
                    "Presupuestada" -> Color(0xFF1976D2)
                    "Aceptada" -> Color(0xFF388E3C)
                    "Rechazada" -> Color(0xFFD32F2F)
                    "Finalizada" -> Color.Gray
                    else -> Color.Gray
                }
                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(appointment.status, color = statusColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Fecha: $dateStr", color = ForestGreen.copy(0.8f))
            Text("Hora: ${appointment.time}", color = ForestGreen.copy(0.8f))

            if (appointment.price > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Precio presupuestado:", color = ForestGreen.copy(0.6f))
                Text("${appointment.price} €", fontWeight = FontWeight.ExtraBold, color = SunsetOrange, style = MaterialTheme.typography.headlineSmall)
            }

            if (appointment.status == "Presupuestada") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onRejectBudget) {
                        Text("RECHAZAR", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onAcceptBudget, colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)) {
                        Text("ACEPTAR PRECIO", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}