package com.serviclick.domain.use_case

import com.serviclick.domain.model.Appointment
import com.serviclick.domain.repository.AppointmentRepository
import javax.inject.Inject

class CreateAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(appointment: Appointment) = repository.createAppointment(appointment)
}