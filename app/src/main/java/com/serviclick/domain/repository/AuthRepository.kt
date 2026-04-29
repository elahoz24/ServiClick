package com.serviclick.domain.repository

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun register(email: String, pass: String, role: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
}