package com.serviclick.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.serviclick.presentation.home.components.*
import com.serviclick.ui.theme.*

data class BottomNavItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCompany: (String) -> Unit, // <-- AÑADIDO AQUI
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearErrorMessage() },
            title = { Text("Aviso", color = ForestGreen, fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage!!, color = ForestGreen) },
            confirmButton = { TextButton(onClick = { viewModel.clearErrorMessage() }) { Text("OK", color = SunsetOrange, fontWeight = FontWeight.Bold) } },
            containerColor = BeigeSurface
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(CreamBackground), contentAlignment = Alignment.Center) {
        when (uiState) {
            HomeState.LOADING -> CircularProgressIndicator(color = SunsetOrange)
            HomeState.NEEDS_CLIENT_INFO -> ClientSetupSection(viewModel)
            HomeState.NEEDS_COMPANY_INFO -> CompanySetupSection(viewModel)
            HomeState.DASHBOARD -> DashboardSection(viewModel, onNavigateToCompany, onLogout) // <-- PASADO AQUI
        }
    }
}

@Composable
fun DashboardSection(viewModel: HomeViewModel, onNavigateToCompany: (String) -> Unit, onLogout: () -> Unit) {
    val role by viewModel.userRole.collectAsState()
    var currentTab by remember { mutableStateOf(0) }

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

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = BeigeSurface) {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
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
        Box(modifier = Modifier.padding(padding)) {
            when (currentTab) {
                0 -> {
                    if (role == "cliente") ClientHomeTab(viewModel, onNavigateToCompany)
                    else CompanyRequestsTab(viewModel)
                }
                1 -> {
                    if (role == "cliente") ClientAppointmentsTab(viewModel)
                    else PlaceholderScreen("Agenda")
                }
                2 -> {
                    PlaceholderScreen("Mensajes")
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

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = ForestGreen)
    }
}