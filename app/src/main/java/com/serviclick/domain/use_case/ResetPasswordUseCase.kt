package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para el proceso de recuperación de cuenta.
 * Envia un correo electrónico con el enlace de restablecimiento de contraseña.
 * Pasa el email proporcionado al repositorio de autenticación.
 */
class ResetPasswordUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String) = repository.resetPassword(email)
}