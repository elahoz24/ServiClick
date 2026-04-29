package com.serviclick.domain.use_case

import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

class UpdateCompanyProfileUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(data: Map<String, Any>) = repository.updateCompanyProfile(data)
    suspend fun updateField(field: String, value: Any) = repository.updateCompanyProfile(mapOf(field to value))
}