package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para el inicio de sesión.
 * Valida las credenciales del usuario contra el sistema de autenticación.
 * Envía el email y el password al repositorio de autenticación (`AuthRepository`).
 */
class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String) = repository.login(email, pass)
}