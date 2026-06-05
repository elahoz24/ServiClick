package com.serviclick.domain.model

import androidx.annotation.Keep

/**
 * Representa un mensaje individual dentro del sistema de chat interno de la aplicación.
 * Almacena la información básica de un mensaje enviado entre un cliente y un profesional.
 * Identifica el mensaje (`id`), quién lo escribió (`senderId`), su contenido (`text`) y el momento exacto (`timestamp`).
 */
@Keep
data class ChatMessage(
    val id: String = "",
    val senderId: String = "", // ID del usuario que lo envía (cliente o empresa)
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)