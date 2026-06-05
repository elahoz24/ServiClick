package com.serviclick.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serviclick.data.repository.ReviewRepositoryImpl
import com.serviclick.domain.model.Appointment
import com.serviclick.domain.model.CompanyProfile
import com.serviclick.domain.model.Review
import com.serviclick.domain.repository.UserRepository
import com.serviclick.domain.use_case.GetClientAppointmentsUseCase
import com.serviclick.domain.use_case.GetCompaniesUseCase
import com.serviclick.domain.use_case.GetCompanyAppointmentsUseCase
import com.serviclick.domain.use_case.GetCompanyByIdUseCase
import com.serviclick.domain.use_case.GetUserProfileUseCase
import com.serviclick.domain.use_case.RespondToAppointmentUseCase
import com.serviclick.domain.use_case.UpdateAppointmentStatusUseCase
import com.serviclick.domain.use_case.UpdateCompanyProfileUseCase
import com.serviclick.domain.use_case.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Define los estados posibles en los que se encuentra la aplicación tras el inicio de sesión.
 */
enum class HomeState { LOADING, NEEDS_CLIENT_INFO, NEEDS_COMPANY_INFO, DASHBOARD }

/**
 * ViewModel principal que centraliza la lógica de negocio de la sesión iniciada.
 * Orquesta el flujo de datos entre las capas de dominio (Use Cases) y la UI (HomeScreen).
 * Utiliza `StateFlow` para emitir estados observables. Toda operación de red o base de datos
 * se ejecuta dentro de `viewModelScope`, asegurando que si el usuario cierra la pantalla, la corrutina
 * se cancele y no desperdiciemos recursos.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val getCompanyByIdUseCase: GetCompanyByIdUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updateCompanyProfileUseCase: UpdateCompanyProfileUseCase,
    private val getCompanyAppointmentsUseCase: GetCompanyAppointmentsUseCase,
    private val getClientAppointmentsUseCase: GetClientAppointmentsUseCase,
    private val respondToAppointmentUseCase: RespondToAppointmentUseCase,
    private val updateAppointmentStatusUseCase: UpdateAppointmentStatusUseCase,
    private val repository: UserRepository,
    private val reviewRepository: ReviewRepositoryImpl
) : ViewModel() {

    // MÁQUINA DE ESTADOS (UI STATE)
    private val _uiState = MutableStateFlow(HomeState.LOADING)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ESTADOS DE PERFIL
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

    private val _mockCard = MutableStateFlow("")
    val mockCard: StateFlow<String> = _mockCard.asStateFlow()

    // ESTADOS DE EMPRESA
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

    private val _acceptedPayments = MutableStateFlow<List<String>>(emptyList())
    val acceptedPayments: StateFlow<List<String>> = _acceptedPayments.asStateFlow()

    // Estados de agenda (Longs para compatibilidad total con Firebase)
    private val _blockedDates = MutableStateFlow<List<Long>>(emptyList())
    val blockedDates: StateFlow<List<Long>> = _blockedDates.asStateFlow()

    private val _blockedDaysOfWeek = MutableStateFlow<List<Long>>(emptyList())
    val blockedDaysOfWeek: StateFlow<List<Long>> = _blockedDaysOfWeek.asStateFlow()

    // COLECCIONES DE DATOS (LISTAS)
    private val _companiesList = MutableStateFlow<List<CompanyProfile>>(emptyList())
    val companiesList: StateFlow<List<CompanyProfile>> = _companiesList.asStateFlow()

    private val _isLoadingCompanies = MutableStateFlow(false)
    val isLoadingCompanies: StateFlow<Boolean> = _isLoadingCompanies.asStateFlow()

    private val _companyAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val companyAppointments: StateFlow<List<Appointment>> = _companyAppointments.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _myCompanyReviews = MutableStateFlow<List<Review>>(emptyList())
    val myCompanyReviews: StateFlow<List<Review>> = _myCompanyReviews.asStateFlow()

    val provinces = listOf(
        "A Coruña",
        "Álava",
        "Albacete",
        "Alicante",
        "Almería",
        "Asturias",
        "Ávila",
        "Badajoz",
        "Baleares",
        "Barcelona",
        "Burgos",
        "Cáceres",
        "Cádiz",
        "Cantabria",
        "Castellón",
        "Ciudad Real",
        "Córdoba",
        "Cuenca",
        "Girona",
        "Granada",
        "Guadalajara",
        "Gipuzkoa",
        "Huelva",
        "Huesca",
        "Jaén",
        "La Rioja",
        "Las Palmas",
        "León",
        "Lérida",
        "Lugo",
        "Madrid",
        "Málaga",
        "Murcia",
        "Navarra",
        "Ourense",
        "Palencia",
        "Pontevedra",
        "Salamanca",
        "Segovia",
        "Sevilla",
        "Soria",
        "Tarragona",
        "Santa Cruz de Tenerife",
        "Teruel",
        "Toledo",
        "Valencia",
        "Valladolid",
        "Vizcaya",
        "Zamora",
        "Zaragoza",
        "Ceuta",
        "Melilla"
    )
    val categories = listOf(
        "Fontanería",
        "Electricidad",
        "Limpieza",
        "Reformas",
        "Cerrajería",
        "Pintura",
        "Carpintería",
        "Climatización",
        "Jardinería",
        "Mudanzas"
    )
    val languages = listOf("Español", "English", "Français", "Deutsch")
    val phonePrefixes = listOf("+34 (ES)", "+44 (UK)", "+33 (FR)", "+1 (US)")

    // ESTADOS DE CONFIGURACIÓN (SETUP)
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

    // ESTADOS DE NAVEGACIÓN Y CHAT
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _activeChatAppointmentId = MutableStateFlow<String?>(null)
    val activeChatAppointmentId: StateFlow<String?> = _activeChatAppointmentId.asStateFlow()

    private val _activeChatSenderId = MutableStateFlow<String?>(null)
    val activeChatSenderId: StateFlow<String?> = _activeChatSenderId.asStateFlow()

    private val _activeChatIsClosed = MutableStateFlow(false)
    val activeChatIsClosed: StateFlow<Boolean> = _activeChatIsClosed.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var isUpdatingPayments = false
    private var isUpdatingCard = false

    init {
        fetchUserProfile(showLoading = true)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setTab(index: Int) {
        _currentTab.value = index
        if (index != 2) closeActiveChat()
    }

    fun openChatForAppointment(appointmentId: String, senderId: String, isClosed: Boolean = false) {
        _activeChatAppointmentId.value = appointmentId
        _activeChatSenderId.value = senderId
        _activeChatIsClosed.value = isClosed
        _currentTab.value = 2
    }

    fun closeActiveChat() {
        _activeChatAppointmentId.value = null
        _activeChatSenderId.value = null
        _activeChatIsClosed.value = false
        if (_userRole.value == "cliente") fetchClientAppointments() else fetchCompanyAppointments()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Ciclo de vida del usuario: Determina si necesita configurar el perfil o puede acceder al dashboard.
     */
    private fun fetchUserProfile(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _uiState.value = HomeState.LOADING
            repository.getUserProfile().onSuccess { user ->
                _userId.value = user.id
                _userRole.value = user.role
                _userName.value = user.name
                _userPhone.value = user.phone
                _savedCity.value = user.city
                _savedAddress.value = user.address
                _profileImage.value = user.profileImage
                _savedLanguage.value = user.language

                if (!isUpdatingCard) _mockCard.value = user.mockCard

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

                        _blockedDates.value = comp.blockedDates
                        _blockedDaysOfWeek.value = comp.blockedDaysOfWeek

                        if (!isUpdatingPayments) _acceptedPayments.value = comp.acceptedPayments

                        if (comp.profileImage.isNotEmpty()) _profileImage.value = comp.profileImage
                        fetchMyCompanyReviews(comp.id)
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

    private fun fetchMyCompanyReviews(companyId: String) {
        viewModelScope.launch {
            reviewRepository.getCompanyReviews(companyId)
                .collect { reviews -> _myCompanyReviews.value = reviews }
        }
    }

    /**
     * Mantenimiento Preventivo (Autofinalización): Convierte automáticamente las citas Aceptadas
     * que han superado su fecha/hora en "Finalizadas".
     */
    private suspend fun autoFinishPastAppointments(list: List<Appointment>): List<Appointment> {
        val currentTime = System.currentTimeMillis()
        val format = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        val dateOnly = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val updatedList = list.toMutableList()

        for (i in updatedList.indices) {
            val appt = updatedList[i]
            if (appt.status == "Aceptada") {
                val dateStr = dateOnly.format(java.util.Date(appt.startDateMillis))
                val exactTimeStr = "$dateStr ${appt.time}"
                try {
                    val exactMillis = format.parse(exactTimeStr)?.time ?: appt.startDateMillis
                    if (currentTime > exactMillis) {
                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("appointments").document(appt.id)
                            .update("status", "Finalizada")
                        updatedList[i] = appt.copy(status = "Finalizada")
                    }
                } catch (e: Exception) {
                    if (currentTime > appt.startDateMillis + 86400000) {
                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("appointments").document(appt.id)
                            .update("status", "Finalizada")
                        updatedList[i] = appt.copy(status = "Finalizada")
                    }
                }
            }
        }
        return updatedList
    }

    fun fetchCompanyAppointments() {
        val id = _userId.value
        if (id.isEmpty()) return
        viewModelScope.launch {
            getCompanyAppointmentsUseCase(id).onSuccess { list ->
                _companyAppointments.value = autoFinishPastAppointments(list)
            }
        }
    }

    fun fetchClientAppointments() {
        val id = _userId.value
        if (id.isEmpty()) return
        viewModelScope.launch {
            getClientAppointmentsUseCase(id).onSuccess { list ->
                _appointments.value = autoFinishPastAppointments(list)
            }
        }
    }

    fun respondToRequest(appointmentId: String, price: Double, status: String) {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            respondToAppointmentUseCase(appointmentId, price, status).onSuccess {
                fetchCompanyAppointments()
                _uiState.value = HomeState.DASHBOARD
            }.onFailure {
                _errorMessage.value = "Error al responder"
                _uiState.value = HomeState.DASHBOARD
            }
        }
    }

    fun updateAppointmentStatus(appointmentId: String, status: String, paymentMethod: String = "") {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            updateAppointmentStatusUseCase(appointmentId, status).onSuccess {
                if (paymentMethod.isNotEmpty()) {
                    try {
                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("appointments").document(appointmentId)
                            .update("paymentMethod", paymentMethod)
                    } catch (e: Exception) {
                    }
                }
                if (_userRole.value == "cliente") fetchClientAppointments() else fetchCompanyAppointments()
                _uiState.value = HomeState.DASHBOARD
            }.onFailure {
                _errorMessage.value = it.message
                _uiState.value = HomeState.DASHBOARD
            }
        }
    }

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
            updateProfileUseCase.updateField(field, value)
                .onSuccess { fetchUserProfile(showLoading = false) }
        }
    }

    fun updateCompanyField(field: String, value: Any) {
        viewModelScope.launch {
            updateCompanyProfileUseCase.updateField(field, value)
                .onSuccess { fetchUserProfile(showLoading = false) }
        }
    }

    fun updateAgenda(dates: List<Long>, days: List<Long>) {
        viewModelScope.launch {
            updateCompanyProfileUseCase.updateField("blockedDates", dates)
            updateCompanyProfileUseCase.updateField("blockedDaysOfWeek", days).onSuccess {
                _blockedDates.value = dates
                _blockedDaysOfWeek.value = days
            }
        }
    }

    fun updateAcceptedPayments(newList: List<String>) {
        val validList = if (newList.isEmpty()) listOf("Tarjeta") else newList
        isUpdatingPayments = true
        _acceptedPayments.value = validList
        viewModelScope.launch {
            updateCompanyProfileUseCase.updateField("acceptedPayments", validList)
                .onSuccess { isUpdatingPayments = false }.onFailure { isUpdatingPayments = false }
        }
    }

    fun updateMockCard(cardData: String) {
        isUpdatingCard = true
        _mockCard.value = cardData
        viewModelScope.launch {
            updateProfileUseCase.updateField("mockCard", cardData)
                .onSuccess { isUpdatingCard = false; fetchUserProfile(showLoading = false) }
                .onFailure { isUpdatingCard = false }
        }
    }

    fun updateWorkingHours(newList: List<String>) {
        updateCompanyField("workingHours", newList)
    }

    fun saveClientProfile() {
        val phoneNum = _setupPhone.value.replace(" ", "")
        if (_setupName.value.trim()
                .isEmpty() || phoneNum.length < 9 || _selectedCity.value.isEmpty() || _setupAddress.value.trim()
                .isEmpty()
        ) {
            _errorMessage.value = "Rellena todos los campos."
            return
        }
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            val fullPhone =
                "${_setupPhonePrefix.value.substringBefore(" ")} ${_setupPhone.value.trim()}"
            val data = mapOf(
                "name" to _setupName.value.trim(),
                "phone" to fullPhone,
                "city" to _selectedCity.value,
                "address" to _setupAddress.value.trim()
            )
            updateProfileUseCase.updateAccount(data)
                .onSuccess { fetchUserProfile(showLoading = false) }.onFailure {
                    _uiState.value = HomeState.NEEDS_CLIENT_INFO
                    _errorMessage.value = it.message
                }
        }
    }

    fun saveCompanyProfile() {
        val phoneNum = _setupPhone.value.replace(" ", "")
        if (_setupName.value.trim()
                .isEmpty() || phoneNum.length < 9 || _selectedCity.value.isEmpty() || _selectedCategory.value.isEmpty() || _description.value.trim()
                .isEmpty() || _setupAddress.value.trim().isEmpty()
        ) {
            _errorMessage.value = "Rellena todos los datos correctamente."
            return
        }
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            val fullPhone =
                "${_setupPhonePrefix.value.substringBefore(" ")} ${_setupPhone.value.trim()}"
            updateProfileUseCase.updateAccount(
                mapOf(
                    "phone" to fullPhone,
                    "city" to _selectedCity.value,
                    "address" to _setupAddress.value.trim()
                )
            )
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
                ),
                "acceptedPayments" to listOf("Tarjeta", "Efectivo"),
                "blockedDates" to emptyList<Long>(),
                "blockedDaysOfWeek" to emptyList<Long>()
            )
            updateCompanyProfileUseCase(compData).onSuccess { fetchUserProfile(showLoading = false) }
                .onFailure {
                    _uiState.value = HomeState.NEEDS_COMPANY_INFO
                    _errorMessage.value = it.message
                }
        }
    }

    fun onSetupNameChanged(v: String) {
        if (v.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$")) && v.length <= 40) _setupName.value = v
    }

    fun onSetupCompanyNameChanged(v: String) {
        if (v.matches(Regex("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s&'-]*$")) && v.length <= 50) _setupName.value =
            v
    }

    fun onSetupPhoneChanged(v: String) {
        if (v.replace(" ", "").all { it.isDigit() } && v.length <= 15) _setupPhone.value = v
    }

    fun onCityChanged(v: String) {
        _selectedCity.value = v
    }

    fun onCategoryChanged(v: String) {
        _selectedCategory.value = v
    }

    fun onDescriptionChanged(v: String) {
        if (v.length <= 300) _description.value = v
    }

    fun onSetupPhonePrefixChanged(v: String) {
        _setupPhonePrefix.value = v
    }

    fun onSetupAddressChanged(v: String) {
        _setupAddress.value = v
    }

    fun logout() {
        repository.logout()
    }

    fun sendPasswordReset() {
        repository.sendPasswordReset()
    }

    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING; repository.deleteAccount()
            .onSuccess { onComplete() }
        }
    }

    fun submitReview(appointment: Appointment, rating: Double, comment: String) {
        viewModelScope.launch {
            _uiState.value = HomeState.LOADING
            val review = Review(
                companyId = appointment.companyId,
                clientId = appointment.clientId,
                clientName = _userName.value,
                rating = rating,
                comment = comment.trim()
            )
            reviewRepository.addReview(review, appointment.id).onSuccess {
                fetchClientAppointments()
                fetchCompaniesInMyCity(_savedCity.value)
                _uiState.value = HomeState.DASHBOARD
            }.onFailure {
                _errorMessage.value = "No se pudo enviar la reseña."
                _uiState.value = HomeState.DASHBOARD
            }
        }
    }
}