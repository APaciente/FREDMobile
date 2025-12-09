package com.example.fredmobile.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

/**
 * Simple UI-facing model for an authenticated user.
 *
 * This wraps [FirebaseUser] so the rest of the app does not depend
 * on Firebase-specific types.
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?
)

/**
 * Repository that wraps [FirebaseAuth] and exposes coroutine-friendly
 * authentication operations for the app.
 *
 * Supports:
 * - Email/password sign-in and registration
 * - Google sign-in using an ID token
 * - Retrieving the current user and signing out
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Returns the currently signed-in user as [AuthUser],
     * or null if there is no authenticated user.
     */
    fun currentUser(): AuthUser? = auth.currentUser?.toAuthUser()

    /**
     * Signs in using email and password.
     *
     * @param email User's email address.
     * @param password User's password.
     * @return The signed-in [AuthUser], or null if sign-in failed.
     */
    suspend fun signInWithEmail(email: String, password: String): AuthUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.toAuthUser()
    }

    /**
     * Registers a new account with email and password.
     * Optionally sets a display name on the Firebase profile.
     *
     * @param email New account email.
     * @param password New account password.
     * @param displayName Optional display name to store in the profile.
     * @return The newly created [AuthUser], or null if registration failed.
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
     * Signs in using a Google ID token returned from the
     * Google Sign-In flow.
     *
     * @param idToken The ID token from Google.
     * @return The signed-in [AuthUser], or null if sign-in failed.
     */
    suspend fun signInWithGoogle(idToken: String): AuthUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user?.toAuthUser()
    }

    /**
     * Signs the current user out of FirebaseAuth.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Helper to convert a [FirebaseUser] into an [AuthUser].
     */
    private fun FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        email = email,
        displayName = displayName
    )
}