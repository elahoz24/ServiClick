package com.serviclick.presentation.home.components

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serviclick.domain.model.CompanyProfile
import com.serviclick.presentation.home.HomeViewModel
import com.serviclick.ui.theme.BeigeSurface
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ForestGreen
import com.serviclick.ui.theme.SunsetOrange

/**
 * Pestaña principal de Inicio para los clientes.
 * Muestra el catálogo de profesionales filtrado por ciudad, categoría y búsqueda por texto.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeTab(viewModel: HomeViewModel, onNavigateToCompany: (String) -> Unit) {
    val companies by viewModel.companiesList.collectAsState()
    val isLoading by viewModel.isLoadingCompanies.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("Todas") }
    val filterCategories = listOf("Todas") + viewModel.categories

    // --- LÓGICA DE BÚSQUEDA INTELIGENTE ---
    val filteredCompanies = companies.filter { company ->
        val matchesCategory =
            selectedCategoryFilter == "Todas" || company.category == selectedCategoryFilter

        val matchesSearch = if (searchQuery.isBlank()) {
            true
        } else {
            // Partimos la frase por espacios y nos quedamos solo con palabras de más de 2 letras
            val keywords = searchQuery.trim().split("\\s+".toRegex()).filter { it.length > 2 }

            if (keywords.isEmpty()) {
                // Si solo ha escrito palabras cortas, hacemos búsqueda exacta
                company.name.contains(searchQuery, ignoreCase = true) ||
                        company.description.contains(searchQuery, ignoreCase = true)
            } else {
                // Si ALGUNA palabra clave coincide en nombre, desc o categoría, la empresa aparece
                keywords.any { word ->
                    company.name.contains(word, ignoreCase = true) ||
                            company.description.contains(word, ignoreCase = true) ||
                            company.category.contains(word, ignoreCase = true)
                }
            }
        }

        matchesCategory && matchesSearch
    }.sortedByDescending { it.rating } // ORDENACIÓN POR MEJORES VALORACIONES

    Column(modifier = Modifier
        .fillMaxSize()
        .background(CreamBackground)) {

        // --- FILTROS DE CATEGORÍA ---
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 4.dp),
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
                    border = null, shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // --- BARRA DE BÚSQUEDA ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            placeholder = { Text("Buscar profesional, servicio, palabra...") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = SunsetOrange
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SunsetOrange,
                unfocusedBorderColor = BeigeSurface,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = BeigeSurface,
                focusedTextColor = ForestGreen,
                unfocusedTextColor = ForestGreen
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        // Gestión de estados de la UI: Cargando, Vacío o Lista
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = SunsetOrange) }
        } else if (filteredCompanies.isEmpty()) {
            EmptyCompaniesView()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 8.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredCompanies) { company ->
                    CompanyCard(company = company, onClick = { onNavigateToCompany(company.id) })
                }
            }
        }
    }
}

/**
 * Componente visual que representa a un profesional en el listado del cliente.
 */
@Composable
fun CompanyCard(company: CompanyProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(BeigeSurface)) {
                if (company.bannerImage.isNotEmpty()) Base64Image(
                    company.bannerImage,
                    Modifier.fillMaxSize()
                )
                Surface(
                    color = SunsetOrange,
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        company.category,
                        color = CreamBackground,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(54.dp)
                        .offset(y = (-25).dp),
                    shape = CircleShape,
                    color = CreamBackground,
                    shadowElevation = 4.dp
                ) {
                    if (company.profileImage.isNotEmpty()) Base64Image(
                        company.profileImage,
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                    else Icon(
                        Icons.Default.Person,
                        null,
                        tint = SunsetOrange,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier
                    .weight(1f)
                    .offset(y = (-5).dp)) {
                    Text(
                        company.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        company.description.ifEmpty { "Profesional de confianza en ServiClick." },
                        style = MaterialTheme.typography.bodySmall,
                        color = ForestGreen.copy(0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Vista de reemplazo (Fallback View) cuando la búsqueda no devuelve resultados.
 */
@Composable
fun EmptyCompaniesView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Build,
                null,
                tint = ForestGreen.copy(0.2f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No hay profesionales aquí",
                color = ForestGreen.copy(0.6f),
                fontWeight = FontWeight.Bold
            )
            Text(
                "Prueba con otra búsqueda o filtro",
                color = ForestGreen.copy(0.4f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}