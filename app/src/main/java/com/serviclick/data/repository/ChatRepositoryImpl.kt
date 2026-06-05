package com.serviclick.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.serviclick.domain.model.ChatMessage
import com.serviclick.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repositorio dedicado al sistema de mensajería en tiempo real.
 * Permite la comunicación instantánea asociada a una cita en concreto.
 * Transforma los listeners asíncronos de Firestore en flujos de datos (Flows) nativos de Kotlin.
 */
class ChatRepositoryImpl @Inject constructor() : ChatRepository {

    private val db = FirebaseFirestore.getInstance()

    override fun getMessagesForAppointment(appointmentId: String): Flow<List<ChatMessage>> =
        callbackFlow {
            // Apuntamos a la subcolección "messages" dentro de la cita específica
            val query = db.collection("appointments")
                .document(appointmentId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING) // Orden cronológico

            // Abrimos la escucha en tiempo real
            val subscription = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages =
                        snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                    trySend(messages).isSuccess
                }
            }

            // Matamos el listener si se cierra el flujo para evitar memory leaks
            awaitClose { subscription.remove() }
        }

    override suspend fun sendMessage(appointmentId: String, message: ChatMessage): Result<Unit> =
        try {
            val messageRef = db.collection("appointments")
                .document(appointmentId)
                .collection("messages")
                .document()

            val messageWithId = message.copy(id = messageRef.id)
            val appointmentRef = db.collection("appointments").document(appointmentId)

            // Usamos un Batch para escribir el mensaje Y actualizar la cita al mismo tiempo
            db.runBatch { batch ->
                batch.set(messageRef, messageWithId)
                batch.update(
                    appointmentRef,
                    "hasMessages",
                    true
                ) // Marca que ya no es un chat vacío
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}