package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serviclick.domain.model.Appointment
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.BeigeSurface
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ForestGreen
import com.serviclick.ui.theme.SunsetOrange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Pestaña interactiva de Agenda donde el profesional puede ver su carga de trabajo mensual y diaria.
 * Muestra visualmente en un calendario los días que tienen trabajo y lista las tareas abajo.
 */
@Composable
fun CompanyAgendaTab(viewModel: HomeViewModel) {
    val allAppointments by viewModel.companyAppointments.collectAsState()

    // Solo mostramos en el calendario lo que es compromiso real (Aceptada o Finalizada)
    val calendarAppointments =
        allAppointments.filter { it.status in listOf("Aceptada", "Finalizada") }

    // Estados reactivos locales para controlar qué mes y día estamos viendo en pantalla
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    // Formateadores
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
    val dayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(modifier = Modifier
        .fillMaxSize()
        .background(CreamBackground)) {

        // --- SELECTOR DE MES ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = BeigeSurface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val newCal = calendar.clone() as Calendar
                        newCal.add(Calendar.MONTH, -1)
                        calendar = newCal
                    }) {
                        Icon(Icons.Default.KeyboardArrowLeft, null, tint = ForestGreen)
                    }

                    Text(
                        text = monthYearFormat.format(calendar.time)
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = {
                        val newCal = calendar.clone() as Calendar
                        newCal.add(Calendar.MONTH, 1)
                        calendar = newCal
                    }) {
                        Icon(Icons.Default.KeyboardArrowRight, null, tint = ForestGreen)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- DÍAS DE LA SEMANA ---
                Row(modifier = Modifier.fillMaxWidth()) {
                    val days = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                    days.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestGreen.copy(0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val daysInMonth = getDaysInMonth(calendar)
                // Se divide el listado lineal en trozos de 7 para formar filas (Semanas)
                daysInMonth.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { date ->
                            if (date == null) {
                                // Relleno invisible para mantener la estructura de 7 columnas si el mes no empieza en lunes
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                val isSelected = isSameDay(date, selectedDate.time)
                                val hasAppt = calendarAppointments.any {
                                    isSameDay(
                                        Date(it.startDateMillis),
                                        date
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                        .background(
                                            if (isSelected) SunsetOrange else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable {
                                            val newSelected = Calendar.getInstance()
                                            newSelected.time = date
                                            selectedDate = newSelected
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = SimpleDateFormat(
                                                "d",
                                                Locale.getDefault()
                                            ).format(date),
                                            color = if (isSelected) CreamBackground else ForestGreen,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp
                                        )
                                        // Dibuja el indicador de "Hay Cita" si corresponde
                                        if (hasAppt && !isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .background(SunsetOrange, CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- LISTA DE TAREAS PARA EL DÍA SELECCIONADO ---
        Text(
            text = "Trabajos para el ${dayFormat.format(selectedDate.time)}",
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = ForestGreen,
            fontWeight = FontWeight.Bold
        )

        // Extraemos de todas las citas solo las que coinciden con el día tocado
        val dailyAppts =
            calendarAppointments.filter { isSameDay(Date(it.startDateMillis), selectedDate.time) }

        if (dailyAppts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay trabajos programados", color = ForestGreen.copy(0.4f))
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dailyAppts) { appt ->
                    AgendaItemCard(appt)
                }
            }
        }
    }
}

/** Componente visual para mostrar una cita en el listado bajo el calendario. */
@Composable
fun AgendaItemCard(appt: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BeigeSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(appt.clientName, fontWeight = FontWeight.Bold, color = ForestGreen)
                Text(
                    appt.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = SunsetOrange,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    appt.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = ForestGreen.copy(0.7f)
                )
            }

            Surface(
                color = if (appt.status == "Finalizada") Color.Gray.copy(0.1f) else ForestGreen.copy(
                    0.1f
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = appt.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (appt.status == "Finalizada") Color.Gray else ForestGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- FUNCIONES DE UTILIDAD PARA LA LÓGICA DEL CALENDARIO ---

/**
 * Construye una lista de Fechas ordenadas para renderizar la cuadrícula del mes en Compose.
 * Rellena los huecos vacíos de la primera y última semana con 'null' para no descuadrar las columnas.
 */
private fun getDaysInMonth(calendar: Calendar): List<Date?> {
    val cal = calendar.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)

    // Ajuste algorítmico para que la semana empiece en Lunes (estándar europeo ISO-8601)
    val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val days = mutableListOf<Date?>()

    // Relleno inicial de la primera semana
    repeat(firstDayOfWeek) { days.add(null) }

    val currentMonth = cal.get(Calendar.MONTH)
    while (cal.get(Calendar.MONTH) == currentMonth) {
        days.add(cal.time)
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }

    // Relleno final para que la última semana tenga exactamente 7 huecos en lugar de romperse visualmente
    val remaining = days.size % 7
    if (remaining != 0) {
        repeat(7 - remaining) { days.add(null) }
    }

    return days
}

/** Comprueba si dos objetos de fecha distintos apuntan al mismo día natural del año. */
private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}