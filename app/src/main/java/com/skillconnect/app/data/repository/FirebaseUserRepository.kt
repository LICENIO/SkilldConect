package com.skillconnect.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class CloudCourse(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Int = 0,
    val syllabus: List<String> = emptyList()
)

data class UserProfile(
    val email: String = "",
    val name: String = "",
    val initials: String = "",
    val role: String = "Ambos",
    val description: String = "",
    val teachSkills: List<String> = emptyList(),
    val learnSkills: List<String> = emptyList(),
    val hourlyRate: Int = 0,
    val rating: Double = 5.0,
    val reviewCount: Int = 0,
    val availability: List<String> = listOf("Por coordinar"),
    val courses: List<CloudCourse> = emptyList()
)

class FirebaseUserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun saveUserProfile(profile: UserProfile) {
        try {
            usersCollection.document(profile.email).set(profile, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("Firebase", "Error saving user profile", e)
        }
    }

    suspend fun getUserProfile(email: String): UserProfile? {
        return try {
            val doc = usersCollection.document(email).get().await()
            doc.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            Log.e("Firebase", "Error getting user profile", e)
            null
        }
    }

    fun getRealtimeUsers(): Flow<List<UserProfile>> = callbackFlow {
        val listener = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val users = snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java) }
                trySend(users).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }
}
