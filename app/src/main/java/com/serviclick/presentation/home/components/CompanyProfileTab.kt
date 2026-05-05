package com.serviclick.presentation.home.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.*

@Composable
fun CompanyProfileTab(viewModel: HomeViewModel) {
    val name by viewModel.companyName.collectAsState()
    val city by viewModel.savedCity.collectAsState()
    val address by viewModel.savedAddress.collectAsState()
    val category by viewModel.savedCategory.collectAsState()
    val description by viewModel.savedDescription.collectAsState()
    val profileImage by viewModel.profileImage.collectAsState()
    val bannerImage by viewModel.bannerImage.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val reviewCount by viewModel.reviewCount.collectAsState()

    var showEdit by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(CreamBackground)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(BeigeSurface)) {
                if (bannerImage.isNotEmpty()) Base64Image(bannerImage, Modifier.fillMaxSize())
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Surface(modifier = Modifier.size(120.dp).offset(y = (-60).dp), shape = CircleShape, color = CreamBackground, shadowElevation = 6.dp) {
                    if (profileImage.isNotEmpty()) {
                        Base64Image(base64String = profileImage, modifier = Modifier.fillMaxSize().clip(CircleShape))
                    } else {
                        Icon(Icons.Default.Person, null, modifier = Modifier.padding(24.dp), tint = SunsetOrange)
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-40).dp)) {
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
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    if (reviewCount > 0) {
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", rating),
                            style = MaterialTheme.typography.headlineMedium,
                            color = ForestGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${formatReviewCount(reviewCount)} valoraciones)",
                            style = MaterialTheme.typography.titleMedium,
                            color = ForestGreen.copy(alpha = 0.6f)
                        )
                    } else {
                        Text(
                            text = "Nuevo profesional",
                            style = MaterialTheme.typography.titleLarge,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(color = SunsetOrange.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp), modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = category.ifEmpty { "Categoría" }, color = SunsetOrange, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Ubicación", style = MaterialTheme.typography.titleLarge, color = ForestGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(color = BeigeSurface, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = SunsetOrange.copy(0.2f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SunsetOrange, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(city, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Text(address.ifEmpty { "Pulsa editar para añadir dirección" }, color = ForestGreen.copy(0.7f), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Sobre nosotros", style = MaterialTheme.typography.titleLarge, color = ForestGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = description.ifEmpty { "Añade una descripción para atraer clientes." },
                    color = ForestGreen.copy(0.8f),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Button(
            onClick = { showEdit = true },
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
        ) {
            Icon(Icons.Default.Edit, null, tint = CreamBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Text("EDITAR MI PERFIL PÚBLICO", fontWeight = FontWeight.ExtraBold)
        }
    }

    if (showEdit) EditProfileFullScreen(viewModel) { showEdit = false }
}

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

    val context = LocalContext.current

    val pLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { it?.let { uriToBase64(context, it, 400f)?.let { viewModel.updateCompanyField("profileImage", it) } } }
    val bLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { it?.let { uriToBase64(context, it, 800f)?.let { viewModel.updateCompanyField("bannerImage", it) } } }

    var showEditName by remember { mutableStateOf(false) }
    var showEditPhone by remember { mutableStateOf(false) }
    var showEditAddress by remember { mutableStateOf(false) }
    var showEditDesc by remember { mutableStateOf(false) }
    var showEditHours by remember { mutableStateOf(false) }
    var expandedCity by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(modifier = Modifier.fillMaxSize().background(CreamBackground).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(BeigeSurface).clickable { bLauncher.launch("image/*") }) {
                if (bannerImage.isNotEmpty()) Base64Image(bannerImage, Modifier.fillMaxSize())
                IconButton(onClick = onClose) { Icon(Icons.Default.ArrowBack, null, tint = CreamBackground, modifier = Modifier.background(ForestGreen.copy(0.5f), CircleShape).padding(4.dp)) }
                Surface(color = ForestGreen.copy(0.6f), shape = CircleShape, modifier = Modifier.align(Alignment.Center)) {
                    Text("Cambiar portada", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().offset(y = (-50).dp), contentAlignment = Alignment.TopCenter) {
                Surface(modifier = Modifier.size(100.dp).clickable { pLauncher.launch("image/*") }, shape = CircleShape, color = SunsetOrange.copy(0.2f), shadowElevation = 4.dp) {
                    if (profileImage.isNotEmpty()) Base64Image(profileImage, Modifier.fillMaxSize().clip(CircleShape))
                    else Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.padding(24.dp), tint = SunsetOrange)
                }
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-20).dp)) {
                SectionTitle("DATOS DEL NEGOCIO")
                SettingsItem("Nombre comercial", name, Icons.Default.Storefront) { showEditName = true }
                SettingsItem("Teléfono contacto", phone, Icons.Default.Phone) { showEditPhone = true }
                Box {
                    SettingsItem("Ciudad principal", city, Icons.Default.Map) { expandedCity = true }
                    DropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }) {
                        viewModel.provinces.forEach { p ->
                            DropdownMenuItem(text = { Text(p) }, onClick = {
                                viewModel.updateAccountField("city", p)
                                viewModel.updateCompanyField("city", p)
                                expandedCity = false
                            })
                        }
                    }
                }
                SettingsItem("Dirección exacta", address.ifEmpty { "Configurar ahora" }, Icons.Default.LocationOn) { showEditAddress = true }

                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("ESCAPARATE")
                Box {
                    SettingsItem("Categoría", category, Icons.Default.Build) { expandedCat = true }
                    DropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                        viewModel.categories.forEach { c ->
                            DropdownMenuItem(text = { Text(c) }, onClick = {
                                viewModel.updateCompanyField("category", c)
                                expandedCat = false
                            })
                        }
                    }
                }
                SettingsItem("Descripción", if(description.length > 25) description.take(25)+"..." else description, Icons.Default.Edit) { showEditDesc = true }

                // NUEVA SECCIÓN DE HORARIO
                SettingsItem("Horario de atención", "${workingHours.size} horas activas", Icons.Default.DateRange) { showEditHours = true }

                Spacer(modifier = Modifier.height(40.dp))
                Button(onClick = onClose, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)) {
                    Text("FINALIZAR EDICIÓN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showEditName) EditNameDialog("Nombre Comercial", name, true, { showEditName = false }) { viewModel.updateCompanyField("name", it) }
    if (showEditPhone) EditPhoneDialog(phone, viewModel.phonePrefixes, { showEditPhone = false }) { viewModel.updateAccountField("phone", it) }

    if (showEditAddress) {
        EditAddressDialog(address, { showEditAddress = false }) {
            viewModel.updateAccountField("address", it)
            viewModel.updateCompanyField("address", it)
        }
    }

    if (showEditDesc) EditDescriptionDialog(description, { showEditDesc = false }) { viewModel.updateCompanyField("description", it) }

    if (showEditHours) {
        EditHoursDialog(
            currentHours = workingHours,
            onDismiss = { showEditHours = false },
            onSave = { viewModel.updateWorkingHours(it) }
        )
    }
}