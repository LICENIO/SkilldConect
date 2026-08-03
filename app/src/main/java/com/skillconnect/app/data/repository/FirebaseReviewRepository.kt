package com.skillconnect.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

data class CloudReview(
    val id: String = "",
    val reviewerEmail: String = "",
    val reviewerName: String = "",
    val rating: Double = 5.0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class FirebaseReviewRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getReviews(targetEmail: String): Flow<List<CloudReview>> = callbackFlow {
        val listener = db.collection("users").document(targetEmail).collection("reviews")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reviews = snapshot.documents.mapNotNull { it.toObject(CloudReview::class.java) }
                        .sortedByDescending { it.timestamp }
                    trySend(reviews).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    fun addReview(targetEmail: String, review: CloudReview) {
        val reviewId = UUID.randomUUID().toString()
        val newReview = review.copy(id = reviewId)
        db.collection("users").document(targetEmail).collection("reviews").document(reviewId).set(newReview)
            .addOnSuccessListener {
                updateAverageRating(targetEmail)
            }
            .addOnFailureListener { e -> Log.e("Firebase", "Error saving review", e) }
    }

    private fun updateAverageRating(targetEmail: String) {
        db.collection("users").document(targetEmail).collection("reviews").get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.documents.mapNotNull { it.toObject(CloudReview::class.java) }
                if (reviews.isNotEmpty()) {
                    val average = reviews.map { it.rating }.average()
                    db.collection("users").document(targetEmail)
                        .update(mapOf(
                            "rating" to average,
                            "reviewCount" to reviews.size
                        ))
                }
            }
    }
}
