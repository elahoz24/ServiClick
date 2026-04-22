package com.serviclick.domain.use_case
import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(private val repository: UserRepository) {
    suspend fun updateField(f: String, v: Any) = repository.updateProfileField(f, v)
    suspend fun updateMultiple(d: Map<String, Any>) = repository.updateMultipleFields(d)
}