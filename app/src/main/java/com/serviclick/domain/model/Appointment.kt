package com.serviclick.domain.model

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
    val price: Double = 0.0, // NUEVO: Precio del servicio
    val status: String = "Pendiente", // Pendiente, Presupuestada, Aceptada, Rechazada, Finalizada
    val ratingGiven: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)