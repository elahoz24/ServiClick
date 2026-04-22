package com.serviclick.domain.use_case
import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

class GetCompaniesUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(city: String) = repository.getCompaniesInCity(city)
}