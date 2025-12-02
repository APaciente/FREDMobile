package com.example.fredmobile.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Small wrapper around FirebaseAuth.
 * ViewModels use this instead of calling Firebase directly.
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val currentUser get() = firebaseAuth.currentUser

    suspend fun signIn(email: String, password: String): Result<Unit> =
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun signUp(email: String, password: String): Result<Unit> =
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
