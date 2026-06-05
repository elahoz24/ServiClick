package com.serviclick.presentation.home.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.serviclick.domain.model.Review
import com.serviclick.presentation.home.HomeViewModel
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
 * Pestaña principal de visualización para el rol "Empresa" / "Profesional".
 * Muestra una vista previa exacta de cómo los clientes ven su escaparate público.
 */
@Composable
fun CompanyProfileTab(viewModel: HomeViewModel) {
    val name by viewModel.companyName.collectAsState()
    val city by viewModel.savedCity.collectAsState()
    val address by viewModel.savedAddress.collectAsState()
    val category by viewModel.savedCategory.collectAsState()
    val description by viewModel.savedDescription.collectAsState()
    val profileImage by viewModel.profileImage.collectAsState()
    val bannerImage by viewModel.bannerImage.collectAsState()
    val dbRating by viewModel.rating.collectAsState()
    val dbReviewCount by viewModel.reviewCount.collectAsState()
    val acceptedPayments by viewModel.acceptedPayments.collectAsState()

    val myReviews by viewModel.myCompanyReviews.collectAsState()

    var showEdit by remember { mutableStateOf(false) }

    // Si las reseñas locales no cuadran con el contador estático, usamos el mayor
    val actualReviewCount = maxOf(dbReviewCount, myReviews.size)
    val actualRating =
        if (myReviews.isNotEmpty()) myReviews.map { it.rating }.average() else dbRating

    Box(modifier = Modifier
        .fillMaxSize()
        .background(CreamBackground)) {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {

            // --- CABECERA ---
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(BeigeSurface)) {
                if (bannerImage.isNotEmpty()) Base64Image(bannerImage, Modifier.fillMaxSize())
            }

            // --- FOTO DE PERFIL FLOTANTE ---
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .offset(y = (-60).dp),
                    shape = CircleShape,
                    color = CreamBackground,
                    shadowElevation = 6.dp
                ) {
                    if (profileImage.isNotEmpty()) {
                        Base64Image(
                            base64String = profileImage,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.padding(24.dp),
                            tint = SunsetOrange
                        )
                    }
                }
            }

            // --- CONTENIDO DESCRIPTIVO ---
            Column(modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset(y = (-40).dp)) {
                Text(
                    text = name.ifEmpty { "Tu Negocio" },
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForestGreen,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        null,
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
                        text = category.ifEmpty { "Categoría" },
                        color = SunsetOrange,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Ubicación y Pagos",
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = BeigeSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                                Text(city, fontWeight = FontWeight.Bold, color = ForestGreen)
                                Text(
                                    address.ifEmpty { "Pulsa editar para añadir dirección" },
                                    color = ForestGreen.copy(0.7f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = SunsetOrange.copy(0.2f),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Payment,
                                    contentDescription = null,
                                    tint = SunsetOrange,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Acepta:", fontWeight = FontWeight.Bold, color = ForestGreen)
                                Text(
                                    if (acceptedPayments.isEmpty()) "No configurado" else acceptedPayments.joinToString(
                                        " y "
                                    ),
                                    color = ForestGreen.copy(0.7f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Sobre nosotros",
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = description.ifEmpty { "Añade una descripción para atraer clientes." },
                    color = ForestGreen.copy(0.8f),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Opiniones de tus Clientes",
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (myReviews.isEmpty()) {
                    Text(
                        "Aún no tienes valoraciones. ¡Sigue completando trabajos!",
                        color = ForestGreen.copy(0.6f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        myReviews.forEach { review ->
                            CompanyProfileReviewItem(
                                review
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // --- BOTÓN FLOTANTE INFERIOR ---
        Button(
            onClick = { showEdit = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
        ) {
            Icon(Icons.Default.Edit, null, tint = CreamBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Text("EDITAR MI PERFIL PÚBLICO", fontWeight = FontWeight.ExtraBold)
        }
    }

    if (showEdit) EditProfileFullScreen(viewModel) { showEdit = false }
}

/** Componente de UI para representar una reseña individual en el listado del perfil de la empresa. */
@Composable
fun CompanyProfileReviewItem(review: Review) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(review.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
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

/**
 * Pantalla (Diálogo a pantalla completa) para la edición profunda del perfil comercial de la empresa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileFullScreen(viewModel: HomeViewModel, onClose: () -> Unit) {
    val name by viewModel.companyName.collectAsState()
    val phone by viewModel.userPhone.collectAsState()
    val city by viewModel.savedCity.collectAsState()
    val address by viewModel.savedAddress.collectAsState()
    val category by viewModel.savedCategory.collectAsState()
    val description by viewModel.savedDescription.collectAsState()
    val profileImage by viewModel.profileImage.collectAsState()
    val bannerImage by viewModel.bannerImage.collectAsState()
    val workingHours by viewModel.workingHours.collectAsState()

    val acceptedPayments by viewModel.acceptedPayments.collectAsState()
    val dbBlockedDates by viewModel.blockedDates.collectAsState()
    val dbBlockedDaysOfWeek by viewModel.blockedDaysOfWeek.collectAsState()

    // Manejo de estado local para la agenda, permitiendo guardar todo en bloque al finalizar
    var localBlockedDates by remember(dbBlockedDates) { mutableStateOf(dbBlockedDates) }
    var localBlockedDaysOfWeek by remember(dbBlockedDaysOfWeek) { mutableStateOf(dbBlockedDaysOfWeek) }

    // --- ACCESO A LA GALERÍA DEL DISPOSITIVO ---
    val context = LocalContext.current
    val pLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            uriToBase64(
                context,
                it,
                400f
            )?.let { viewModel.updateCompanyField("profileImage", it) }
        }
    }
    val bLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            uriToBase64(
                context,
                it,
                800f
            )?.let { viewModel.updateCompanyField("bannerImage", it) }
        }
    }

    var localPayments by remember { mutableStateOf(acceptedPayments) }

    var showEditName by remember { mutableStateOf(false) }
    var showEditPhone by remember { mutableStateOf(false) }
    var showEditAddress by remember { mutableStateOf(false) }
    var showEditDesc by remember { mutableStateOf(false) }
    var showEditHours by remember { mutableStateOf(false) }
    var showAgendaDialog by remember { mutableStateOf(false) }
    var expandedCity by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamBackground)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(BeigeSurface)
                    .clickable { bLauncher.launch("image/*") }) {
                if (bannerImage.isNotEmpty()) Base64Image(bannerImage, Modifier.fillMaxSize())
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.ArrowBack,
                        null,
                        tint = CreamBackground,
                        modifier = Modifier
                            .background(ForestGreen.copy(0.5f), CircleShape)
                            .padding(4.dp)
                    )
                }
                Surface(
                    color = ForestGreen.copy(0.6f),
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        "Cambiar portada",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { pLauncher.launch("image/*") },
                    shape = CircleShape,
                    color = SunsetOrange.copy(0.2f),
                    shadowElevation = 4.dp
                ) {
                    if (profileImage.isNotEmpty()) Base64Image(
                        profileImage,
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                    else Icon(
                        Icons.Default.AddAPhoto,
                        null,
                        modifier = Modifier.padding(24.dp),
                        tint = SunsetOrange
                    )
                }
            }
            Column(modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset(y = (-20).dp)) {
                SectionTitle("DATOS DEL NEGOCIO")
                SettingsItem("Nombre comercial", name, Icons.Default.Storefront) {
                    showEditName = true
                }
                SettingsItem("Teléfono contacto", phone, Icons.Default.Phone) {
                    showEditPhone = true
                }
                Box {
                    SettingsItem("Ciudad principal", city, Icons.Default.Map) {
                        expandedCity = true
                    }
                    DropdownMenu(
                        expanded = expandedCity,
                        onDismissRequest = { expandedCity = false }) {
                        viewModel.provinces.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    viewModel.updateAccountField(
                                        "city",
                                        p
                                    ); viewModel.updateCompanyField("city", p); expandedCity = false
                                })
                        }
                    }
                }
                SettingsItem(
                    "Dirección exacta",
                    address.ifEmpty { "Configurar ahora" },
                    Icons.Default.LocationOn
                ) { showEditAddress = true }

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("ESCAPARATE")
                Box {
                    SettingsItem("Categoría", category, Icons.Default.Build) { expandedCat = true }
                    DropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = { expandedCat = false }) {
                        viewModel.categories.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    viewModel.updateCompanyField("category", c); expandedCat = false
                                })
                        }
                    }
                }
                SettingsItem(
                    "Descripción",
                    if (description.length > 25) description.take(25) + "..." else description,
                    Icons.Default.Edit
                ) { showEditDesc = true }
                SettingsItem(
                    "Horario de atención",
                    "${workingHours.size} horas activas",
                    Icons.Default.DateRange
                ) { showEditHours = true }

                SettingsItem(
                    "Gestión de Agenda",
                    "Días de descanso y vacaciones",
                    Icons.Default.DateRange
                ) { showAgendaDialog = true }

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("MÉTODOS DE PAGO ACEPTADOS")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = localPayments.contains("Tarjeta"),
                        onCheckedChange = { isChecked ->
                            localPayments =
                                if (isChecked) localPayments + "Tarjeta" else localPayments - "Tarjeta"
                        },
                        colors = CheckboxDefaults.colors(checkedColor = SunsetOrange)
                    )
                    Text("Tarjeta a través de la app", color = ForestGreen)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = localPayments.contains("Efectivo"),
                        onCheckedChange = { isChecked ->
                            localPayments =
                                if (isChecked) localPayments + "Efectivo" else localPayments - "Efectivo"
                        },
                        colors = CheckboxDefaults.colors(checkedColor = SunsetOrange)
                    )
                    Text("Efectivo en mano", color = ForestGreen)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (localPayments != acceptedPayments) viewModel.updateAcceptedPayments(
                            localPayments
                        )
                        onClose()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                ) {
                    Text("FINALIZAR EDICIÓN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // --- CONFIGURACIÓN DE AGENDA INTERACTIVA ---
    if (showAgendaDialog) {
        var currentMonthCalendar by remember {
            mutableStateOf(
                Calendar.getInstance(
                    TimeZone.getTimeZone(
                        "UTC"
                    )
                )
            )
        }
        val sdfMonth = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))

        Dialog(
            onDismissRequest = { showAgendaDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = CreamBackground) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            showAgendaDialog = false
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Cerrar",
                                tint = ForestGreen
                            )
                        }
                        Text(
                            "Configurar Agenda",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen
                        )
                    }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "1. Descanso Semanal Fijo",
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Toca los días que cierras todas las semanas (ej: Sábado y Domingo).",
                            style = MaterialTheme.typography.bodySmall,
                            color = ForestGreen.copy(0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Generación declarativa de los días de la semana
                        val weekDays = listOf(
                            Pair(Calendar.MONDAY.toLong(), "L"),
                            Pair(Calendar.TUESDAY.toLong(), "M"),
                            Pair(Calendar.WEDNESDAY.toLong(), "X"),
                            Pair(Calendar.THURSDAY.toLong(), "J"),
                            Pair(Calendar.FRIDAY.toLong(), "V"),
                            Pair(Calendar.SATURDAY.toLong(), "S"),
                            Pair(Calendar.SUNDAY.toLong(), "D")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            weekDays.forEach { (calValue, label) ->
                                val isBlocked = localBlockedDaysOfWeek.contains(calValue)
                                Surface(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            localBlockedDaysOfWeek =
                                                if (isBlocked) localBlockedDaysOfWeek - calValue else localBlockedDaysOfWeek + calValue
                                        },
                                    shape = CircleShape,
                                    color = if (isBlocked) SunsetOrange else Color.LightGray.copy(
                                        0.3f
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            label,
                                            color = if (isBlocked) Color.White else ForestGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            "2. Calendario de Días Sueltos",
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Los días fijados arriba salen en gris. Toca cualquier otro día para bloquearlo (naranja).",
                            style = MaterialTheme.typography.bodySmall,
                            color = ForestGreen.copy(0.7f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // --- CONTROLES Y RENDERIZADO DEL CALENDARIO MENSUAL CUSTOM ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                currentMonthCalendar =
                                    (currentMonthCalendar.clone() as Calendar).apply {
                                        add(
                                            Calendar.MONTH,
                                            -1
                                        )
                                    }
                            }) {
                                Icon(Icons.Default.KeyboardArrowLeft, null, tint = ForestGreen)
                            }
                            Text(
                                sdfMonth.format(currentMonthCalendar.time)
                                    .replaceFirstChar { it.uppercase() },
                                fontWeight = FontWeight.ExtraBold,
                                color = ForestGreen
                            )
                            IconButton(onClick = {
                                currentMonthCalendar =
                                    (currentMonthCalendar.clone() as Calendar).apply {
                                        add(
                                            Calendar.MONTH,
                                            1
                                        )
                                    }
                            }) {
                                Icon(Icons.Default.KeyboardArrowRight, null, tint = ForestGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // ENCABEZADOS DE LA REJILLA
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do").forEach {
                                Text(
                                    it,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ForestGreen.copy(0.5f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // GENERACIÓN MATEMÁTICA DEL MES Y BLOQUEO CRUZADO
                        val year = currentMonthCalendar.get(Calendar.YEAR)
                        val month = currentMonthCalendar.get(Calendar.MONTH)
                        val tempCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                            set(Calendar.YEAR, year); set(
                            Calendar.MONTH,
                            month
                        ); set(Calendar.DAY_OF_MONTH, 1)
                            set(Calendar.HOUR_OF_DAY, 0); set(
                            Calendar.MINUTE,
                            0
                        ); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                        }
                        val maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
                        val emptySlots =
                            if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

                        var dayCounter = 1
                        val totalSlots = emptySlots + maxDays
                        val totalRows = (totalSlots + 6) / 7

                        for (r in 0 until totalRows) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                for (c in 0 until 7) {
                                    val slotIndex = r * 7 + c
                                    if (slotIndex < emptySlots || dayCounter > maxDays) {
                                        Spacer(modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp))
                                    } else {
                                        val currentDay = dayCounter
                                        val dayCal =
                                            Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                                                .apply {
                                                    set(Calendar.YEAR, year); set(
                                                    Calendar.MONTH,
                                                    month
                                                ); set(Calendar.DAY_OF_MONTH, currentDay)
                                                    set(
                                                        Calendar.HOUR_OF_DAY,
                                                        0
                                                    ); set(Calendar.MINUTE, 0); set(
                                                    Calendar.SECOND,
                                                    0
                                                ); set(Calendar.MILLISECOND, 0)
                                                }
                                        val timeMillis = dayCal.timeInMillis

                                        // AQUI SE COMPRUEBA SI ES UN DÍA FIJO PARA DESHABILITARLO VISUALMENTE EN EL CALENDARIO INFERIOR
                                        val dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK).toLong()
                                        val isWeeklyBlocked =
                                            localBlockedDaysOfWeek.contains(dayOfWeek)
                                        val isDaySpecificBlocked =
                                            localBlockedDates.contains(timeMillis)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    when {
                                                        isWeeklyBlocked -> Color.LightGray.copy(0.5f) // Gris oscuro para días fijos
                                                        isDaySpecificBlocked -> SunsetOrange // Naranja para días sueltos
                                                        else -> Color.LightGray.copy(0.2f) // Gris claro para días libres
                                                    }
                                                )
                                                .then(
                                                    if (isWeeklyBlocked) Modifier else Modifier.clickable {
                                                        localBlockedDates =
                                                            if (isDaySpecificBlocked) localBlockedDates - timeMillis else localBlockedDates + timeMillis
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                currentDay.toString(),
                                                color = if (isDaySpecificBlocked || isWeeklyBlocked) Color.White else ForestGreen,
                                                fontWeight = if (isDaySpecificBlocked || isWeeklyBlocked) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                        dayCounter++
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = {
                                viewModel.updateAgenda(localBlockedDates, localBlockedDaysOfWeek)
                                showAgendaDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                        ) {
                            Text(
                                "GUARDAR CAMBIOS EN LA AGENDA",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditName) EditNameDialog(
        "Nombre Comercial",
        name,
        true,
        { showEditName = false }) { viewModel.updateCompanyField("name", it) }
    if (showEditPhone) EditPhoneDialog(
        phone,
        viewModel.phonePrefixes,
        { showEditPhone = false }) { viewModel.updateAccountField("phone", it) }
    if (showEditAddress) {
        EditAddressDialog(
            address,
            { showEditAddress = false }) {
            viewModel.updateAccountField("address", it); viewModel.updateCompanyField(
            "address",
            it
        )
        }
    }
    if (showEditDesc) EditDescriptionDialog(
        description,
        { showEditDesc = false }) { viewModel.updateCompanyField("description", it) }
    if (showEditHours) {
        EditHoursDialog(
            currentHours = workingHours,
            onDismiss = { showEditHours = false },
            onSave = { viewModel.updateWorkingHours(it) })
    }
}