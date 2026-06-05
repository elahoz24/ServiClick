package com.serviclick.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.serviclick.domain.model.Appointment
import com.serviclick.domain.repository.AppointmentRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repositorio encargado de gestionar el ciclo de vida de las reservas (Appointments) en Firestore.
 * Centraliza la creación, lectura (tanto para clientes como para empresas) y
 * actualización de estado de las citas.
 * Ejecuta consultas (Queries) NoSQL filtrando por IDs y ordenando por fecha de creación.
 */
class AppointmentRepositoryImpl @Inject constructor() : AppointmentRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun createAppointment(appointment: Appointment): Result<Unit> = try {
        val docRef = db.collection("appointments").document()
        val apptWithId = appointment.copy(id = docRef.id) // Incrustamos el ID generado
        docRef.set(apptWithId).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCompanyAppointments(companyId: String): Result<List<Appointment>> =
        try {
            val snapshot = db.collection("appointments")
                .whereEqualTo("companyId", companyId)
                .orderBy("createdAt", Query.Direction.DESCENDING) // Orden cronológico inverso
                .get()
                .await()

            val list = snapshot.documents.mapNotNull { it.toObject(Appointment::class.java) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getClientAppointments(clientId: String): Result<List<Appointment>> = try {
        val snapshot = db.collection("appointments")
            .whereEqualTo("clientId", clientId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val list = snapshot.documents.mapNotNull { it.toObject(Appointment::class.java) }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateAppointmentStatus(
        appointmentId: String,
        status: String
    ): Result<Unit> = try {
        db.collection("appointments").document(appointmentId)
            .update("status", status)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // AQUÍ ESTÁ LA FUNCIÓN PARA LAS VALORACIONES
    // Actualiza el flag local 'ratingGiven' para evitar valoraciones duplicadas
    override suspend fun markAsRated(appointmentId: String): Result<Unit> = try {
        db.collection("appointments").document(appointmentId)
            .update("ratingGiven", true)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}