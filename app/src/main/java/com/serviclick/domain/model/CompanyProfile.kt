package com.serviclick.domain.model

data class CompanyProfile(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val city: String = "",
    val address: String = "",
    val profileImage: String = "",
    val bannerImage: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val workingHours: List<String> = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "16:00", "17:00", "18:00", "19:00") // Horario por defecto
)