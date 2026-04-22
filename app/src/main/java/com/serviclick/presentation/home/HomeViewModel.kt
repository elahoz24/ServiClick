package com.serviclick.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serviclick.domain.model.CompanyProfile
import com.serviclick.domain.repository.UserRepository
import com.serviclick.domain.use_case.GetCompaniesUseCase
import com.serviclick.domain.use_case.GetUserProfileUseCase
import com.serviclick.domain.use_case.UpdateProfileUseCase
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
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState.LOADING)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _userEmail = MutableStateFlow(repository.getCurrentUserEmail())
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

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

    private val _savedCategory = MutableStateFlow("")
    val savedCategory: StateFlow<String> = _savedCategory.asStateFlow()

    private val _savedDescription = MutableStateFlow("")
    val savedDescription: StateFlow<String> = _savedDescription.asStateFlow()

    private val _profileImage = MutableStateFlow("")
    val profileImage: StateFlow<String> = _profileImage.asStateFlow()

    private val _bannerImage = MutableStateFlow("")
    val bannerImage: StateFlow<String> = _bannerImage.asStateFlow()

    private val _savedLanguage = MutableStateFlow("Español")
    val savedLanguage: StateFlow<String> = _savedLanguage.asStateFlow()

    private val _companiesList = MutableStateFlow<List<CompanyProfile>>(emptyList())
    val companiesList: StateFlow<List<CompanyProfile>> = _companiesList.asStateFlow()

    private val _isLoadingCompanies = MutableStateFlow(false)
    val isLoadingCompanies: StateFlow<Boolean> = _isLoadingCompanies.asStateFlow()

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

    init {
        fetchUserProfile()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            getUserProfileUseCase().onSuccess { profile ->
                _userRole.value = profile.role
                _savedCity.value = profile.city
                _savedAddress.value = profile.address
                _savedLanguage.value = profile.language
                _userName.value = if (profile.role == "empresa") profile.companyName else profile.name
                _userPhone.value = profile.phone
                _savedCategory.value = profile.category
                _savedDescription.value = profile.description
                _profileImage.value = profile.profileImage
                _bannerImage.value = profile.bannerImage

                if (profile.role == "cliente") {
                    if (profile.city.isEmpty() || profile.name.isEmpty() || profile.phone.isEmpty()) {
                        _uiState.value = HomeState.NEEDS_CLIENT_INFO
                    } else {
                        _uiState.value = HomeState.DASHBOARD
                        fetchCompaniesInMyCity(profile.city)
                    }
                } else {
                    if (profile.companyName.isEmpty() || profile.city.isEmpty()) {
                        _uiState.value = HomeState.NEEDS_COMPANY_INFO
                    } else {
                        _uiState.value = HomeState.DASHBOARD
                    }
                }
            }.onFailure {
                _errorMessage.value = it.message ?: "Error al cargar el perfil"
                _uiState.value = HomeState.DASHBOARD
            }
        }
    }

    fun fetchCompaniesInMyCity(city: String) {
        viewModelScope.launch {
            _isLoadingCompanies.value = true
            getCompaniesUseCase(city).onSuccess { list ->
                _companiesList.value = list
                _isLoadingCompanies.value = false
            }.onFailure {
                _isLoadingCompanies.value = false
                _errorMessage.value = "Error al buscar profesionales en $city"
            }
        }
    }

    fun updateProfileField(field: String, value: String) {
        viewModelScope.launch {
            updateProfileUseCase.updateField(field, value).onSuccess {
                fetchUserProfile()
            }.onFailure {
                _errorMessage.value = "No se pudo actualizar el campo $field"
            }
        }
    }

    fun saveClientProfile() {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            val fullPhone = "${_setupPhonePrefix.value.substringBefore(" ")} ${_setupPhone.value.trim()}"
            val data = mapOf(
                "name" to _setupName.value.trim(),
                "phone" to fullPhone,
                "city" to _selectedCity.value
            )
            updateProfileUseCase.updateMultiple(data).onSuccess {
                fetchUserProfile()
            }.onFailure {
                _uiState.value = HomeState.NEEDS_CLIENT_INFO
                _errorMessage.value = "Error al guardar el perfil: ${it.message}"
            }
        }
    }

    fun saveCompanyProfile() {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            val fullPhone = "${_setupPhonePrefix.value.substringBefore(" ")} ${_setupPhone.value.trim()}"
            val data = mapOf(
                "companyName" to _setupName.value.trim(),
                "phone" to fullPhone,
                "city" to _selectedCity.value,
                "category" to _selectedCategory.value,
                "description" to _description.value.trim()
            )
            updateProfileUseCase.updateMultiple(data).onSuccess {
                fetchUserProfile()
            }.onFailure {
                _uiState.value = HomeState.NEEDS_COMPANY_INFO
                _errorMessage.value = "Error al guardar el perfil de empresa: ${it.message}"
            }
        }
    }

    fun onSetupNameChanged(v: String) {
        if (v.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$")) && v.length <= 40) {
            _setupName.value = v
        }
    }

    fun onSetupCompanyNameChanged(v: String) {
        if (v.matches(Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s&'-]*$")) && v.length <= 50) {
            _setupName.value = v
        }
    }

    fun onSetupPhoneChanged(v: String) {
        if (v.all { it.isDigit() } && v.length <= 15) {
            _setupPhone.value = v
        }
    }

    fun onCityChanged(v: String) {
        _selectedCity.value = v
    }

    fun onCategoryChanged(v: String) {
        _selectedCategory.value = v
    }

    fun onDescriptionChanged(v: String) {
        if (v.length <= 300) {
            _description.value = v
        }
    }

    fun onSetupPhonePrefixChanged(v: String) {
        _setupPhonePrefix.value = v
    }

    fun logout() {
        repository.logout()
    }

    fun sendPasswordReset() {
        repository.sendPasswordReset()
    }

    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            repository.deleteAccount().onSuccess {
                onComplete()
            }.onFailure {
                _uiState.value = HomeState.DASHBOARD
                _errorMessage.value = it.message
            }
        }
    }
}