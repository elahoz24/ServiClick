package com.serviclick.domain.repository

import com.serviclick.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el manejo de la mensajería en tiempo real entre usuarios.
 *
 * Tiene una función suspendida para enviar mensajes, y una función reactiva (`Flow`) para leerlos.
 */
interface ChatRepository {
    // Usamos Flow para recibir los mensajes en tiempo real desde Firebase
    fun getMessagesForAppointment(appointmentId: String): Flow<List<ChatMessage>>

    // Función normal (suspend) para enviar un mensaje
    suspend fun sendMessage(appointmentId: String, message: ChatMessage): Result<Unit>
}