package com.serviclick.domain.use_case

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RespondToAppointmentUseCase @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()

    suspend operator fun invoke(appointmentId: String, price: Double, status: String): Result<Unit> = try {
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