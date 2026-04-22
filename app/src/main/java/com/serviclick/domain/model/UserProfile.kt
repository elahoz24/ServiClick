package com.serviclick.domain.model

data class UserProfile(
    val id: String = "",
    val email: String = "",
    val role: String = "cliente",
    val name: String = "",
    val companyName: String = "",
    val phone: String = "",
    val city: String = "",
    val address: String = "",
    val category: String = "",
    val description: String = "",
    val profileImage: String = "",
    val bannerImage: String = "",
    val language: String = "Español"
)