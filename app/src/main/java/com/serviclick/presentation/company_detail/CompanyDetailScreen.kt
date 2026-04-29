package com.serviclick.presentation.company_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.serviclick.presentation.home.components.Base64Image
import com.serviclick.presentation.home.components.formatReviewCount
import com.serviclick.presentation.home.components.getTextFieldColors
import com.serviclick.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CompanyDetailScreen(
    viewModel: CompanyDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val company by viewModel.company.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Estados de la cita
    val showDialog by viewModel.showDialog.collectAsState()
    val time by viewModel.appointmentTime.collectAsState()
    val desc by viewModel.appointmentDesc.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val message by viewModel.message.collectAsState()

    message?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            title = { Text("Aviso", color = ForestGreen, fontWeight = FontWeight.Bold) },
            text = { Text(msg, color = ForestGreen) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearMessage() }) { Text("OK", color = SunsetOrange, fontWeight = FontWeight.Bold) }
            },
            containerColor = BeigeSurface
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(CreamBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SunsetOrange)
        }
        return
    }

    val profile = company
    if (profile == null) {
        Box(modifier = Modifier.fillMaxSize().background(CreamBackground), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error al cargar el perfil", color = ForestGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)) {
                    Text("Volver", color = CreamBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(CreamBackground)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(BeigeSurface)) {
                if (profile.bannerImage.isNotEmpty()) {
                    Base64Image(base64String = profile.bannerImage, modifier = Modifier.fillMaxSize())
                }
                IconButton(onClick = onNavigateBack, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = CreamBackground, modifier = Modifier.background(ForestGreen.copy(0.6f), CircleShape).padding(8.dp))
                }
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Surface(modifier = Modifier.size(120.dp).offset(y = (-60).dp), shape = CircleShape, color = CreamBackground, shadowElevation = 6.dp) {
                    if (profile.profileImage.isNotEmpty()) {
                        Base64Image(base64String = profile.profileImage, modifier = Modifier.fillMaxSize().clip(CircleShape))
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(24.dp), tint = SunsetOrange)
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-40).dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = profile.name.ifEmpty { "Nombre de Empresa" },
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForestGreen,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFB300), modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    if (profile.reviewCount > 0) {
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", profile.rating),
                            style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${formatReviewCount(profile.reviewCount)} valoraciones)",
                            style = MaterialTheme.typography.titleMedium, color = ForestGreen.copy(alpha = 0.6f)
                        )
                    } else {
                        Text("Nuevo profesional", style = MaterialTheme.typography.titleLarge, color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(color = SunsetOrange.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp), modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = profile.category.ifEmpty { "Categoría general" }, color = SunsetOrange, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Ubicación", style = MaterialTheme.typography.titleLarge, color = ForestGreen, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(12.dp))
                Surface(color = BeigeSurface, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = SunsetOrange.copy(0.2f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SunsetOrange, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(profile.city.ifEmpty { "Ciudad no definida" }, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Text(if (profile.address.isNotEmpty()) profile.address else "A domicilio", color = ForestGreen.copy(0.7f), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Sobre nosotros", style = MaterialTheme.typography.titleLarge, color = ForestGreen, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = profile.description.ifEmpty { "Esta empresa aún no ha añadido una descripción." },
                    style = MaterialTheme.typography.bodyLarge, color = ForestGreen.copy(0.8f),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Surface(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(), color = CreamBackground, shadowElevation = 24.dp) {
            Button(
                onClick = { viewModel.setShowDialog(true) },
                modifier = Modifier.fillMaxWidth().padding(24.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
            ) {
                Text("SOLICITAR SERVICIO", fontWeight = FontWeight.ExtraBold, color = CreamBackground)
            }
        }
    }

    // --- POPUP PARA CREAR CITA (PANTALLA COMPLETA) ---
    if (showDialog) {
        val bookedRanges by viewModel.bookedRanges.collectAsState()
        val takenSlots by viewModel.takenSlots.collectAsState()

        // Lógica de límites: Hoy hasta 6 meses después
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 6)
        val sixMonthsLater = calendar.timeInMillis

        val dateRangePickerState = rememberDateRangePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // Solo seleccionable si está entre Hoy (menos 24h para zona horaria) y 6 meses
                    val isInRange = utcTimeMillis in (today - 86400000)..sixMonthsLater
                    val isBooked = bookedRanges.any { range -> utcTimeMillis in range }
                    return isInRange && !isBooked
                }
            }
        )

        // Actualiza las horas disponibles cuando se selecciona un único día
        LaunchedEffect(dateRangePickerState.selectedStartDateMillis) {
            dateRangePickerState.selectedStartDateMillis?.let {
                viewModel.updateAvailableSlots(it)
            }
        }

        val isRange = dateRangePickerState.selectedEndDateMillis != null &&
                dateRangePickerState.selectedEndDateMillis != dateRangePickerState.selectedStartDateMillis

        val isValid = dateRangePickerState.selectedStartDateMillis != null &&
                desc.trim().isNotEmpty() &&
                (isRange || time.isNotEmpty())

        Dialog(
            onDismissRequest = { if (!isSubmitting) viewModel.setShowDialog(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = CreamBackground) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.setShowDialog(false) }, enabled = !isSubmitting) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = ForestGreen)
                        }
                        Text("Solicitar Servicio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = ForestGreen)
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
                        if (dateRangePickerState.selectedStartDateMillis != null && !isRange) {
                            Text("Hora disponible", fontWeight = FontWeight.Bold, color = ForestGreen)
                            Spacer(modifier = Modifier.height(12.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val allHours = company?.workingHours ?: emptyList()

                                allHours.forEach { slot ->
                                    // CAMBIO 2: Si el slot está en la lista de ocupados, lo marcamos como taken
                                    val isTaken = slot in takenSlots
                                    val isSelected = time == slot

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.onTimeChanged(slot) },
                                        label = { Text(slot) },
                                        enabled = !isTaken, // Si está taken, se deshabilita
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = SunsetOrange,
                                            selectedLabelColor = Color.White,
                                            disabledContainerColor = Color.LightGray.copy(alpha = 0.2f)
                                        )
                                    )
                                }
                            }
                        } else if (isRange) {
                            Surface(color = SunsetOrange.copy(0.1f), shape = RoundedCornerShape(8.dp)) {
                                Text("Trabajo de varios días: Se solicitará jornada completa",
                                    modifier = Modifier.padding(12.dp), color = SunsetOrange, style = MaterialTheme.typography.bodySmall)
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
                                val finalTime = if (isRange) "Jornada Completa" else time
                                viewModel.createAppointment(
                                    startMillis = dateRangePickerState.selectedStartDateMillis!!,
                                    endMillis = dateRangePickerState.selectedEndDateMillis ?: dateRangePickerState.selectedStartDateMillis!!,
                                    finalTime = finalTime
                                )
                            },
                            enabled = isValid && !isSubmitting,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange, disabledContainerColor = SunsetOrange.copy(alpha = 0.4f))
                        ) {
                            if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            else Text("CONFIRMAR SOLICITUD", fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }
    }
}