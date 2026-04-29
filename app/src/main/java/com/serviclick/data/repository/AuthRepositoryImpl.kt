package com.serviclick.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.serviclick.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun login(email: String, pass: String): Result<Unit> = try {
        auth.signInWithEmailAndPassword(email, pass).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(getTranslatedErrorMessage(e)))
    }

    override suspend fun register(email: String, pass: String, role: String): Result<Unit> = try {
        val result = auth.createUserWithEmailAndPassword(email, pass).await()
        val userId = result.user?.uid ?: throw Exception("Error al obtener el usuario")

        val userMap = hashMapOf(
            "email" to email,
            "role" to role,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(userId).set(userMap).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(getTranslatedErrorMessage(e)))
    }

    override suspend fun resetPassword(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email.trim()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(getTranslatedErrorMessage(e)))
    }

    private fun getTranslatedErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "No existe ninguna cuenta registrada con este correo."
            is FirebaseAuthInvalidCredentialsException -> "El correo o la contraseña son incorrectos (o el formato no es válido)."
            is FirebaseNetworkException -> "No hay conexión a internet. Revisa tu red."
            is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres."
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo electrónico."
            else -> exception?.message ?: "Se ha producido un error inesperado. Inténtalo de nuevo."
        }
    }
}