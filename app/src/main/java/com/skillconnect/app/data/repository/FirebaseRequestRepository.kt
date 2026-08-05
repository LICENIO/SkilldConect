package com.skillconnect.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

data class CloudRequest(
    val id: String = "",
    val senderEmail: String = "",
    val senderName: String = "",
    val senderInitials: String = "SC",
    val recipientEmail: String = "",
    val recipientName: String = "",
    val type: String = "TRUEQUE", // "TRUEQUE" or "CLASE"
    val teachSkill: String = "",
    val learnSkill: String = "",
    val dateOrTime: String = "",
    val message: String = "",
    val status: String = "PENDIENTE", // "PENDIENTE", "ACEPTADO", "RECHAZADO"
    val timestamp: Long = System.currentTimeMillis()
)

class FirebaseRequestRepository {
    private val db = FirebaseFirestore.getInstance()
    private val requestsCollection = db.collection("requests")

    fun getRequestsForUser(email: String): Flow<List<CloudRequest>> = callbackFlow {
        val listener = requestsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val requests = snapshot.documents.mapNotNull { it.toObject(CloudRequest::class.java) }
                        .filter { 
                            it.recipientEmail.equals(email, ignoreCase = true) || 
                            it.senderEmail.equals(email, ignoreCase = true) 
                        }
                        .sortedByDescending { it.timestamp }
                    trySend(requests).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    fun createRequest(request: CloudRequest) {
        val requestId = if (request.id.isBlank()) UUID.randomUUID().toString() else request.id
        val newReq = request.copy(id = requestId)
        requestsCollection.document(requestId).set(newReq)
            .addOnFailureListener { e -> Log.e("Firebase", "Error saving request", e) }
    }

    fun updateRequestStatus(requestId: String, newStatus: String) {
        requestsCollection.document(requestId).update("status", newStatus)
            .addOnFailureListener { e -> Log.e("Firebase", "Error updating request status", e) }
    }

    fun deleteRequest(requestId: String) {
        if (requestId.isBlank()) return
        requestsCollection.document(requestId).delete()
            .addOnFailureListener { e -> Log.e("Firebase", "Error deleting request", e) }
    }
}
