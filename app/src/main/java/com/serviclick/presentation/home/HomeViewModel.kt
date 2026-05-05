package com.serviclick.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serviclick.domain.model.Appointment
import com.serviclick.domain.model.CompanyProfile
import com.serviclick.domain.repository.UserRepository
import com.serviclick.domain.use_case.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HomeState { LOADING, NEEDS_CLIENT_INFO, NEEDS_COMPANY_INFO, DASHBOARD }

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val getCompanyByIdUseCase: GetCompanyByIdUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updateCompanyProfileUseCase: UpdateCompanyProfileUseCase,
    private val getCompanyAppointmentsUseCase: GetCompanyAppointmentsUseCase, // Inyectado para citas de la Empresa
    private val getClientAppointmentsUseCase: GetClientAppointmentsUseCase,   // Inyectado para citas del Cliente
    private val respondToAppointmentUseCase: RespondToAppointmentUseCase,     // Inyectado para dar presupuestos
    private val updateAppointmentStatusUseCase: UpdateAppointmentStatusUseCase, // Inyectado para aceptar/rechazar
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState.LOADING)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _userEmail = MutableStateFlow(repository.getCurrentUserEmail())
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _userRole = MutableStateFlow("")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _savedCity = MutableStateFlow("")
    val savedCity: StateFlow<String> = _savedCity.asStateFlow()

    private val _savedAddress = MutableStateFlow("")
    val savedAddress: StateFlow<String> = _savedAddress.asStateFlow()

    private val _profileImage = MutableStateFlow("")
    val profileImage: StateFlow<String> = _profileImage.asStateFlow()

    private val _savedLanguage = MutableStateFlow("Español")
    val savedLanguage: StateFlow<String> = _savedLanguage.asStateFlow()

    private val _companyName = MutableStateFlow("")
    val companyName: StateFlow<String> = _companyName.asStateFlow()

    private val _savedCategory = MutableStateFlow("")
    val savedCategory: StateFlow<String> = _savedCategory.asStateFlow()

    private val _savedDescription = MutableStateFlow("")
    val savedDescription: StateFlow<String> = _savedDescription.asStateFlow()

    private val _bannerImage = MutableStateFlow("")
    val bannerImage: StateFlow<String> = _bannerImage.asStateFlow()

    private val _rating = MutableStateFlow(0.0)
    val rating: StateFlow<Double> = _rating.asStateFlow()

    private val _reviewCount = MutableStateFlow(0)
    val reviewCount: StateFlow<Int> = _reviewCount.asStateFlow()

    private val _workingHours = MutableStateFlow<List<String>>(emptyList())
    val workingHours: StateFlow<List<String>> = _workingHours.asStateFlow()

    private val _companiesList = MutableStateFlow<List<CompanyProfile>>(emptyList())
    val companiesList: StateFlow<List<CompanyProfile>> = _companiesList.asStateFlow()

    private val _isLoadingCompanies = MutableStateFlow(false)
    val isLoadingCompanies: StateFlow<Boolean> = _isLoadingCompanies.asStateFlow()

    // === EL CORAZÓN DE LAS CITAS (AQUÍ ESTABA EL ERROR DE TIPO) ===
    private val _companyAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val companyAppointments: StateFlow<List<Appointment>> = _companyAppointments.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

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

    private val _setupAddress = MutableStateFlow("")
    val setupAddress: StateFlow<String> = _setupAddress.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    init {
        fetchUserProfile(showLoading = true)
    }

    fun clearErrorMessage() { _errorMessage.value = null }

    private fun fetchUserProfile(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _uiState.value = HomeState.LOADING
            getUserProfileUseCase().onSuccess { user ->
                _userId.value = user.id
                _userRole.value = user.role
                _userName.value = user.name
                _userPhone.value = user.phone
                _savedCity.value = user.city
                _savedAddress.value = user.address
                _profileImage.value = user.profileImage
                _savedLanguage.value = user.language

                if (user.role == "cliente") {
                    if (user.city.isEmpty() || user.name.isEmpty()) {
                        _uiState.value = HomeState.NEEDS_CLIENT_INFO
                    } else {
                        _uiState.value = HomeState.DASHBOARD
                        fetchCompaniesInMyCity(user.city)
                        fetchClientAppointments()
                    }
                } else {
                    getCompanyByIdUseCase(user.id).onSuccess { comp ->
                        _companyName.value = comp.name
                        _savedCategory.value = comp.category
                        _savedDescription.value = comp.description
                        _bannerImage.value = comp.bannerImage
                        _rating.value = comp.rating
                        _reviewCount.value = comp.reviewCount
                        _workingHours.value = comp.workingHours
                        if (comp.profileImage.isNotEmpty()) _profileImage.value = comp.profileImage
                    }

                    if (_companyName.value.isEmpty() || user.city.isEmpty()) {
                        _uiState.value = HomeState.NEEDS_COMPANY_INFO
                    } else {
                        _uiState.value = HomeState.DASHBOARD
                        fetchCompanyAppointments()
                    }
                }
            }.onFailure {
                _errorMessage.value = it.message
                _uiState.value = HomeState.DASHBOARD
            }
        }
    }

    // === FUNCIONES DE CITAS Y PRESUPUESTOS ===
    fun fetchCompanyAppointments() {
        val id = _userId.value
        if (id.isEmpty()) return
        viewModelScope.launch {
            getCompanyAppointmentsUseCase(id).onSuccess { _companyAppointments.value = it }
        }
    }

    fun fetchClientAppointments() {
        val id = _userId.value
        if (id.isEmpty()) return
        viewModelScope.launch {
            getClientAppointmentsUseCase(id).onSuccess { _appointments.value = it }
        }
    }

    fun respondToRequest(appointmentId: String, price: Double, status: String) {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            respondToAppointmentUseCase(appointmentId, price, status).onSuccess {
                fetchCompanyAppointments()
                _uiState.value = HomeState.DASHBOARD
            }.onFailure {
                _errorMessage.value = "Error al responder: ${it.message}"
                _uiState.value = HomeState.DASHBOARD
            }
        }
    }

    fun updateAppointmentStatus(appointmentId: String, status: String) {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            updateAppointmentStatusUseCase(appointmentId, status).onSuccess {
                if (_userRole.value == "cliente") fetchClientAppointments() else fetchCompanyAppointments()
                _uiState.value = HomeState.DASHBOARD
            }.onFailure {
                _errorMessage.value = it.message
                _uiState.value = HomeState.DASHBOARD
            }
        }
    }

    // === FUNCIONES DE EMPRESA Y CONFIGURACIÓN ===
    fun fetchCompaniesInMyCity(city: String) {
        viewModelScope.launch {
            _isLoadingCompanies.value = true
            getCompaniesUseCase(city).onSuccess {
                _companiesList.value = it
                _isLoadingCompanies.value = false
            }.onFailure { _isLoadingCompanies.value = false }
        }
    }

    fun updateAccountField(field: String, value: String) {
        viewModelScope.launch {
            updateProfileUseCase.updateField(field, value).onSuccess { fetchUserProfile(showLoading = false) }
        }
    }

    fun updateCompanyField(field: String, value: String) {
        viewModelScope.launch {
            updateCompanyProfileUseCase.updateField(field, value).onSuccess { fetchUserProfile(showLoading = false) }
        }
    }

    fun updateWorkingHours(newList: List<String>) {
        viewModelScope.launch {
            updateCompanyProfileUseCase.updateField("workingHours", newList).onSuccess {
                fetchUserProfile(showLoading = false)
            }
        }
    }

    fun saveClientProfile() {
        val phoneNum = _setupPhone.value.replace(" ", "")
        if (_setupName.value.trim().isEmpty() || phoneNum.length < 9 || _selectedCity.value.isEmpty() || _setupAddress.value.trim().isEmpty()) {
            _errorMessage.value = "Por favor, rellena todos los campos. El teléfono debe tener al menos 9 números."
            return
        }

        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            val fullPhone = "${_setupPhonePrefix.value.substringBefore(" ")} ${_setupPhone.value.trim()}"
            val data = mapOf(
                "name" to _setupName.value.trim(),
                "phone" to fullPhone,
                "city" to _selectedCity.value,
                "address" to _setupAddress.value.trim()
            )
            updateProfileUseCase.updateAccount(data).onSuccess {
                fetchUserProfile(showLoading = false)
            }.onFailure {
                _uiState.value = HomeState.NEEDS_CLIENT_INFO
                _errorMessage.value = it.message
            }
        }
    }

    fun saveCompanyProfile() {
        val phoneNum = _setupPhone.value.replace(" ", "")
        if (_setupName.value.trim().isEmpty() || phoneNum.length < 9 || _selectedCity.value.isEmpty() || _selectedCategory.value.isEmpty() || _description.value.trim().isEmpty() || _setupAddress.value.trim().isEmpty()) {
            _errorMessage.value = "Por favor, rellena todos los datos correctamente. El teléfono debe tener al menos 9 números."
            return
        }

        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            val fullPhone = "${_setupPhonePrefix.value.substringBefore(" ")} ${_setupPhone.value.trim()}"

            updateProfileUseCase.updateAccount(mapOf(
                "phone" to fullPhone,
                "city" to _selectedCity.value,
                "address" to _setupAddress.value.trim()
            ))

            val compData = mapOf(
                "name" to _setupName.value.trim(),
                "category" to _selectedCategory.value,
                "description" to _description.value.trim(),
                "city" to _selectedCity.value,
                "address" to _setupAddress.value.trim(),
                "profileImage" to "",
                "bannerImage" to "",
                "rating" to 0.0,
                "reviewCount" to 0,
                "workingHours" to listOf(
                    "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                    "12:00", "12:30", "13:00", "16:00", "16:30", "17:00",
                    "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"
                )
            )

            updateCompanyProfileUseCase(compData).onSuccess {
                fetchUserProfile(showLoading = false)
            }.onFailure {
                _uiState.value = HomeState.NEEDS_COMPANY_INFO
                _errorMessage.value = it.message
            }
        }
    }

    fun onSetupNameChanged(v: String) {
        if (v.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$")) && v.length <= 40) _setupName.value = v
    }
    fun onSetupCompanyNameChanged(v: String) {
        if (v.matches(Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s&'-]*$")) && v.length <= 50) _setupName.value = v
    }
    fun onSetupPhoneChanged(v: String) {
        if (v.replace(" ", "").all { it.isDigit() } && v.length <= 15) _setupPhone.value = v
    }
    fun onCityChanged(v: String) { _selectedCity.value = v }
    fun onCategoryChanged(v: String) { _selectedCategory.value = v }
    fun onDescriptionChanged(v: String) { if (v.length <= 300) _description.value = v }
    fun onSetupPhonePrefixChanged(v: String) { _setupPhonePrefix.value = v }
    fun onSetupAddressChanged(v: String) { _setupAddress.value = v }

    fun logout() { repository.logout() }
    fun sendPasswordReset() { repository.sendPasswordReset() }
    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            repository.deleteAccount().onSuccess { onComplete() }
        }
    }
}