package com.serviclick.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

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

    fun onRegisterChanged(email: String, pass: String, confirmPass: String, role: String) {
        _email.value = email
        _password.value = pass
        _confirmPassword.value = confirmPass
        _role.value = role
        _isRegisterEnabled.value = isValidEmail(email) && isValidPassword(pass) && (pass == confirmPass)
    }

    fun onRoleChanged(newRole: String) {
        _role.value = newRole
    }

    fun onRegisterSelected() {
        _isLoading.value = true
        _errorMessage.value = null

        auth.createUserWithEmailAndPassword(_email.value, _password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserToFirestore(userId)
                    }
                } else {
                    _isLoading.value = false
                    // AQUÍ USAMOS NUESTRO TRADUCTOR
                    _errorMessage.value = getTranslatedErrorMessage(task.exception)
                }
            }
    }

    private fun saveUserToFirestore(userId: String) {
        val userMap = hashMapOf(
            "email" to _email.value,
            "role" to _role.value,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                _isLoading.value = false
                _registerSuccess.value = true
            }
            .addOnFailureListener {
                _isLoading.value = false
                _errorMessage.value = "Cuenta creada, pero hubo un error de conexión al guardar tu perfil."
            }
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean = password.length >= 6

    // --- TRADUCTOR DE ERRORES AL ESPAÑOL ---
    private fun getTranslatedErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo electrónico."
            is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil (mínimo 6 caracteres)."
            is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico no es válido."
            is FirebaseNetworkException -> "No hay conexión a internet. Revisa tu red."
            else -> "Se ha producido un error inesperado al registrarte."
        }
    }
}