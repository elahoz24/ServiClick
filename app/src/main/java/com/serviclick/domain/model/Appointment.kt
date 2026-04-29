package com.serviclick.domain.model

data class Appointment(
    val id: String = "",
    val clientId: String = "",
    val companyId: String = "",
    val clientName: String = "",
    val companyName: String = "",
    val startDateMillis: Long = 0L, // Fecha de inicio del trabajo
    val endDateMillis: Long = 0L,   // Fecha de fin (puede ser el mismo día)
    val time: String = "",          // Tramo horario (Ej: 10:00 - 12:00)
    val description: String = "",
    val status: String = "Pendiente",
    val ratingGiven: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)