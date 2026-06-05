package com.serviclick.domain.repository

/**
 * Interfaz que define el acceso al sistema de autenticación de la aplicación.
 *
 * Pide los datos esenciales (email y contraseña) y el `role` en caso de registro, delegando
 * a la capa de datos la comunicación con el proveedor (en este caso, Firebase Authentication).
 */
interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<Unit>
    suspend fun register(email: String, pass: String, role: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
}