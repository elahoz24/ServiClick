package com.serviclick.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginDestination

@Serializable
object RegisterDestination

@Serializable
object HomeDestination

// NUEVO: Destino para ver el perfil de una empresa. Exige su ID.
@Serializable
data class CompanyDetailDestination(val companyId: String)