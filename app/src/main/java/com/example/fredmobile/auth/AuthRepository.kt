package com.example.fredmobile.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

/**
 * Thin wrapper around FirebaseAuth.
 * Provides suspend functions for email + Google sign-in.
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?
)

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun currentUser(): AuthUser? = auth.currentUser?.toAuthUser()

    suspend fun signInWithEmail(email: String, password: String): AuthUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.toAuthUser()
    }

    /**
     * Register with email/password and optionally set a display name.
     */
    suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String?
    ): AuthUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user

        if (firebaseUser != null && !displayName.isNullOrBlank()) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()
        }

        return firebaseUser?.toAuthUser()
    }

    /**
     * Sign in using the ID token returned from Google Sign-In.
     */
    suspend fun signInWithGoogle(idToken: String): AuthUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user?.toAuthUser()
    }

    fun signOut() {
        auth.signOut()
    }

    private fun FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        email = email,
        displayName = displayName
    )
}
