package com.serviclick.domain.use_case

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Caso de uso para que el profesional responda a una solicitud de servicio (dando presupuesto o rechazando).
 * Actualiza el estado y el precio de una cita simultáneamente.
 */
class RespondToAppointmentUseCase @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()

    suspend operator fun invoke(
        appointmentId: String,
        price: Double,
        status: String
    ): Result<Unit> = try {
        // Acceso directo a infraestructura (Firebase) desde un Use Case. No es correcto pero lo he hecho asi para lograr el MVP
        db.collection("appointments").document(appointmentId)
            .update(
                mapOf(
                    "price" to price,
                    "status" to status
                )
            ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}