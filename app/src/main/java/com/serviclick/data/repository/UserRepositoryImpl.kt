package com.serviclick.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.serviclick.domain.model.CompanyProfile
import com.serviclick.domain.model.UserProfile
import com.serviclick.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun getCurrentUserEmail(): String = auth.currentUser?.email ?: ""

    override suspend fun getUserProfile(): Result<UserProfile> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Sin sesión")
        val doc = db.collection("users").document(userId).get().await()
        if (doc.exists()) {
            Result.success(UserProfile(
                id = doc.id,
                role = doc.getString("role") ?: "cliente",
                name = doc.getString("name") ?: "",
                companyName = doc.getString("companyName") ?: "",
                phone = doc.getString("phone") ?: "",
                city = doc.getString("city") ?: "",
                address = doc.getString("address") ?: "",
                category = doc.getString("category") ?: "",
                description = doc.getString("description") ?: "",
                profileImage = doc.getString("profileImage") ?: "",
                bannerImage = doc.getString("bannerImage") ?: ""
            ))
        } else Result.failure(Exception("No existe"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getCompaniesInCity(city: String): Result<List<CompanyProfile>> = try {
        val snapshot = db.collection("users").whereEqualTo("role", "empresa").whereEqualTo("city", city).get().await()
        val list = snapshot.documents.map { doc ->
            CompanyProfile(
                id = doc.id,
                name = doc.getString("companyName") ?: doc.getString("name") ?: "",
                category = doc.getString("category") ?: "",
                description = doc.getString("description") ?: "",
                city = doc.getString("city") ?: "",
                profileImage = doc.getString("profileImage") ?: "",
                bannerImage = doc.getString("bannerImage") ?: ""
            )
        }
        Result.success(list)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun updateProfileField(field: String, value: Any): Result<Unit> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Sin sesión")
        db.collection("users").document(userId).update(field, value).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun updateMultipleFields(data: Map<String, Any>): Result<Unit> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Sin sesión")
        db.collection("users").document(userId).update(data).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun deleteAccount(): Result<Unit> = try {
        val user = auth.currentUser ?: throw Exception("Sin sesión")
        db.collection("users").document(user.uid).delete().await()
        user.delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override fun sendPasswordReset() { auth.currentUser?.email?.let { auth.sendPasswordResetEmail(it) } }
    override fun logout() { auth.signOut() }
}