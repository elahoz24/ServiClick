package com.serviclick.domain.model

data class UserProfile(
    val id: String = "",
    val email: String = "",
    val role: String = "cliente",
    val name: String = "", // Nombre de la persona (dueño o cliente)
    val phone: String = "",
    val city: String = "",
    val address: String = "",
    val profileImage: String = "",
    val language: String = "Español"
)