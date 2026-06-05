package com.serviclick.domain.model

import androidx.annotation.Keep

/**
 * Representa la cuenta base de un usuario en el sistema, independientemente de si
 * luego actúa como cliente o como empresa.
 *
 * Maneja la identidad, los roles y la información de contacto personal.
 *
 * El campo `role` ("cliente" o "empresa") es el que dicta qué interfaz y qué permisos
 * tiene el usuario al hacer login en la aplicación.
 */
@Keep
data class UserProfile(
    val id: String = "",
    val email: String = "",
    val role: String = "cliente", // Por defecto, todo nuevo registro es cliente
    val name: String = "",
    val phone: String = "",
    val city: String = "",
    val address: String = "",
    val profileImage: String = "",
    val language: String = "Español",

    // Se guarda la información de la tarjeta de forma simulada en un String
    // La capa de UI (Presentation) es la encargada de parsear este string al mostrarlo.
    val mockCard: String = ""
)