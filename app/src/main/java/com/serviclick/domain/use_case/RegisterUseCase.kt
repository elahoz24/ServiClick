package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String, role: String) = repository.register(email, pass, role)
}