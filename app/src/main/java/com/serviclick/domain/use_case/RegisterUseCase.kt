package com.serviclick.domain.use_case

import com.serviclick.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para registrar un nuevo usuario en la plataforma.
 * Crea una cuenta en ServiClick asignando un rol inicial.
 * Comunica el email, la contraseña y el rol ("cliente" o "empresa") al repositorio.
 */
class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, pass: String, role: String) =
        repository.register(email, pass, role)
}