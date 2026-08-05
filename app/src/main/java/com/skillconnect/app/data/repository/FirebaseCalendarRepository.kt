package com.skillconnect.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

data class CloudCalendarEvent(
    val id: String = "",
    val title: String = "",
    val time: String = "",
    val tag: String = "",
    val initials: String = "",
    val categoryTab: String = "Clases",
    val partnerEmail: String = "",
    val partnerName: String = "",
    val status: String = "ACEPTADO"
)

class FirebaseCalendarRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getEvents(email: String, tab: String): Flow<List<CloudCalendarEvent>> = callbackFlow {
        val listener = db.collection("users").document(email).collection("calendar")
            .whereEqualTo("categoryTab", tab)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val events = snapshot.documents.mapNotNull { it.toObject(CloudCalendarEvent::class.java) }
                    trySend(events).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    fun addEvent(email: String, event: CloudCalendarEvent) {
        val eventId = if (event.id.isBlank()) UUID.randomUUID().toString() else event.id
        val newEvent = event.copy(id = eventId)
        db.collection("users").document(email).collection("calendar").document(eventId).set(newEvent)
            .addOnFailureListener { e -> Log.e("Firebase", "Error saving calendar event", e) }
    }

    fun deleteEvent(email: String, eventId: String) {
        if (email.isBlank() || eventId.isBlank()) return
        db.collection("users").document(email).collection("calendar").document(eventId).delete()
            .addOnFailureListener { e -> Log.e("Firebase", "Error deleting calendar event", e) }
    }
}
