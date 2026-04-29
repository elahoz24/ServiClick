package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String) = repository.resetPassword(email)
}