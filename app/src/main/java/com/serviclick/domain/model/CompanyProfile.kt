package com.serviclick.domain.model

data class CompanyProfile(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val city: String = "",
    val phone: String = "",
    val profileImage: String = "",
    val bannerImage: String = ""
)