package com.serviclick.domain.repository

import com.serviclick.domain.model.CompanyProfile
import com.serviclick.domain.model.UserProfile

interface UserRepository {
    fun getCurrentUserEmail(): String
    suspend fun getUserProfile(): Result<UserProfile>
    suspend fun getCompaniesInCity(city: String): Result<List<CompanyProfile>>
    suspend fun updateProfileField(field: String, value: Any): Result<Unit>
    suspend fun updateMultipleFields(data: Map<String, Any>): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    fun sendPasswordReset()
    fun logout()
}