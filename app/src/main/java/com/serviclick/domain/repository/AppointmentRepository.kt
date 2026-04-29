package com.serviclick.domain.repository

import com.serviclick.domain.model.Appointment

interface AppointmentRepository {
    suspend fun createAppointment(appointment: Appointment): Result<Unit>
    suspend fun getClientAppointments(clientId: String): Result<List<Appointment>>
    suspend fun getCompanyAppointments(companyId: String): Result<List<Appointment>>
    suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Unit>
    suspend fun markAsRated(appointmentId: String): Result<Unit>
}