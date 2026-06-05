package com.serviclick.presentation.home.components

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serviclick.domain.model.Appointment
import com.serviclick.presentation.chat.ChatScreen
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.BeigeSurface
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ForestGreen
import com.serviclick.ui.theme.SunsetOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pestaña centralizadora de los chats (Bandeja de Mensajes).
 * Actua como enrutador y lista de conversaciones tanto para el Cliente como para la Empresa.
 */
@Composable
fun MessagesTab(viewModel: HomeViewModel) {
    val appointmentId by viewModel.activeChatAppointmentId.collectAsState()
    val senderId by viewModel.activeChatSenderId.collectAsState()
    val isClosed by viewModel.activeChatIsClosed.collectAsState()
    val role by viewModel.userRole.collectAsState()
    val currentUserId by viewModel.userId.collectAsState()

    val clientAppointments by viewModel.appointments.collectAsState()
    val companyAppointments by viewModel.companyAppointments.collectAsState()
    // Asignación dinámica basada en el rol
    val allAppointments = if (role == "cliente") clientAppointments else companyAppointments

    // "Pendiente Efectivo" mantiene el chat activo hasta que la empresa cobra y la cita se da por cerrada
    val activeChats = allAppointments.filter {
        it.status in listOf(
            "Aceptada",
            "Finalizada",
            "Pendiente Efectivo"
        ) && it.hasMessages
    }
    val historyChats = allAppointments.filter { it.status == "Pagada" && it.hasMessages }

    var selectedTab by remember { mutableIntStateOf(0) }

    // Si hay un chat seleccionado, montamos la vista de chat directamente aquí.
    if (appointmentId != null && senderId != null) {
        // Intercepta el botón físico "Atrás" del móvil para cerrar el chat en lugar de cerrar la aplicación
        BackHandler { viewModel.closeActiveChat() }
        ChatScreen(
            appointmentId = appointmentId!!,
            senderId = senderId!!,
            isClosed = isClosed,
            onNavigateBack = { viewModel.closeActiveChat() }
        )
    } else {
        // Si no hay chat seleccionado, mostramos la lista normal
        Column(modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)) {

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CreamBackground,
                contentColor = SunsetOrange,
                divider = {}) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Activos (${activeChats.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) SunsetOrange else ForestGreen.copy(0.6f)
                        )
                    })
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Historial (${historyChats.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) SunsetOrange else ForestGreen.copy(0.6f)
                        )
                    })
            }

            val listToShow = if (selectedTab == 0) activeChats else historyChats

            if (listToShow.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (selectedTab == 0) "No tienes chats activos." else "Tu historial de chats está vacío.",
                        color = ForestGreen.copy(0.6f)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listToShow) { appt ->
                        ChatItemRow(
                            appointment = appt,
                            isClient = role == "cliente",
                            onClick = {
                                viewModel.openChatForAppointment(
                                    appointmentId = appt.id,
                                    senderId = currentUserId,
                                    isClosed = appt.status == "Pagada"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fila visual de un chat en la bandeja de entrada.
 */
@Composable
fun ChatItemRow(appointment: Appointment, isClient: Boolean, onClick: () -> Unit) {
    // Si soy cliente, veo el nombre de la empresa. Si soy empresa, veo el nombre del cliente.
    val chatTitle = if (isClient) appointment.companyName else appointment.clientName
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = formatter.format(Date(appointment.startDateMillis))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BeigeSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(50),
                color = ForestGreen.copy(alpha = 0.2f),
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chatTitle,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Ref: $dateStr a las ${appointment.time}",
                    color = ForestGreen.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Etiqueta visual de Chat Inactivo
            if (appointment.status == "Pagada") {
                Surface(color = Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        "Cerrado",
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}