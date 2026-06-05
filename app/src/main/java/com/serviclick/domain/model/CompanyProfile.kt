package com.serviclick.domain.model

import androidx.annotation.Keep

/**
 * Representa el perfil comercial de un profesional o empresa dentro de ServiClick.
 * Es el modelo de datos principal que ve un cliente al buscar un servicio y el que
 * gestiona el profesional en su panel.
 * Almacena tanto información descriptiva (nombre, categoría, imágenes en Base64) como
 * datos operativos vitales para la lógica de negocio (horarios, fechas bloqueadas y pagos).
 */
@Keep
data class CompanyProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val address: String = "",
    val category: String = "",
    val description: String = "",
    // Las imágenes se guardan como String (Base64) para simplificar la persistencia en el MVP
    val profileImage: String = "",
    val bannerImage: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val sumaTotalEstrellas: Double = 0.0,
    val isVerified: Boolean = false,

    // Lista de horas operativas predeterminadas de la empresa
    val workingHours: List<String> = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "16:00", "16:30", "17:00",
        "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"
    ),

    val acceptedPayments: List<String> = listOf("Tarjeta", "Efectivo"),

    val blockedDates: List<Long> = emptyList(), // Días sueltos o vacaciones
    val blockedDaysOfWeek: List<Long> = emptyList() // Días fijos semanales (ej: 1=Dom, 7=Sab)
)