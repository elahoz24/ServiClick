package com.serviclick.domain.use_case

import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Caso de uso para recuperar el perfil del usuario activo en la sesión.
 * Es la primera llamada que hace la app tras el login para saber quién somos, qué rol tenemos
 * y cargar nuestros datos base (ciudad, tarjetas, etc.).
 * Llama al repositorio para recuperar el documento "users" ligado al Firebase Auth actual.
 */
class GetUserProfileUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.getUserProfile()
}