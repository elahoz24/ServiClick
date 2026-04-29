package com.serviclick.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.serviclick.domain.model.Appointment
import com.serviclick.domain.repository.AppointmentRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor() : AppointmentRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("appointments")

    override suspend fun createAppointment(appointment: Appointment): Result<Unit> = try {
        val docRef = collection.document()
        // Le asignamos el ID generado por Firebase al propio objeto
        val newAppointment = appointment.copy(id = docRef.id)
        docRef.set(newAppointment).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getClientAppointments(clientId: String): Result<List<Appointment>> = try {
        val snapshot = collection
            .whereEqualTo("clientId", clientId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
        val list = snapshot.toObjects(Appointment::class.java)
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCompanyAppointments(companyId: String): Result<List<Appointment>> = try {
        val snapshot = collection
            .whereEqualTo("companyId", companyId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
        val list = snapshot.toObjects(Appointment::class.java)
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Unit> = try {
        collection.document(appointmentId).update("status", status).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun markAsRated(appointmentId: String): Result<Unit> = try {
        collection.document(appointmentId).update("ratingGiven", true).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}