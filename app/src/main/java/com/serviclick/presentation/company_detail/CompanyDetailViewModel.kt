package com.serviclick.presentation.company_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serviclick.domain.model.Appointment
import com.serviclick.domain.model.CompanyProfile
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

@HiltViewModel
class CompanyDetailViewModel @Inject constructor(
    private val getCompanyByIdUseCase: GetCompanyByIdUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val getCompanyAppointmentsUseCase: GetCompanyAppointmentsUseCase,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _company = MutableStateFlow<CompanyProfile?>(null)
    val company: StateFlow<CompanyProfile?> = _company.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _bookedRanges = MutableStateFlow<List<LongRange>>(emptyList())
    val bookedRanges: StateFlow<List<LongRange>> = _bookedRanges.asStateFlow()

    private val _takenSlots = MutableStateFlow<List<String>>(emptyList())
    val takenSlots: StateFlow<List<String>> = _takenSlots.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _appointmentTime = MutableStateFlow("")
    val appointmentTime: StateFlow<String> = _appointmentTime.asStateFlow()

    private val _appointmentDesc = MutableStateFlow("")
    val appointmentDesc: StateFlow<String> = _appointmentDesc.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        val companyId = savedStateHandle.get<String>("companyId")
        if (companyId != null) {
            fetchCompany(companyId)
            fetchBookedDates(companyId)
        }
    }

    private fun fetchCompany(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            getCompanyByIdUseCase(id).onSuccess {
                _company.value = it
                _isLoading.value = false
            }.onFailure { _isLoading.value = false }
        }
    }

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

    fun updateAvailableSlots(selectedDateMillis: Long) {
        viewModelScope.launch {
            val comp = _company.value ?: return@launch
            getCompanyAppointmentsUseCase(comp.id).onSuccess { appointments ->
                // Guardamos solo las horas de las citas que ya están aceptadas para ese día
                val taken = appointments.filter {
                    it.status == "Aceptada" && it.startDateMillis == selectedDateMillis
                }.map { it.time }

                _takenSlots.value = taken
            }
        }
    }

    fun onTimeChanged(v: String) {
        _appointmentTime.value = v
    }

    fun onDescChanged(v: String) { if (v.length <= 200) _appointmentDesc.value = v }

    fun setShowDialog(show: Boolean) {
        _showDialog.value = show
        if (!show) {
            _appointmentTime.value = ""
            _appointmentDesc.value = ""
        }
    }

    fun clearMessage() { _message.value = null }

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