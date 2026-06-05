package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AppointmentRepository
import javax.inject.Inject

/**
 * Caso de uso para recuperar el historial de solicitudes de una empresa.
 * Muestra al profesional su bandeja de entrada de trabajos y su agenda histórica.
 * Utiliza el ID de la empresa autenticada para solicitar sus citas al repositorio.
 */
class GetCompanyAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(companyId: String) = repository.getCompanyAppointments(companyId)
}