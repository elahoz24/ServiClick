package com.serviclick.domain.use_case

import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(private val repository: UserRepository) {
    suspend fun updateAccount(data: Map<String, Any>) = repository.updateUserProfile(data)
    suspend fun updateField(field: String, value: Any) = repository.updateUserProfile(mapOf(field to value))
}