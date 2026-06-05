package com.serviclick.domain.repository

import com.serviclick.domain.model.CompanyProfile
import com.serviclick.domain.model.UserProfile

/**
 * Interfaz central para la gestión de las identidades y los perfiles de los usuarios en el sistema.
 *
 * Define cómo obtener el perfil actual, recuperar los datos comerciales de una empresa, buscar profesionales
 * por ciudad, y gestionar las actualizaciones y eliminaciones de cuenta (borrado en cascada).
 */
interface UserRepository {
    fun getCurrentUserEmail(): String
    suspend fun getUserProfile(): Result<UserProfile>
    suspend fun getCompanyProfile(id: String): Result<CompanyProfile>
    suspend fun getCompaniesInCity(city: String): Result<List<CompanyProfile>>

    suspend fun updateUserProfile(data: Map<String, Any>): Result<Unit>
    suspend fun updateCompanyProfile(data: Map<String, Any>): Result<Unit>

    suspend fun deleteAccount(): Result<Unit>
    fun sendPasswordReset()
    fun logout()
}