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
                email = auth.currentUser?.email ?: "",
                role = doc.getString("role") ?: "cliente",
                name = doc.getString("name") ?: "",
                phone = doc.getString("phone") ?: "",
                city = doc.getString("city") ?: "",
                address = doc.getString("address") ?: "",
                profileImage = doc.getString("profileImage") ?: "",
                language = doc.getString("language") ?: "Español"
            ))
        } else Result.failure(Exception("Usuario no encontrado"))
    } catch (e: Exception) { Result.failure(e) }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getCompanyProfile(id: String): Result<CompanyProfile> = try {
        val doc = db.collection("company_profiles").document(id).get().await()
        if (doc.exists()) {
            val hours = doc.get("workingHours") as? List<String>
            Result.success(CompanyProfile(
                id = doc.id,
                name = doc.getString("name") ?: "",
                category = doc.getString("category") ?: "",
                description = doc.getString("description") ?: "",
                city = doc.getString("city") ?: "",
                address = doc.getString("address") ?: "",
                profileImage = doc.getString("profileImage") ?: "",
                bannerImage = doc.getString("bannerImage") ?: "",
                rating = doc.getDouble("rating") ?: 0.0,
                reviewCount = doc.getLong("reviewCount")?.toInt() ?: 0,
                workingHours = hours ?: listOf(
                    "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                    "12:00", "12:30", "13:00", "16:00", "16:30", "17:00",
                    "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"
                )
            ))
        } else Result.failure(Exception("Perfil comercial no encontrado"))
    } catch (e: Exception) { Result.failure(e) }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getCompaniesInCity(city: String): Result<List<CompanyProfile>> = try {
        val snapshot = db.collection("company_profiles").whereEqualTo("city", city).get().await()
        val list = snapshot.documents.map { doc ->
            val hours = doc.get("workingHours") as? List<String>
            CompanyProfile(
                id = doc.id,
                name = doc.getString("name") ?: "",
                category = doc.getString("category") ?: "",
                description = doc.getString("description") ?: "",
                city = doc.getString("city") ?: "",
                address = doc.getString("address") ?: "",
                profileImage = doc.getString("profileImage") ?: "",
                bannerImage = doc.getString("bannerImage") ?: "",
                rating = doc.getDouble("rating") ?: 0.0,
                reviewCount = doc.getLong("reviewCount")?.toInt() ?: 0,
                workingHours = hours ?: listOf(
                    "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                    "12:00", "12:30", "13:00", "16:00", "16:30", "17:00",
                    "17:30", "18:00", "18:30", "19:00", "19:30", "20:00"
                )
            )
        }
        Result.success(list)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun updateUserProfile(data: Map<String, Any>): Result<Unit> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Sin sesión")
        db.collection("users").document(userId).update(data).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun updateCompanyProfile(data: Map<String, Any>): Result<Unit> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Sin sesión")
        db.collection("company_profiles").document(userId).set(data, com.google.firebase.firestore.SetOptions.merge()).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun deleteAccount(): Result<Unit> = try {
        val user = auth.currentUser ?: throw Exception("Sin sesión")
        db.collection("users").document(user.uid).delete().await()
        db.collection("company_profiles").document(user.uid).delete().await()
        user.delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override fun sendPasswordReset() { auth.currentUser?.email?.let { auth.sendPasswordResetEmail(it) } }
    override fun logout() { auth.signOut() }
}