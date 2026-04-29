package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AppointmentRepository
import javax.inject.Inject

class GetCompanyAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(companyId: String) = repository.getCompanyAppointments(companyId)
}