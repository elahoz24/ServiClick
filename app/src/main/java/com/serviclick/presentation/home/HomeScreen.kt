package com.serviclick.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.serviclick.R
import com.serviclick.presentation.home.components.ClientAccountTab
import com.serviclick.presentation.home.components.ClientAppointmentsTab
import com.serviclick.presentation.home.components.ClientHomeTab
import com.serviclick.presentation.home.components.ClientSetupSection
import com.serviclick.presentation.home.components.CompanyAgendaTab
import com.serviclick.presentation.home.components.CompanyProfileTab
import com.serviclick.presentation.home.components.CompanyRequestsTab
import com.serviclick.presentation.home.components.CompanySetupSection
import com.serviclick.presentation.home.components.MessagesTab
import com.serviclick.presentation.home.components.SettingsTab
import com.serviclick.ui.theme.BeigeSurface
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ForestGreen
import com.serviclick.ui.theme.SunsetOrange

data class BottomNavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

/**
 * Pantalla raíz (Host) que rige toda la sesión iniciada.
 * Aloja el Dashboard o los formularios de configuración inicial en caso de cuentas nuevas.
 * Observa el `uiState` (Máquina de estados de HomeViewModel) y monta el componente
 * adecuado dinámicamente: Loading -> Setup -> Dashboard.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCompany: (String) -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearErrorMessage() },
            title = { Text("Aviso", color = ForestGreen, fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage!!, color = ForestGreen) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearErrorMessage() }) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground),
        contentAlignment = Alignment.Center
    ) {
        // Renderizado condicional basado en el rol y el estado del perfil
        when (uiState) {
            HomeState.LOADING -> CircularProgressIndicator(color = SunsetOrange)
            HomeState.NEEDS_CLIENT_INFO -> ClientSetupSection(viewModel)
            HomeState.NEEDS_COMPANY_INFO -> CompanySetupSection(viewModel)
            HomeState.DASHBOARD -> DashboardSection(viewModel, onNavigateToCompany, onLogout)
        }
    }
}

/**
 * Contenedor del Dashboard principal con Bottom Navigation Bar.
 * Muestra los sub-apartados y pestañas según si eres "cliente" o "empresa".
 */
@Composable
fun DashboardSection(
    viewModel: HomeViewModel,
    onNavigateToCompany: (String) -> Unit,
    onLogout: () -> Unit
) {
    val role by viewModel.userRole.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val city by viewModel.savedCity.collectAsState()

    BackHandler(enabled = currentTab != 0) {
        viewModel.setTab(0)
    }

    // Configuración condicional de las pestañas inferiores
    val tabs = if (role == "cliente") {
        listOf(
            BottomNavItem("Inicio", Icons.Default.Home),
            BottomNavItem("Citas", Icons.Default.DateRange),
            BottomNavItem("Mensajes", Icons.Default.Email),
            BottomNavItem("Mi Cuenta", Icons.Default.Person)
        )
    } else {
        listOf(
            BottomNavItem("Solicitudes", Icons.Default.List),
            BottomNavItem("Agenda", Icons.Default.DateRange),
            BottomNavItem("Mensajes", Icons.Default.Email),
            BottomNavItem("Negocio", Icons.Default.Storefront),
            BottomNavItem("Ajustes", Icons.Default.Settings)
        )
    }

    // Textos dinámicos en el TopAppBar basados en la selección
    val (headerSubtitle, headerTitle) = if (role == "cliente") {
        when (currentTab) {
            0 -> "Servicios en" to city.ifEmpty { "Tu ciudad" }
            1 -> "Gestión de" to "Tus Citas"
            2 -> "Bandeja de" to "Mensajes"
            3 -> "Ajustes de" to "Mi Cuenta"
            else -> "App" to "ServiClick"
        }
    } else {
        when (currentTab) {
            0 -> "Nuevas" to "Solicitudes"
            1 -> "Calendario de" to "Tu Agenda"
            2 -> "Bandeja de" to "Mensajes"
            3 -> "Perfil de" to "Tu Negocio"
            4 -> "Configuración de" to "Ajustes"
            else -> "App" to "ServiClick"
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BeigeSurface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            headerSubtitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = ForestGreen.copy(0.6f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentTab == 0 && role == "cliente") {
                                Icon(
                                    Icons.Default.LocationOn,
                                    null,
                                    tint = SunsetOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                headerTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = ForestGreen,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo_sin_texto),
                        contentDescription = "ServiClick Logo",
                        modifier = Modifier.size(46.dp)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = BeigeSurface) {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { viewModel.setTab(index) },
                        icon = { Icon(item.icon, null) },
                        label = {
                            Text(
                                item.title,
                                maxLines = 1,
                                overflow = TextOverflow.Visible,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                            )
                        },
                        alwaysShowLabel = currentTab == index,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CreamBackground,
                            indicatorColor = SunsetOrange
                        )
                    )
                }
            }
        }
    ) { padding ->
        // Contenedor principal que carga el Composable específico correspondiente a la pestaña
        Box(
            modifier = Modifier
                .padding(padding)
                .background(CreamBackground)
        ) {
            when (currentTab) {
                0 -> {
                    if (role == "cliente") ClientHomeTab(viewModel, onNavigateToCompany)
                    else CompanyRequestsTab(viewModel)
                }

                1 -> {
                    if (role == "cliente") ClientAppointmentsTab(viewModel)
                    else CompanyAgendaTab(viewModel)
                }

                2 -> {
                    MessagesTab(viewModel = viewModel)
                }

                3 -> {
                    if (role == "cliente") ClientAccountTab(viewModel, onLogout)
                    else CompanyProfileTab(viewModel)
                }

                4 -> {
                    if (role == "empresa") SettingsTab(viewModel, onLogout)
                    else PlaceholderScreen("")
                }
            }
        }
    }
}

/** Pantalla de relleno (Fallback). */
@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
    }
}