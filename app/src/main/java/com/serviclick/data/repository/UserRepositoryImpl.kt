package com.serviclick.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.serviclick.domain.model.CompanyProfile
import com.serviclick.domain.model.UserProfile
import com.serviclick.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementación concreta del repositorio de usuarios conectada a Firebase.
 * Centraliza todas las operaciones de lectura y escritura relacionadas con la identidad del usuario
 * y su perfil comercial en Firestore.
 *
 * Usa las APIs de Firebase Auth (para identidad) y Firebase Firestore (para almacenamiento de datos).
 * Se inyecta mediante Dagger Hilt (`@Inject`). Convierte los `Task` de Firebase en `Coroutines`
 * mediante `.await()` para manejar el asincronismo de forma secuencial y limpia.
 */
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /** Obtiene el email del usuario actualmente logueado en Firebase Auth. */
    override fun getCurrentUserEmail(): String = auth.currentUser?.email ?: ""

    /**
     * Recupera el perfil básico del usuario desde Firestore.
     * Retorna un Result encapsulado para un manejo seguro de errores en la capa de UI.
     */
    override suspend fun getUserProfile(): Result<UserProfile> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Sin sesión")
        val doc = db.collection("users").document(userId).get().await()
        if (doc.exists()) {
            Result.success(
                UserProfile(
                    id = doc.id,
                    email = auth.currentUser?.email ?: "",
                    role = doc.getString("role") ?: "cliente",
                    name = doc.getString("name") ?: "",
                    phone = doc.getString("phone") ?: "",
                    city = doc.getString("city") ?: "",
                    address = doc.getString("address") ?: "",
                    profileImage = doc.getString("profileImage") ?: "",
                    language = doc.getString("language") ?: "Español",
                    mockCard = doc.getString("mockCard") ?: ""
                )
            )
        } else Result.failure(Exception("Usuario no encontrado"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Recupera el perfil comercial de una empresa concreta.
     * Aplica parseo defensivo en listas complejas (horarios, pagos y agenda) para evitar crashes.
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun getCompanyProfile(id: String): Result<CompanyProfile> = try {
        val doc = db.collection("company_profiles").document(id).get().await()
        if (doc.exists()) {
            val hours = doc.get("workingHours") as? List<String>
            val payments = doc.get("acceptedPayments") as? List<String>

            // Firebase puede devolver números pequeños como Int y grandes como Long.
            // Pasar por 'Number' asegura que independientemente de cómo se guardó, la app lo trate como Long.
            val bDates = doc.get("blockedDates") as? List<*>
            val blockedDatesLong = bDates?.mapNotNull { (it as? Number)?.toLong() } ?: emptyList()

            val bDays = doc.get("blockedDaysOfWeek") as? List<*>
            val blockedDaysLong = bDays?.mapNotNull { (it as? Number)?.toLong() } ?: emptyList()

            Result.success(
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
                    ),
                    acceptedPayments = payments ?: listOf("Tarjeta", "Efectivo"),
                    blockedDates = blockedDatesLong,
                    blockedDaysOfWeek = blockedDaysLong
                )
            )
        } else Result.failure(Exception("Perfil comercial no encontrado"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Búsqueda de empresas por ciudad.
     * Retorna una lista de perfiles mapeando cada documento recuperado mediante una Query simple de Firestore.
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun getCompaniesInCity(city: String): Result<List<CompanyProfile>> = try {
        val snapshot = db.collection("company_profiles").whereEqualTo("city", city).get().await()
        val list = snapshot.documents.map { doc ->
            val hours = doc.get("workingHours") as? List<String>
            val payments = doc.get("acceptedPayments") as? List<String>

            val bDates = doc.get("blockedDates") as? List<*>
            val blockedDatesLong = bDates?.mapNotNull { (it as? Number)?.toLong() } ?: emptyList()

            val bDays = doc.get("blockedDaysOfWeek") as? List<*>
            val blockedDaysLong = bDays?.mapNotNull { (it as? Number)?.toLong() } ?: emptyList()

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
                ),
                acceptedPayments = payments ?: listOf("Tarjeta", "Efectivo"),
                blockedDates = blockedDatesLong,
                blockedDaysOfWeek = blockedDaysLong
            )
        }
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Actualiza campos específicos del perfil personal en la colección de usuarios. */
    override suspend fun updateUserProfile(data: Map<String, Any>): Result<Unit> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Sin sesión")
        db.collection("users").document(userId).update(data).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Crea o actualiza el perfil comercial usando SetOptions.merge().
     * Esto asegura que los campos no enviados en 'data' no se sobrescriban o borren.
     */
    override suspend fun updateCompanyProfile(data: Map<String, Any>): Result<Unit> = try {
        val userId = auth.currentUser?.uid ?: throw Exception("Sin sesión")
        db.collection("company_profiles").document(userId)
            .set(data, com.google.firebase.firestore.SetOptions.merge()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Borrado en cascada: Elimina registros públicos antes de borrar la cuenta en Authentication. */
    override suspend fun deleteAccount(): Result<Unit> = try {
        val user = auth.currentUser ?: throw Exception("Sin sesión")
        db.collection("users").document(user.uid).delete().await()
        db.collection("company_profiles").document(user.uid).delete().await()
        user.delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Envía un correo de reseteo automático de Firebase al email registrado. */
    override fun sendPasswordReset() {
        auth.currentUser?.email?.let { auth.sendPasswordResetEmail(it) }
    }

    /** Cierra la sesión activa en el SDK de Firebase. */
    override fun logout() {
        auth.signOut()
    }
}