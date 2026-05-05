package com.serviclick.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.serviclick.domain.model.Appointment
import com.serviclick.domain.repository.AppointmentRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor() : AppointmentRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun createAppointment(appointment: Appointment): Result<Unit> = try {
        val docRef = db.collection("appointments").document()
        val apptWithId = appointment.copy(id = docRef.id)
        docRef.set(apptWithId).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCompanyAppointments(companyId: String): Result<List<Appointment>> = try {
        val snapshot = db.collection("appointments")
            .whereEqualTo("companyId", companyId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
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

    override suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Unit> = try {
        db.collection("appointments").document(appointmentId)
            .update("status", status)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- AQUÍ ESTÁ LA FUNCIÓN QUE FALTABA PARA LAS VALORACIONES ---
    override suspend fun markAsRated(appointmentId: String): Result<Unit> = try {
        db.collection("appointments").document(appointmentId)
            .update("ratingGiven", true)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}