package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AppointmentRepository
import javax.inject.Inject

/**
 * Caso de uso para recuperar el historial completo de citas de un cliente.
 * Obtiene todas las reservas (pasadas y futuras) donde el usuario actual figure como cliente.
 * Delega en el repositorio pasándole el ID del cliente actual.
 */
class GetClientAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(clientId: String) = repository.getClientAppointments(clientId)
}