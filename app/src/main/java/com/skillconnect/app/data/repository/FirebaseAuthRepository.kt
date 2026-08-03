package com.skillconnect.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.tasks.await

enum class AuthFailure {
    UserNotFound,
    InvalidCredentials,
    EmailAlreadyInUse,
    NetworkOrServer
}

class FirebaseAuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado tras el inicio de sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loginFailure(error: Throwable?): AuthFailure {
        return when (error) {
            is FirebaseAuthInvalidUserException ->
            AuthFailure.UserNotFound
            is FirebaseAuthInvalidCredentialsException ->
            AuthFailure.InvalidCredentials
            else ->
            AuthFailure.NetworkOrServer
        }
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Error al crear usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun registerFailure(error: Throwable?): AuthFailure {
        return when (error) {
            is FirebaseAuthUserCollisionException ->
            AuthFailure.EmailAlreadyInUse
            is FirebaseAuthInvalidCredentialsException ->
            AuthFailure.InvalidCredentials
            else ->
            AuthFailure.NetworkOrServer
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        auth.signOut()
    }
}
