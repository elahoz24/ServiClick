package com.serviclick.presentation.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class HomeState {
    LOADING,
    NEEDS_CITY,           // Para Clientes nuevos
    NEEDS_COMPANY_INFO,   // Para Empresas nuevas
    DASHBOARD             // Para todos los que ya están listos
}

class HomeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(HomeState.LOADING)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    // --- DATOS GLOBALES ---
    private val _userRole = MutableStateFlow("")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _savedCity = MutableStateFlow("")
    val savedCity: StateFlow<String> = _savedCity.asStateFlow()

    // --- FORMULARIO COMÚN (CIUDAD) ---
    private val _selectedCity = MutableStateFlow("")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    // --- FORMULARIO EMPRESA ---
    private val _companyName = MutableStateFlow("")
    val companyName: StateFlow<String> = _companyName.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    // LISTAS DESPLEGABLES
    val provinces = listOf(
        "A Coruña", "Álava", "Albacete", "Alicante", "Almería", "Asturias", "Ávila", "Badajoz", "Baleares", "Barcelona",
        "Burgos", "Cáceres", "Cádiz", "Cantabria", "Castellón", "Ciudad Real", "Córdoba", "Cuenca", "Girona", "Granada",
        "Guadalajara", "Gipuzkoa", "Huelva", "Huesca", "Jaén", "La Rioja", "Las Palmas", "León", "Lérida", "Lugo",
        "Madrid", "Málaga", "Murcia", "Navarra", "Ourense", "Palencia", "Pontevedra", "Salamanca", "Segovia", "Sevilla",
        "Soria", "Tarragona", "Santa Cruz de Tenerife", "Teruel", "Toledo", "Valencia", "Valladolid", "Vizcaya", "Zamora", "Zaragoza",
        "Ceuta", "Melilla"
    )

    val categories = listOf(
        "Fontanería", "Electricidad", "Limpieza", "Reformas", "Cerrajería",
        "Pintura", "Carpintería", "Climatización", "Jardinería", "Mudanzas"
    )

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role") ?: "cliente"
                    val city = document.getString("city")
                    val company = document.getString("companyName")

                    _userRole.value = role

                    if (role == "cliente") {
                        if (city.isNullOrEmpty()) {
                            _uiState.value = HomeState.NEEDS_CITY
                        } else {
                            _savedCity.value = city
                            _uiState.value = HomeState.DASHBOARD
                        }
                    } else if (role == "empresa") {
                        // Si es empresa y no tiene nombre, le falta configurar el perfil
                        if (company.isNullOrEmpty()) {
                            _uiState.value = HomeState.NEEDS_COMPANY_INFO
                        } else {
                            _savedCity.value = city ?: ""
                            _uiState.value = HomeState.DASHBOARD
                        }
                    }
                }
            }
    }

    // Funciones para actualizar los textos desde la vista
    fun onCityChanged(newCity: String) { _selectedCity.value = newCity }
    fun onCompanyNameChanged(newName: String) { _companyName.value = newName }
    fun onPhoneChanged(newPhone: String) { _phone.value = newPhone }
    fun onCategoryChanged(newCategory: String) { _selectedCategory.value = newCategory }
    fun onDescriptionChanged(newDesc: String) { _description.value = newDesc }

    fun saveClientCity() {
        val userId = auth.currentUser?.uid ?: return
        if (_selectedCity.value.isNotEmpty()) {
            _uiState.value = HomeState.LOADING
            db.collection("users").document(userId)
                .update("city", _selectedCity.value)
                .addOnSuccessListener {
                    _savedCity.value = _selectedCity.value
                    _uiState.value = HomeState.DASHBOARD
                }
        }
    }

    fun saveCompanyProfile() {
        val userId = auth.currentUser?.uid ?: return

        // Validación básica
        if (_companyName.value.isNotEmpty() && _selectedCity.value.isNotEmpty() && _selectedCategory.value.isNotEmpty()) {
            _uiState.value = HomeState.LOADING

            val updates = mapOf(
                "companyName" to _companyName.value.trim(),
                "phone" to _phone.value.trim(),
                "city" to _selectedCity.value,
                "category" to _selectedCategory.value,
                "description" to _description.value.trim()
            )

            db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener {
                    _savedCity.value = _selectedCity.value
                    _uiState.value = HomeState.DASHBOARD
                }
        }
    }

    fun logout() {
        auth.signOut()
    }
}