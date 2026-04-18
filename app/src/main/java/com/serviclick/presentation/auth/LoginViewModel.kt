package com.serviclick.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    // 1. ESTADOS (Privados para que la Vista no pueda modificarlos directamente)
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoginEnabled = MutableStateFlow(false)
    val isLoginEnabled: StateFlow<Boolean> = _isLoginEnabled.asStateFlow()

    // 2. EVENTOS (Lo que el usuario hace en la pantalla)
    fun onLoginChanged(email: String, pass: String) {
        _email.value = email
        _password.value = pass
        // El botón solo se habilitará si ambas reglas se cumplen
        _isLoginEnabled.value = isValidEmail(email) && isValidPassword(pass)
    }

    fun onLoginSelected() {
        // En el futuro, aquí llamaremos a Firebase Auth para iniciar sesión
    }

    // 3. REGLAS DE NEGOCIO (Validaciones)

    // Validación robusta usando la herramienta nativa de Android
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validación de contraseña (mínimo 6 caracteres suele ser el estándar de Firebase)
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}