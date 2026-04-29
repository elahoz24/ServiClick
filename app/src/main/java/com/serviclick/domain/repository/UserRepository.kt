package com.serviclick.domain.repository

import com.serviclick.domain.model.CompanyProfile
import com.serviclick.domain.model.UserProfile

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