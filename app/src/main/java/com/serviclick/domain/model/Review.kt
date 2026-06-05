package com.serviclick.domain.model

import androidx.annotation.Keep

/**
 * Representa la valoración (nota y reseña) que un cliente deja a un profesional tras finalizar un servicio.
 * Mantiene un sistema de reputación para las empresas.
 * Guarda la puntuación de 1 a 5 (`rating`) y un comentario opcional (`comment`). Además, relaciona los IDs
 * del cliente y la empresa.
 */
@Keep
data class Review(
    val id: String = "",
    val companyId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)