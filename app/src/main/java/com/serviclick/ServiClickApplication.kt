package com.serviclick

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase base de la aplicación requerida por Dagger Hilt.
 * Actua como el punto de entrada principal donde se inicializa el árbol de
 * inyección de dependencias antes de que cualquier otra pantalla o componente se ejecute.
 * Al heredar de `Application` y añadir la anotación `@HiltAndroidApp`,
 * el compilador genera automáticamente todo el código base necesario para que Hilt sepa
 * cómo inyectar los Repositorios y Casos de Uso en los ViewModels.
 */
@HiltAndroidApp
class ServiClickApplication : Application()