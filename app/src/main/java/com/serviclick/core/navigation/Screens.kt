package com.serviclick.core.navigation

import kotlinx.serialization.Serializable

/**
 * Define las rutas de navegación de la aplicación de forma tipada (Type-Safe Navigation).
 * * * **Propósito:** Evitar el uso de cadenas de texto (Strings) crudas para navegar entre pantallas,
 * lo cual es propenso a errores tipográficos y crashes.
 * Utiliza la librería `kotlinx.serialization`. Cada pantalla sin parámetros se declara
 * como un `object`. Las pantallas que necesitan recibir datos se declaran como `data class`.
 */

@Serializable
object LoginDestination

@Serializable
object RegisterDestination

@Serializable
object HomeDestination

@Serializable
data class CompanyDetailDestination(val companyId: String)