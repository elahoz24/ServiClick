package com.serviclick.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serviclick.domain.use_case.LoginUseCase
import com.serviclick.domain.use_case.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

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

        viewModelScope.launch {
            loginUseCase(_email.value, _password.value).onSuccess {
                _isLoading.value = false
                _loginSuccess.value = true
            }.onFailure { error ->
                _isLoading.value = false
                _errorMessage.value = error.message
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

        viewModelScope.launch {
            resetPasswordUseCase(emailToReset.trim()).onSuccess {
                _isLoading.value = false
                _resetMessage.value = "Se ha enviado un enlace a tu correo. Revisa también la carpeta de Spam."
            }.onFailure { error ->
                _isLoading.value = false
                _errorMessage.value = error.message
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _resetMessage.value = null
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean = password.length >= 6
}