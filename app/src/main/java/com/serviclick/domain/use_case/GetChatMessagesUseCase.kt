package com.serviclick.domain.use_case

import com.serviclick.domain.model.ChatMessage
import com.serviclick.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener el flujo en tiempo real de los mensajes de un chat.
 * Alimenta la pantalla de chat con los mensajes más recientes.
 * A diferencia de otros casos de uso, este NO usa la palabra reservada `suspend` porque
 * no devuelve un dato estático, sino un `Flow` (un canal abierto que emite datos cada vez que hay cambios).
 */
class GetChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(appointmentId: String): Flow<List<ChatMessage>> {
        return repository.getMessagesForAppointment(appointmentId)
    }
}