package com.skillconnect.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

data class CloudExchange(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val initials: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class FirebaseExchangeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val exchangesCollection = db.collection("exchanges")

    fun getExchanges(): Flow<List<CloudExchange>> = callbackFlow {
        val listener = exchangesCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(CloudExchange::class.java) }
                        .sortedByDescending { it.timestamp }
                    trySend(items).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    fun addExchange(exchange: CloudExchange) {
        val exchangeId = UUID.randomUUID().toString()
        val newExchange = exchange.copy(id = exchangeId)
        exchangesCollection.document(exchangeId).set(newExchange)
            .addOnFailureListener { e -> Log.e("Firebase", "Error saving exchange", e) }
    }
}
