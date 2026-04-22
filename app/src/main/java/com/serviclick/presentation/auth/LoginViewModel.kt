package com.serviclick.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.FirebaseNetworkException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoginEnabled = MutableStateFlow(false)
    val isLoginEnabled: StateFlow<Boolean> = _isLoginEnabled.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _resetMessage = MutableStateFlow<String?>(null)
    val resetMessage: StateFlow<String?> = _resetMessage.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    fun onLoginChanged(email: String, pass: String) {
        _email.value = email
        _password.value = pass
        _isLoginEnabled.value = isValidEmail(email) && isValidPassword(pass)
    }

    fun onLoginSelected() {
        _isLoading.value = true
        _errorMessage.value = null
        _resetMessage.value = null

        auth.signInWithEmailAndPassword(_email.value, _password.value)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _loginSuccess.value = true
                } else {
                    _errorMessage.value = getTranslatedErrorMessage(task.exception)
                }
            }
    }

    fun onResetPassword(emailToReset: String) {
        if (!isValidEmail(emailToReset)) {
            _errorMessage.value = "Por favor, introduce un correo electrónico válido."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _resetMessage.value = null

        auth.sendPasswordResetEmail(emailToReset.trim())
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _resetMessage.value = "Se ha enviado un enlace a tu correo. Revisa también la carpeta de Spam."
                } else {
                    _errorMessage.value = getTranslatedErrorMessage(task.exception)
                }
            }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _resetMessage.value = null
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean = password.length >= 6

    private fun getTranslatedErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "No existe ninguna cuenta registrada con este correo."
            is FirebaseAuthInvalidCredentialsException -> "El correo o la contraseña son incorrectos."
            is FirebaseNetworkException -> "No hay conexión a internet. Revisa tu red."
            else -> "Se ha producido un error inesperado. Inténtalo de nuevo."
        }
    }
}