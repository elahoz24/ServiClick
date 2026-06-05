package com.serviclick.presentation.company_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serviclick.data.repository.ReviewRepositoryImpl
import com.serviclick.domain.model.Appointment
import com.serviclick.domain.model.CompanyProfile
import com.serviclick.domain.model.Review
import com.serviclick.domain.repository.UserRepository
import com.serviclick.domain.use_case.CreateAppointmentUseCase
import com.serviclick.domain.use_case.GetCompanyAppointmentsUseCase
import com.serviclick.domain.use_case.GetCompanyByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Controlador (ViewModel) de la pantalla de detalles de la empresa.
 * Gestiona el estado de la vista pública de una empresa y orquesta la lógica
 * para la creación de una nueva reserva (Appointment) por parte de un cliente.
 * Mantiene un estado reactivo complejo (datos de la empresa, reseñas, fechas bloqueadas,
 * horas ocupadas y el estado del formulario de reserva). Se alimenta de varios Casos de Uso.
 */
@HiltViewModel
class CompanyDetailViewModel @Inject constructor(
    private val getCompanyByIdUseCase: GetCompanyByIdUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val getCompanyAppointmentsUseCase: GetCompanyAppointmentsUseCase,
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepositoryImpl,
    savedStateHandle: SavedStateHandle // Permite acceder a los argumentos de navegación de forma segura
) : ViewModel() {

    private val _company = MutableStateFlow<CompanyProfile?>(null)
    val company: StateFlow<CompanyProfile?> = _company.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Rangos de días que están completamente bloqueados por reservas de jornada completa
    private val _bookedRanges = MutableStateFlow<List<LongRange>>(emptyList())
    val bookedRanges: StateFlow<List<LongRange>> = _bookedRanges.asStateFlow()

    // Lista de horas que ya han sido reservadas para un día en concreto
    private val _takenSlots = MutableStateFlow<List<String>>(emptyList())
    val takenSlots: StateFlow<List<String>> = _takenSlots.asStateFlow()

    // Control de visibilidad del diálogo modal de reservas
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _appointmentTime = MutableStateFlow("")
    val appointmentTime: StateFlow<String> = _appointmentTime.asStateFlow()

    private val _appointmentDesc = MutableStateFlow("")
    val appointmentDesc: StateFlow<String> = _appointmentDesc.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    // Sistema de feedback al usuario
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        // Recupera el ID de la empresa de forma automática desde la ruta de Compose
        val companyId = savedStateHandle.get<String>("companyId")
        if (companyId != null) {
            fetchCompany(companyId)
            fetchBookedDates(companyId)
            fetchReviews(companyId)
        }
    }

    /** Escucha reactiva de las reseñas de la empresa. */
    private fun fetchReviews(companyId: String) {
        viewModelScope.launch {
            reviewRepository.getCompanyReviews(companyId).collect {
                _reviews.value = it
            }
        }
    }

    /** Descarga el perfil comercial de la empresa. */
    private fun fetchCompany(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            getCompanyByIdUseCase(id).onSuccess {
                _company.value = it
                _isLoading.value = false
            }.onFailure { _isLoading.value = false }
        }
    }

    /** * Busca las citas históricas de la empresa para inhabilitar días en el calendario general.
     * Solo inactiva el día si la cita tiene status "Aceptada" (confirmada por el profesional).
     */
    private fun fetchBookedDates(companyId: String) {
        viewModelScope.launch {
            getCompanyAppointmentsUseCase(companyId).onSuccess { appointments ->
                val occupied = appointments.filter { it.status == "Aceptada" }.map {
                    it.startDateMillis..it.endDateMillis
                }
                _bookedRanges.value = occupied
            }
        }
    }

    /**
     * AÑADIDO ANTI-SOLAPAMIENTO DE HORAS
     * Evita que dos clientes pidan cita a la misma hora el mismo día.
     * Cuando el usuario toca un día en el calendario de Compose, este método evalúa
     * todas las reservas de la empresa, filtra las de ese día específico y extrae las horas que están ocupadas.
     */
    fun updateAvailableSlots(selectedDateMillis: Long) {
        viewModelScope.launch {
            val comp = _company.value ?: return@launch
            getCompanyAppointmentsUseCase(comp.id).onSuccess { appointments ->
                // Bloquea la hora si ya está Aceptada, Presupuestada o Pendiente para ese día
                val taken = appointments.filter {
                    it.status in listOf(
                        "Pendiente",
                        "Presupuestada",
                        "Aceptada",
                        "Pendiente Efectivo"
                    ) &&
                            it.startDateMillis == selectedDateMillis
                }.map { it.time }

                _takenSlots.value = taken
                _appointmentTime.value = "" // Resetea la hora si cambias de día para evitar errores
            }
        }
    }

    fun onTimeChanged(v: String) {
        _appointmentTime.value = v
    }

    fun onDescChanged(v: String) {
        if (v.length <= 200) _appointmentDesc.value = v
    }

    fun setShowDialog(show: Boolean) {
        _showDialog.value = show
        // Si se cierra el diálogo, limpiamos los campos para no guardar datos fantasma
        if (!show) {
            _appointmentTime.value = ""
            _appointmentDesc.value = ""
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    /**
     * Genera la transacción final de reserva.
     */
    fun createAppointment(startMillis: Long, endMillis: Long, finalTime: String) {
        val comp = _company.value ?: return
        viewModelScope.launch {
            _isSubmitting.value = true
            userRepository.getUserProfile().onSuccess { client ->
                val appointment = Appointment(
                    clientId = client.id,
                    companyId = comp.id,
                    clientName = client.name,
                    companyName = comp.name,
                    startDateMillis = startMillis,
                    endDateMillis = endMillis,
                    time = finalTime,
                    description = _appointmentDesc.value.trim()
                )
                createAppointmentUseCase(appointment).onSuccess {
                    _isSubmitting.value = false
                    setShowDialog(false)
                    _message.value = "¡Solicitud enviada! Espera a que el profesional la acepte."
                }.onFailure {
                    _isSubmitting.value = false
                    _message.value = "Error: ${it.message}"
                }
            }.onFailure {
                _isSubmitting.value = false
                _message.value = "Error al obtener tus datos."
            }
        }
    }
}