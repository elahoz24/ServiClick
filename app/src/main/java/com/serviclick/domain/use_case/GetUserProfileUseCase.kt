package com.serviclick.domain.use_case
import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.getUserProfile()
}