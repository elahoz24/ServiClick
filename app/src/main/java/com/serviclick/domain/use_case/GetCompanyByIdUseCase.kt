package com.serviclick.domain.use_case

import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

class GetCompanyByIdUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(id: String) = repository.getCompanyProfile(id)
}