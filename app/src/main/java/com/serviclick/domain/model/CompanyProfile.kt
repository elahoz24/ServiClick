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
    // Ponemos un horario por defecto con intervalos de 30 mins para no abrumar,
    // pero el empresario podrá activar los cuartos de hora desde su panel
    val workingHours: List<String> = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "16:00", "16:30", "17:00",
        "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"
    )
)