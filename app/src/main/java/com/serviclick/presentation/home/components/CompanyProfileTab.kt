package com.serviclick.presentation.home.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
    val name by viewModel.userName.collectAsState()
    val city by viewModel.savedCity.collectAsState()
    val category by viewModel.savedCategory.collectAsState()
    val description by viewModel.savedDescription.collectAsState()
    val profileImage by viewModel.profileImage.collectAsState()
    val bannerImage by viewModel.bannerImage.collectAsState()
    var showEdit by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(BeigeSurface)) {
                if (bannerImage.isNotEmpty()) Base64Image(bannerImage, Modifier.fillMaxSize())
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Surface(modifier = Modifier.size(130.dp).offset(y = (-65).dp), shape = CircleShape, color = CreamBackground, shadowElevation = 8.dp) {
                    if (profileImage.isNotEmpty()) Base64Image(profileImage, Modifier.fillMaxSize().clip(CircleShape))
                    else Icon(Icons.Default.Person, null, modifier = Modifier.padding(32.dp), tint = SunsetOrange)
                }
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-40).dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(name, style = MaterialTheme.typography.headlineMedium, color = ForestGreen, fontWeight = FontWeight.Bold)
                Text(category, color = SunsetOrange, fontWeight = FontWeight.Bold)
                Row { Icon(Icons.Default.LocationOn, null, tint = ForestGreen.copy(0.6f)); Text(city, color = ForestGreen.copy(0.6f)) }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Sobre nosotros", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Text(description, modifier = Modifier.align(Alignment.Start))
            }
        }
        Button(onClick = { showEdit = true }, modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)) {
            Text("EDITAR PERFIL", fontWeight = FontWeight.Bold)
        }
    }
    if (showEdit) EditProfileFullScreen(viewModel) { showEdit = false }
}

@Composable
fun EditProfileFullScreen(viewModel: HomeViewModel, onClose: () -> Unit) {
    val name by viewModel.userName.collectAsState()
    val phone by viewModel.userPhone.collectAsState()
    val city by viewModel.savedCity.collectAsState()
    val category by viewModel.savedCategory.collectAsState()
    val description by viewModel.savedDescription.collectAsState()
    val profileImage by viewModel.profileImage.collectAsState()
    val bannerImage by viewModel.bannerImage.collectAsState()
    val context = LocalContext.current
    val pLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { it?.let { uriToBase64(context, it, 400f)?.let { viewModel.updateProfileField("profileImage", it) } } }
    val bLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { it?.let { uriToBase64(context, it, 800f)?.let { viewModel.updateProfileField("bannerImage", it) } } }

    var showEditName by remember { mutableStateOf(false) }
    var showEditPhone by remember { mutableStateOf(false) }
    var showEditDesc by remember { mutableStateOf(false) }
    var expandedCity by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(modifier = Modifier.fillMaxSize().background(CreamBackground).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(BeigeSurface).clickable { bLauncher.launch("image/*") }) {
                if (bannerImage.isNotEmpty()) Base64Image(bannerImage, Modifier.fillMaxSize())
                Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.align(Alignment.Center), tint = CreamBackground)
                IconButton(onClick = onClose) { Icon(Icons.Default.ArrowBack, null, tint = CreamBackground) }
            }
            Box(modifier = Modifier.fillMaxWidth().offset(y = (-50).dp), contentAlignment = Alignment.TopCenter) {
                Surface(modifier = Modifier.size(100.dp).clickable { pLauncher.launch("image/*") }, shape = CircleShape, color = SunsetOrange.copy(0.2f)) {
                    if (profileImage.isNotEmpty()) Base64Image(profileImage, Modifier.fillMaxSize().clip(CircleShape))
                    else Icon(Icons.Default.Person, null, modifier = Modifier.padding(24.dp), tint = SunsetOrange)
                }
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-20).dp)) {
                SectionTitle("DATOS")
                SettingsItem("Empresa", name, Icons.Default.Storefront) { showEditName = true }
                SettingsItem("Teléfono", phone, Icons.Default.Phone) { showEditPhone = true }
                Box {
                    SettingsItem("Ciudad", city, Icons.Default.LocationOn) { expandedCity = true }
                    DropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }) {
                        viewModel.provinces.forEach { p -> DropdownMenuItem(text = { Text(p) }, onClick = { viewModel.updateProfileField("city", p); expandedCity = false }) }
                    }
                }
                Box {
                    SettingsItem("Categoría", category, Icons.Default.Build) { expandedCat = true }
                    DropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                        viewModel.categories.forEach { c -> DropdownMenuItem(text = { Text(c) }, onClick = { viewModel.updateProfileField("category", c); expandedCat = false }) }
                    }
                }
                SettingsItem("Descripción", if(description.length > 20) description.take(20)+"..." else description, Icons.Default.Edit) { showEditDesc = true }
                Spacer(modifier = Modifier.height(40.dp))
                Button(onClick = onClose, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)) { Text("CONFIRMAR") }
            }
        }
    }
    if (showEditName) EditNameDialog("Editar Empresa", name, true, { showEditName = false }) { viewModel.updateProfileField("companyName", it) }
    if (showEditPhone) EditPhoneDialog(phone, viewModel.phonePrefixes, { showEditPhone = false }) { viewModel.updateProfileField("phone", it) }
    if (showEditDesc) EditDescriptionDialog(description, { showEditDesc = false }) { viewModel.updateProfileField("description", it) }
}