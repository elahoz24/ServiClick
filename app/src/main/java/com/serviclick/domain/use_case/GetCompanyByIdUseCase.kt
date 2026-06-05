package com.serviclick.domain.use_case

import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener los detalles completos de una única empresa.
 * Utilizado cuando el cliente pulsa sobre un profesional para ver su perfil ampliado y su agenda.
 * Delega la obtención del documento específico al `UserRepository`.
 */
class GetCompanyByIdUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(id: String) = repository.getCompanyProfile(id)
}