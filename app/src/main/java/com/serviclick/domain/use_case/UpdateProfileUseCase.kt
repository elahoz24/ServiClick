package com.serviclick.domain.use_case

import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Caso de uso para la modificación de datos del usuario básico/cliente.
 * Actualiza información personal (nombre, dirección, teléfono, tarjetas simuladas).
 * Funciona igual que el caso de uso de empresa, delegando al `UserRepository` la actualización
 * en la colección `users` de Firebase mediante bloques (Map) o campos individuales.
 */
class UpdateProfileUseCase @Inject constructor(private val repository: UserRepository) {
    suspend fun updateAccount(data: Map<String, Any>) = repository.updateUserProfile(data)

    suspend fun updateField(field: String, value: Any) =
        repository.updateUserProfile(mapOf(field to value))
}