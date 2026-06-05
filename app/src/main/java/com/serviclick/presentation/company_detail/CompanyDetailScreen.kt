package com.serviclick.presentation.company_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.serviclick.domain.model.Review
import com.serviclick.presentation.home.components.Base64Image
import com.serviclick.presentation.home.components.formatReviewCount
import com.serviclick.presentation.home.components.getTextFieldColors
import com.serviclick.ui.theme.BeigeSurface
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ForestGreen
import com.serviclick.ui.theme.SunsetOrange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Interfaz de usuario (UI) que muestra la vista detallada de un profesional desde la perspectiva del cliente.
 * Permite al cliente examinar la reputación, datos, descripción de un negocio y solicitar un servicio.
 * Renderiza un perfil público dinámico. Al pulsar "Solicitar Servicio", despliega un diálogo flotante
 * con un `DateRangePicker` de Material 3, el cual inactiva los días no disponibles evaluando múltiples condiciones.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CompanyDetailScreen(
    viewModel: CompanyDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val company by viewModel.company.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    val showDialog by viewModel.showDialog.collectAsState()
    val time by viewModel.appointmentTime.collectAsState()
    val desc by viewModel.appointmentDesc.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val message by viewModel.message.collectAsState()

    // Variables de estado locales alimentadas directamente por Firebase para sincronización inmediata
    var firestoreBlockedDates by remember { mutableStateOf<List<Long>>(emptyList()) }
    var firestoreBlockedDaysOfWeek by remember { mutableStateOf<List<Long>>(emptyList()) }

    // BYPASS REACTIVO: Escucha cambios en tiempo real en la colección de la empresa.
    // Esto asegura que si la empresa bloquea un día mientras el cliente está mirando su perfil,
    // el calendario del cliente se actualiza al momento.
    LaunchedEffect(company) {
        company?.id?.let { cid ->
            if (cid.isNotEmpty()) {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("companies").document(cid).get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            // Cast defensivo para recuperar arrays de números mixtos (Int/Long) sin crashear
                            val rDates = snapshot.get("blockedDates") ?: snapshot.get("blockeddates")
                            firestoreBlockedDates = (rDates as? List<*>)?.map { (it as Number).toLong() } ?: emptyList()

                            val rDays = snapshot.get("blockedDaysOfWeek") ?: snapshot.get("blockeddaysofweek")
                            firestoreBlockedDaysOfWeek = (rDays as? List<*>)?.map { (it as Number).toLong() } ?: emptyList()
                        }
                    }
            }
        }
    }

    message?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            title = { Text("Aviso", color = ForestGreen, fontWeight = FontWeight.Bold) },
            text = { Text(msg, color = ForestGreen) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearMessage() }) {
                    Text(
                        "OK",
                        color = SunsetOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = BeigeSurface
        )
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SunsetOrange)
        }
        return
    }

    val profile = company ?: return

    // Lógica de agregación visual: si el perfil base no está actualizado, la UI calcula la media al vuelo
    val actualReviewCount = maxOf(profile.reviewCount, reviews.size)
    val actualRating =
        if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else profile.rating

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera: Banner de la empresa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(BeigeSurface)
            ) {
                if (profile.bannerImage.isNotEmpty()) {
                    Base64Image(
                        base64String = profile.bannerImage,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = CreamBackground,
                        modifier = Modifier
                            .background(ForestGreen.copy(0.6f), CircleShape)
                            .padding(8.dp)
                    )
                }
            }

            // Imagen de Perfil Flotante
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .offset(y = (-60).dp),
                    shape = CircleShape,
                    color = CreamBackground,
                    shadowElevation = 6.dp
                ) {
                    if (profile.profileImage.isNotEmpty()) {
                        Base64Image(
                            base64String = profile.profileImage,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp),
                            tint = SunsetOrange
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = profile.name.ifEmpty { "Nombre de Empresa" },
                        style = MaterialTheme.typography.headlineMedium,
                        color = ForestGreen,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    if (profile.isVerified) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Verificado",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (actualReviewCount > 0) {
                        Text(
                            text = String.format(Locale.US, "%.1f", actualRating),
                            style = MaterialTheme.typography.headlineMedium,
                            color = ForestGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${formatReviewCount(actualReviewCount)} valoraciones)",
                            style = MaterialTheme.typography.titleMedium,
                            color = ForestGreen.copy(alpha = 0.6f)
                        )
                    } else {
                        Text(
                            "Nuevo profesional",
                            style = MaterialTheme.typography.titleLarge,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = SunsetOrange.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = profile.category.ifEmpty { "Categoría general" },
                        color = SunsetOrange,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Ubicación",
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = BeigeSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = SunsetOrange.copy(0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = SunsetOrange,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                profile.city.ifEmpty { "Ciudad no definida" },
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                            Text(
                                profile.address.ifEmpty { "A domicilio" },
                                color = ForestGreen.copy(0.7f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Sobre nosotros",
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = profile.description.ifEmpty { "Esta empresa aún no ha añadido una descripción." },
                    style = MaterialTheme.typography.bodyLarge,
                    color = ForestGreen.copy(0.8f),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Opiniones de Clientes",
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (reviews.isEmpty()) {
                    Text(
                        "Aún no hay opiniones.",
                        color = ForestGreen.copy(0.6f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        reviews.forEach { review ->
                            ReviewItem(
                                review
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = CreamBackground,
            shadowElevation = 24.dp
        ) {
            Button(
                onClick = { viewModel.setShowDialog(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
            ) {
                Text(
                    "SOLICITAR SERVICIO",
                    fontWeight = FontWeight.ExtraBold,
                    color = CreamBackground
                )
            }
        }
    }

    if (showDialog) {
        val bookedRanges by viewModel.bookedRanges.collectAsState()
        val takenSlots by viewModel.takenSlots.collectAsState()

        // Configuración matemática de cotas temporales en formato estándar UTC
        val calToday = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calToday.set(Calendar.HOUR_OF_DAY, 0); calToday.set(Calendar.MINUTE, 0); calToday.set(
            Calendar.SECOND,
            0
        ); calToday.set(Calendar.MILLISECOND, 0)
        val todayUTC = calToday.timeInMillis
        calToday.add(Calendar.MONTH, 6)
        val sixMonthsLaterUTC = calToday.timeInMillis

        // Instanciación del validador dinámico del calendario M3
        val dateRangePickerState = rememberDateRangePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val isInRange = utcTimeMillis in todayUTC..sixMonthsLaterUTC
                    val calUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        .apply { timeInMillis = utcTimeMillis }

                    // Filtro 1: Invalida días fijos semanales (ej: fines de semana)
                    val dayOfWeek = calUTC.get(Calendar.DAY_OF_WEEK).toLong()
                    val isWeeklyBlocked = firestoreBlockedDaysOfWeek.contains(dayOfWeek)

                    // Filtro 2: Invalida vacaciones o días sueltos concretos bloqueados
                    val isSpecificBlocked = firestoreBlockedDates.any { blockedTime ->
                        val bCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                            .apply { timeInMillis = blockedTime }
                        bCal.get(Calendar.YEAR) == calUTC.get(Calendar.YEAR) && bCal.get(Calendar.DAY_OF_YEAR) == calUTC.get(
                            Calendar.DAY_OF_YEAR
                        )
                    }

                    // Filtro 3: Invalida rangos ya reservados por otros clientes
                    val isBookedRange = bookedRanges.any { range -> utcTimeMillis in range }
                    return isInRange && !isWeeklyBlocked && !isSpecificBlocked && !isBookedRange
                }
            }
        )

        // Escucha reactiva: actualiza las horas libres cuando el cliente elige un día válido
        LaunchedEffect(dateRangePickerState.selectedStartDateMillis) {
            dateRangePickerState.selectedStartDateMillis?.let { viewModel.updateAvailableSlots(it) }
        }

        val isValid = dateRangePickerState.selectedStartDateMillis != null && desc.trim()
            .isNotEmpty() && time.isNotEmpty()

        Dialog(
            onDismissRequest = { if (!isSubmitting) viewModel.setShowDialog(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = CreamBackground) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.setShowDialog(false) },
                            enabled = !isSubmitting
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = ForestGreen
                            )
                        }
                        Text(
                            "Solicitar Servicio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen
                        )
                    }

                    DateRangePicker(
                        state = dateRangePickerState,
                        modifier = Modifier.height(450.dp),
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            containerColor = CreamBackground,
                            selectedDayContainerColor = SunsetOrange,
                            dayInSelectionRangeContainerColor = SunsetOrange.copy(alpha = 0.2f)
                        )
                    )

                    Column(modifier = Modifier.padding(24.dp)) {
                        if (dateRangePickerState.selectedStartDateMillis != null) {
                            Text(
                                "Indica la franja horaria / disponibilidad",
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val allHours = listOf("Jornada Completa") + (profile.workingHours)
                                allHours.forEach { slot ->
                                    val isTaken = slot in takenSlots
                                    val isSelected = time == slot
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.onTimeChanged(slot) },
                                        label = { Text(slot) },
                                        enabled = !isTaken,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SunsetOrange,
                                            selectedLabelColor = Color.White,
                                            disabledContainerColor = Color.LightGray.copy(alpha = 0.2f),
                                            disabledLabelColor = Color.LightGray
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { viewModel.onDescChanged(it) },
                            label = { Text("¿Cuál es el problema?") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getTextFieldColors(),
                            minLines = 3
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                viewModel.createAppointment(
                                    startMillis = dateRangePickerState.selectedStartDateMillis!!,
                                    endMillis = dateRangePickerState.selectedEndDateMillis
                                        ?: dateRangePickerState.selectedStartDateMillis!!,
                                    finalTime = time
                                )
                            },
                            enabled = isValid && !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SunsetOrange,
                                disabledContainerColor = SunsetOrange.copy(alpha = 0.4f)
                            )
                        ) {
                            if (isSubmitting) CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            else Text("CONFIRMAR SOLICITUD", fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }
    }
}

/**
 * Componente interno que renderiza de forma individual cada reseña recibida por la empresa.
 * Extrae la lógica repetitiva fuera de la pantalla principal para mantener el código limpio y modular.
 */
@Composable
fun ReviewItem(review: Review) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(review.timestamp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = BeigeSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.clientName,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row {
                        for (i in 1..5) {
                            val icon = when {
                                review.rating >= i -> Icons.Filled.Star
                                review.rating >= i - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                                else -> Icons.Outlined.StarBorder
                            }
                            Icon(
                                icon,
                                null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        review.rating.toString(),
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }
            }
            Text(date, style = MaterialTheme.typography.labelSmall, color = ForestGreen.copy(0.5f))
            if (review.comment.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    review.comment,
                    color = ForestGreen.copy(0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}