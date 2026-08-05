package com.skillconnect.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

data class CloudMessage(
    val id: String = "",
    val senderEmail: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val fileUrl: String = "",
    val fileName: String = "",
    val fileType: String = "" // "PDF" or ""
)

class FirebaseChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val chatsCollection = db.collection("chats")

    fun getMessages(chatId: String): Flow<List<CloudMessage>> = callbackFlow {
        val listener = chatsCollection.document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val msgs = snapshot.documents.mapNotNull { it.toObject(CloudMessage::class.java) }
                    trySend(msgs).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    fun sendMessage(chatId: String, senderEmail: String, text: String) {
        val msgId = UUID.randomUUID().toString()
        val msg = CloudMessage(
            id = msgId,
            senderEmail = senderEmail,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        chatsCollection.document(chatId).collection("messages").document(msgId).set(msg)
            .addOnFailureListener { e -> Log.e("Firebase", "Error sending message", e) }
    }

    fun sendFileMessage(chatId: String, senderEmail: String, text: String, fileUrl: String, fileName: String, fileType: String = "PDF") {
        val msgId = UUID.randomUUID().toString()
        val msg = CloudMessage(
            id = msgId,
            senderEmail = senderEmail,
            text = text,
            timestamp = System.currentTimeMillis(),
            fileUrl = fileUrl,
            fileName = fileName,
            fileType = fileType
        )
        chatsCollection.document(chatId).collection("messages").document(msgId).set(msg)
            .addOnFailureListener { e -> Log.e("Firebase", "Error sending PDF file message", e) }
    }
}
