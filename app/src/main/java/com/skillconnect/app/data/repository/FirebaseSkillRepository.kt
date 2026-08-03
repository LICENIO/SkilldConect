package com.skillconnect.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseSkillRepository {

    // Inicializamos la conexión a Firestore
    private val db = FirebaseFirestore.getInstance()

    // Referencia a una colección en la nube (ej. "skills")
    private val skillsCollection = db.collection("skills")

    /**
     * Esta función permite "escuchar" en tiempo real todos los cambios que ocurran en la nube.
     * Si el Dispositivo A agrega algo, el Dispositivo B lo recibirá al instante a través de este Flow.
     */
    fun getRealtimeSkills(): Flow<List<Map<String, Any>>> = callbackFlow {
        val listenerRegistration = skillsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Firebase", "Error al escuchar cambios en Firestore", error)
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Mapeamos los documentos obtenidos de la nube
                val skills = snapshot.documents.mapNotNull { it.data }
                // Enviamos los nuevos datos a la UI (pantalla)
                trySend(skills).isSuccess
            }
        }

        // Esto se ejecuta cuando el usuario cierra la pantalla, para ahorrar recursos
        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**
     * Esta función permite enviar un nuevo dato a la nube desde un dispositivo.
     */
    fun addSkill(skillData: Map<String, Any>) {
        skillsCollection.add(skillData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Documento enviado exitosamente a Firebase con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error enviando el documento a Firebase", e)
            }
    }
}
