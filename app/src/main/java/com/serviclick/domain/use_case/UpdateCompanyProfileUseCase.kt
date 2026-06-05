package com.serviclick.domain.use_case

import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Caso de uso para la edición del escaparate y panel de control de la empresa.
 * Guarda los cambios del perfil, agenda y opciones de pago del profesional.
 * Provee dos métodos. `invoke()` para actualizar bloques completos de datos mediante un Mapa,
 * y `updateField()` como atajo para actualizar un solo campo específico (ej: solo el horario o solo la imagen de portada).
 */
class UpdateCompanyProfileUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(data: Map<String, Any>) = repository.updateCompanyProfile(data)

    suspend fun updateField(field: String, value: Any) =
        repository.updateCompanyProfile(mapOf(field to value))
}