package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AppointmentRepository
import javax.inject.Inject

class UpdateAppointmentStatusUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(appointmentId: String, status: String) =
        repository.updateAppointmentStatus(appointmentId, status)
}