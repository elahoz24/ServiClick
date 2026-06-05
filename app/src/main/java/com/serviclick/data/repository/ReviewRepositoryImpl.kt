package com.serviclick.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.serviclick.domain.model.Review
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repositorio para la gestión de las valoraciones de clientes hacia los profesionales.
 * Almacena reseñas y recalcular la nota media de la empresa afectada.
 * Utiliza `runTransaction` de Firebase para asegurar que los cálculos matemáticos (la media
 * de estrellas y el recuento de valoraciones) sean exactos incluso si dos clientes valoran a la vez.
 */
class ReviewRepositoryImpl @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()

    fun getCompanyReviews(companyId: String): Flow<List<Review>> = callbackFlow {
        val query = db.collection("reviews")
            .whereEqualTo("companyId", companyId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val reviews = snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
                trySend(reviews).isSuccess
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun addReview(review: Review, appointmentId: String): Result<Unit> = try {
        val companyRef = db.collection("users").document(review.companyId)
        val reviewRef = db.collection("reviews").document()
        val appointmentRef = db.collection("appointments").document(appointmentId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(companyRef)

            // 1. Obtener valores actuales para el cálculo de promedios
            val currentStars = snapshot.getDouble("sumaTotalEstrellas") ?: 0.0
            val currentCount = snapshot.getLong("reviewCount") ?: 0L

            val newStars = currentStars + review.rating
            val newCount = currentCount + 1
            val average = newStars / newCount

            // 2. Lógica de negocio: Verificación automática por buen rendimiento
            var isVerified = snapshot.getBoolean("isVerified") ?: false
            if (!isVerified && newCount >= 10 && average >= 4.0) {
                isVerified = true
            }

            // 3. Escribir la nueva reseña
            val reviewWithId = review.copy(id = reviewRef.id)
            transaction.set(reviewRef, reviewWithId)

            // Se marca la cita como valorada
            transaction.update(appointmentRef, "hasReview", true)

            // 4. Actualizar las estadísticas agregadas en el perfil de la empresa
            transaction.update(
                companyRef, mapOf(
                    "sumaTotalEstrellas" to newStars,
                    "reviewCount" to newCount,
                    "rating" to average,
                    "isVerified" to isVerified
                )
            )
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}