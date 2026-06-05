package com.serviclick.domain.use_case

import com.serviclick.domain.model.ChatMessage
import com.serviclick.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Caso de uso para enviar un nuevo mensaje en un chat.
 * Inserta la respuesta del cliente o profesional en el hilo de la cita correspondiente.
 * Delega el objeto `ChatMessage` y el ID del hilo al Repositorio de Chat.
 */
class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(appointmentId: String, message: ChatMessage): Result<Unit> {
        return repository.sendMessage(appointmentId, message)
    }
}