package com.serviclick.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serviclick.domain.use_case.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    fun onRegisterChanged(email: String, pass: String, confirm: String, role: String) {
        _email.value = email
        _password.value = pass
        _confirmPassword.value = confirm
        _role.value = role

        _isRegisterEnabled.value = isValidEmail(email) && isValidPassword(pass) && pass == confirm && role.isNotEmpty()
    }

    fun onRoleChanged(newRole: String) {
        _role.value = newRole
        _isRegisterEnabled.value = isValidEmail(_email.value) && isValidPassword(_password.value) && _password.value == _confirmPassword.value && newRole.isNotEmpty()
    }

    fun onRegisterSelected() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            registerUseCase(_email.value, _password.value, _role.value).onSuccess {
                _isLoading.value = false
                _registerSuccess.value = true
            }.onFailure { error ->
                _isLoading.value = false
                _errorMessage.value = error.message
            }
        }
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean = password.length >= 6
}