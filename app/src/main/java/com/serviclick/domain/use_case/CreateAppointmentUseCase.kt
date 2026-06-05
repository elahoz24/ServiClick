package com.serviclick.domain.use_case

import com.serviclick.domain.model.Appointment
import com.serviclick.domain.repository.AppointmentRepository
import javax.inject.Inject

/**
 * Caso de uso para crear una nueva reserva/cita en el sistema.
 * Actua como intermediario entre la UI (cuando el cliente confirma la solicitud) y el repositorio de datos.
 * Recibe el objeto `Appointment` preconfigurado desde el ViewModel y se lo pasa al Repositorio.
 */
class CreateAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(appointment: Appointment) =
        repository.createAppointment(appointment)
}