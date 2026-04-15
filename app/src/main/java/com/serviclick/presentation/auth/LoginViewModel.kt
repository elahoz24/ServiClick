package com.serviclick.presentation.auth

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
        // En el futuro, aquí llamaremos a Firebase Auth
    }

    // 3. REGLAS DE NEGOCIO (Validaciones)
    private fun isValidEmail(email: String): Boolean = email.contains("@") && email.contains(".")
    private fun isValidPassword(password: String): Boolean = password.length >= 6
}