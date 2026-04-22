package com.serviclick.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serviclick.domain.model.CompanyProfile
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeTab(viewModel: HomeViewModel) {
    val companies by viewModel.companiesList.collectAsState()
    val isLoading by viewModel.isLoadingCompanies.collectAsState()
    val city by viewModel.savedCity.collectAsState()

    // Estado para el filtro de categorías
    var selectedCategoryFilter by remember { mutableStateOf("Todas") }
    val filterCategories = listOf("Todas") + viewModel.categories

    val filteredCompanies = if (selectedCategoryFilter == "Todas") {
        companies
    } else {
        companies.filter { it.category == selectedCategoryFilter }
    }

    Column(modifier = Modifier.fillMaxSize().background(CreamBackground)) {

        // --- CABECERA ---
        Box(modifier = Modifier.fillMaxWidth().background(BeigeSurface).padding(24.dp)) {
            Column {
                Text("Servicios en", style = MaterialTheme.typography.labelMedium, color = ForestGreen.copy(0.6f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = SunsetOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(city.ifEmpty { "Tu ciudad" }, style = MaterialTheme.typography.titleLarge, color = ForestGreen, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // --- FILTROS (Chips) ---
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterCategories) { category ->
                FilterChip(
                    selected = selectedCategoryFilter == category,
                    onClick = { selectedCategoryFilter = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SunsetOrange,
                        selectedLabelColor = CreamBackground,
                        containerColor = BeigeSurface,
                        labelColor = ForestGreen
                    ),
                    border = null,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // --- LISTADO ---
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SunsetOrange)
            }
        } else if (filteredCompanies.isEmpty()) {
            EmptyCompaniesView()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredCompanies) { company ->
                    CompanyCard(company = company)
                }
            }
        }
    }
}

@Composable
fun CompanyCard(company: CompanyProfile) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { /* Navegar al detalle */ },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Banner
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(BeigeSurface)) {
                if (company.bannerImage.isNotEmpty()) {
                    Base64Image(company.bannerImage, Modifier.fillMaxSize())
                }
                Surface(
                    color = SunsetOrange,
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(company.category, color = CreamBackground, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            // Info Row
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(54.dp).offset(y = (-25).dp),
                    shape = CircleShape, color = CreamBackground, shadowElevation = 4.dp
                ) {
                    if (company.profileImage.isNotEmpty()) {
                        Base64Image(company.profileImage, Modifier.fillMaxSize().clip(CircleShape))
                    } else {
                        Icon(Icons.Default.Person, null, tint = SunsetOrange, modifier = Modifier.padding(10.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f).offset(y = (-5).dp)) {
                    Text(company.name, style = MaterialTheme.typography.titleMedium, color = ForestGreen, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(company.description.ifEmpty { "Profesional de confianza en ServiClick." }, style = MaterialTheme.typography.bodySmall, color = ForestGreen.copy(0.7f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun EmptyCompaniesView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Build, null, tint = ForestGreen.copy(0.2f), modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("No hay profesionales aquí", color = ForestGreen.copy(0.6f), fontWeight = FontWeight.Bold)
            Text("Intenta cambiar de categoría o ciudad", color = ForestGreen.copy(0.4f), style = MaterialTheme.typography.bodySmall)
        }
    }
}