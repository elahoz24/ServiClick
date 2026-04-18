package com.serviclick.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _role = MutableStateFlow("cliente")
    val role: StateFlow<String> = _role.asStateFlow()

    private val _isRegisterEnabled = MutableStateFlow(false)
    val isRegisterEnabled: StateFlow<Boolean> = _isRegisterEnabled.asStateFlow()

    fun onRegisterChanged(email: String, pass: String, confirmPass: String, role: String) {
        _email.value = email
        _password.value = pass
        _confirmPassword.value = confirmPass
        _role.value = role

        _isRegisterEnabled.value = isValidEmail(email) &&
                isValidPassword(pass) &&
                (pass == confirmPass)
    }

    fun onRoleChanged(newRole: String) {
        _role.value = newRole
    }

    fun onRegisterSelected() {
        // Aquí conectaremos Firebase próximamente
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean = password.length >= 6
}