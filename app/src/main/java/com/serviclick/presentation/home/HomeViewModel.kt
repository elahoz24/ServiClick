package com.serviclick.presentation.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class HomeState { LOADING, NEEDS_CLIENT_INFO, NEEDS_COMPANY_INFO, DASHBOARD }

class HomeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(HomeState.LOADING)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearErrorMessage() { _errorMessage.value = null }

    private val _userEmail = MutableStateFlow(auth.currentUser?.email ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userRole = MutableStateFlow("")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _savedCity = MutableStateFlow("")
    val savedCity: StateFlow<String> = _savedCity.asStateFlow()

    private val _savedLanguage = MutableStateFlow("Español")
    val savedLanguage: StateFlow<String> = _savedLanguage.asStateFlow()

    val provinces = listOf("A Coruña", "Álava", "Albacete", "Alicante", "Almería", "Asturias", "Ávila", "Badajoz", "Baleares", "Barcelona", "Burgos", "Cáceres", "Cádiz", "Cantabria", "Castellón", "Ciudad Real", "Córdoba", "Cuenca", "Girona", "Granada", "Guadalajara", "Gipuzkoa", "Huelva", "Huesca", "Jaén", "La Rioja", "Las Palmas", "León", "Lérida", "Lugo", "Madrid", "Málaga", "Murcia", "Navarra", "Ourense", "Palencia", "Pontevedra", "Salamanca", "Segovia", "Sevilla", "Soria", "Tarragona", "Santa Cruz de Tenerife", "Teruel", "Toledo", "Valencia", "Valladolid", "Vizcaya", "Zamora", "Zaragoza", "Ceuta", "Melilla")
    val categories = listOf("Fontanería", "Electricidad", "Limpieza", "Reformas", "Cerrajería", "Pintura", "Carpintería", "Climatización", "Jardinería", "Mudanzas")
    val languages = listOf("Español", "English", "Français", "Deutsch")
    val phonePrefixes = listOf("+34 (ES)", "+44 (UK)", "+33 (FR)", "+1 (US)")

    private val _setupName = MutableStateFlow("")
    val setupName: StateFlow<String> = _setupName.asStateFlow()

    private val _setupPhonePrefix = MutableStateFlow("+34 (ES)")
    val setupPhonePrefix: StateFlow<String> = _setupPhonePrefix.asStateFlow()

    private val _setupPhone = MutableStateFlow("")
    val setupPhone: StateFlow<String> = _setupPhone.asStateFlow()

    private val _selectedCity = MutableStateFlow("")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    init { fetchUserProfile() }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val role = document.getString("role") ?: "cliente"
                val city = document.getString("city") ?: ""
                val company = document.getString("companyName") ?: ""
                val name = document.getString("name") ?: ""
                val phoneDoc = document.getString("phone") ?: ""

                _userRole.value = role
                _savedCity.value = city
                _savedLanguage.value = document.getString("language") ?: "Español"
                _userName.value = if (role == "empresa") company else name
                _userPhone.value = phoneDoc

                if (role == "cliente") {
                    _uiState.value = if (city.isEmpty() || name.isEmpty() || phoneDoc.isEmpty()) HomeState.NEEDS_CLIENT_INFO else HomeState.DASHBOARD
                } else if (role == "empresa") {
                    _uiState.value = if (company.isEmpty() || city.isEmpty() || phoneDoc.isEmpty()) HomeState.NEEDS_COMPANY_INFO else HomeState.DASHBOARD
                }
            } else {
                _uiState.value = HomeState.DASHBOARD
                _errorMessage.value = "Tu cuenta está corrupta (No hay datos de perfil). Por favor, ve a la pestaña de Perfil y Elimina la cuenta para volver a registrarte."
            }
        }.addOnFailureListener { exception ->
            _uiState.value = HomeState.DASHBOARD
            _errorMessage.value = "Error al conectar con el servidor: ${exception.message}"
        }
    }

    fun onSetupNameChanged(newName: String) {
        if (newName.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$")) && newName.length <= 40) {
            _setupName.value = newName
        }
    }

    fun onSetupCompanyNameChanged(newName: String) {
        if (newName.matches(Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s&'-]*$")) && newName.length <= 50) {
            _setupName.value = newName
        }
    }

    fun onSetupPhoneChanged(newPhone: String) {
        if (newPhone.all { it.isDigit() } && newPhone.length <= 15) {
            _setupPhone.value = newPhone
        }
    }

    fun onDescriptionChanged(newDesc: String) {
        if (newDesc.length <= 300) {
            _description.value = newDesc
        }
    }

    fun onSetupPhonePrefixChanged(newPrefix: String) { _setupPhonePrefix.value = newPrefix }
    fun onCityChanged(newCity: String) { _selectedCity.value = newCity }
    fun onCategoryChanged(newCategory: String) { _selectedCategory.value = newCategory }

    fun saveClientProfile() {
        val userId = auth.currentUser?.uid ?: return
        // VALIDACIÓN: Mínimo 9 dígitos para el teléfono
        if (_setupName.value.length >= 3 && _setupPhone.value.length >= 9 && _selectedCity.value.isNotEmpty()) {
            _uiState.value = HomeState.LOADING
            val cleanPrefix = _setupPhonePrefix.value.substringBefore(" ")
            val fullPhone = "$cleanPrefix ${_setupPhone.value.trim()}"

            db.collection("users").document(userId).update(mapOf("name" to _setupName.value.trim(), "phone" to fullPhone, "city" to _selectedCity.value))
                .addOnSuccessListener { fetchUserProfile() }
        } else {
            _errorMessage.value = "Por favor, rellena todos los campos correctamente."
        }
    }

    fun saveCompanyProfile() {
        val userId = auth.currentUser?.uid ?: return
        // VALIDACIÓN: Mínimo 9 dígitos para el teléfono
        if (_setupName.value.length >= 3 && _setupPhone.value.length >= 9 && _selectedCity.value.isNotEmpty() && _selectedCategory.value.isNotEmpty()) {
            _uiState.value = HomeState.LOADING
            val cleanPrefix = _setupPhonePrefix.value.substringBefore(" ")
            val fullPhone = "$cleanPrefix ${_setupPhone.value.trim()}"

            db.collection("users").document(userId).update(mapOf("companyName" to _setupName.value.trim(), "phone" to fullPhone, "city" to _selectedCity.value, "category" to _selectedCategory.value, "description" to _description.value.trim()))
                .addOnSuccessListener { fetchUserProfile() }
        } else {
            _errorMessage.value = "Por favor, revisa que todos los campos sean correctos y válidos."
        }
    }

    fun updateProfileField(field: String, value: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).update(field, value).addOnSuccessListener { fetchUserProfile() }
    }

    fun sendPasswordReset() { auth.currentUser?.email?.let { auth.sendPasswordResetEmail(it) } }

    fun deleteAccount(onComplete: () -> Unit) {
        val user = auth.currentUser
        val userId = user?.uid
        if (userId != null && user != null) {
            _uiState.value = HomeState.LOADING
            db.collection("users").document(userId).delete().addOnSuccessListener {
                user.delete().addOnSuccessListener {
                    onComplete()
                }.addOnFailureListener { exception ->
                    _uiState.value = HomeState.DASHBOARD
                    if (exception is FirebaseAuthRecentLoginRequiredException) {
                        _errorMessage.value = "Por seguridad, debes cerrar sesión y volver a entrar antes de eliminar tu cuenta permanentemente."
                    } else {
                        _errorMessage.value = "Error al eliminar la cuenta de Google: ${exception.message}"
                    }
                }
            }.addOnFailureListener {
                _uiState.value = HomeState.DASHBOARD
                _errorMessage.value = "Error al borrar los datos del servidor."
            }
        }
    }

    fun logout() { auth.signOut() }
}