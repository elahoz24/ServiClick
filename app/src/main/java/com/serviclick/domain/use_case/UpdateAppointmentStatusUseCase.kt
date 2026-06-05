package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AppointmentRepository
import javax.inject.Inject

/**
 * Caso de uso para avanzar una cita en su ciclo de vida (máquina de estados).
 * Cambiar el estatus de la reserva (ej: "Aceptada", "Pagada", "Finalizada").
 * Delega la mutación de estado al `AppointmentRepository`.
 */
class UpdateAppointmentStatusUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(appointmentId: String, status: String) =
        repository.updateAppointmentStatus(appointmentId, status)
}