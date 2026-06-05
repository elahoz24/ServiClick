package com.serviclick.domain.model

import androidx.annotation.Keep

/**
 * Representa una cita, reserva o solicitud de servicio entre un cliente y una empresa.
 *
 * Es el puente transaccional de ServiClick. Conecta la necesidad de un usuario con la agenda
 * de un profesional.
 *
 * Funciona como una máquina de estados mediante el campo `status`
 * (Pendiente -> Aceptada -> Finalizada -> Pagada). Almacena IDs cruzados (cliente y empresa)
 * para facilitar las consultas bidireccionales en base de datos.
 */

@Keep
data class Appointment(
    val id: String = "",
    val clientId: String = "",
    val companyId: String = "",
    val clientName: String = "",
    val companyName: String = "",
    val startDateMillis: Long = 0L,
    val endDateMillis: Long = 0L,
    val time: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val status: String = "Pendiente", // Estado actual en el ciclo de vida del servicio
    val hasReview: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(), // Timestamp de creación para ordenar historiales
    val hasMessages: Boolean = false, // Bandera para saber si el chat interno está activo
    val paymentMethod: String = "" // "Tarjeta" o "Efectivo", asignado al finalizar la cita
)