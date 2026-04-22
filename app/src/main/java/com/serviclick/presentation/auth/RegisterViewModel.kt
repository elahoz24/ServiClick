package com.serviclick.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

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

        auth.createUserWithEmailAndPassword(_email.value, _password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    if (userId != null) {
                        val userMap = hashMapOf(
                            "email" to _email.value,
                            "role" to _role.value,
                            "createdAt" to System.currentTimeMillis()
                        )

                        db.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener {
                                _isLoading.value = false
                                _registerSuccess.value = true
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                _errorMessage.value = "Error al crear perfil en base de datos: ${e.message}"
                            }
                    }
                } else {
                    _isLoading.value = false
                    _errorMessage.value = getTranslatedErrorMessage(task.exception)
                }
            }
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean = password.length >= 6

    private fun getTranslatedErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres."
            is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico no es válido."
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo electrónico."
            else -> "Se ha producido un error inesperado. Inténtalo de nuevo."
        }
    }
}