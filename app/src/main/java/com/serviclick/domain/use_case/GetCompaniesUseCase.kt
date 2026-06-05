package com.serviclick.domain.use_case

import com.serviclick.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Caso de uso para buscar empresas filtradas por su ciudad.
 * Alimenta el "Dashboard" principal del cliente con los profesionales disponibles en su zona.
 * Pide al `UserRepository` el listado de empresas asociadas a una cadena de texto (ciudad).
 */
class GetCompaniesUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(city: String) = repository.getCompaniesInCity(city)
}