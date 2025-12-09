package com.example.fredmobile.data

import android.net.Uri
import com.example.fredmobile.model.firestore.CheckIn
import com.example.fredmobile.model.firestore.Incident
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository that wraps Firestore access for check-ins and incidents.
 *
 * All operations are scoped to the currently authenticated user and
 * expose suspend functions for use with coroutines.
 */
class FirestoreRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val storage = FirebaseStorage.getInstance()

    /**
     * Convenience property to access the current user's UID.
     *
     * @throws IllegalStateException if there is no logged-in user.
     */
    private val uid: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("No logged-in user")

    // ---- CHECK-INS ----

    /**
     * Creates a new check-in document for the current user.
     *
     * @param siteId Identifier of the site where the user is checking in.
     * @param siteName Human-readable name of the site.
     * @return The created [CheckIn] model.
     */
    suspend fun createCheckIn(siteId: String, siteName: String): CheckIn {
        val ref = db.collection("users")
            .document(uid)
            .collection("checkins")
            .document()

        val checkIn = CheckIn(
            id = ref.id,
            siteId = siteId,
            siteName = siteName,
            inTime = Timestamp.now(),
            status = "IN_PROGRESS"
        )

        ref.set(checkIn).await()
        return checkIn
    }

    /**
     * Marks a check-in as completed by setting an out time and status.
     *
     * @param checkInId ID of the check-in document to update.
     */
    suspend fun completeCheckIn(checkInId: String) {
        val ref = db.collection("users")
            .document(uid)
            .collection("checkins")
            .document(checkInId)

        ref.update(
            mapOf(
                "outTime" to Timestamp.now(),
                "status" to "COMPLETED"
            )
        ).await()
    }

    /**
     * Deletes a check-in document.
     *
     * @param checkInId ID of the check-in document to delete.
     */
    suspend fun deleteCheckIn(checkInId: String) {
        db.collection("users")
            .document(uid)
            .collection("checkins")
            .document(checkInId)
            .delete()
            .await()
    }

    /**
     * Returns all check-ins for the current user, ordered by in time.
     */
    suspend fun getCheckInsOnce(): List<CheckIn> {
        val snapshot = db.collection("users")
            .document(uid)
            .collection("checkins")
            .orderBy("inTime")
            .get()
            .await()

        return snapshot.toObjects(CheckIn::class.java)
    }

    // ---- INCIDENTS ----

    /**
     * Creates a new incident for the current user and optionally uploads
     * an associated photo to Firebase Storage.
     *
     * @param siteId Identifier of the site where the incident occurred.
     * @param siteName Human-readable name of the site.
     * @param severity Incident severity level.
     * @param description Text description of what happened.
     * @param localPhotoUri Optional local URI for a photo to attach; if provided,
     *                      the image is uploaded and the download URL is stored
     *                      in the incident document.
     * @return The created [Incident] model.
     */
    suspend fun createIncidentWithOptionalPhoto(
        siteId: String,
        siteName: String,
        severity: String,
        description: String,
        localPhotoUri: String?
    ): Incident {
        val ref = db.collection("users")
            .document(uid)
            .collection("incidents")
            .document()

        val photoUrl: String? = if (!localPhotoUri.isNullOrBlank()) {
            uploadIncidentPhoto(uid, localPhotoUri)
        } else {
            null
        }

        val incident = Incident(
            id = ref.id,
            siteId = siteId,
            siteName = siteName,
            severity = severity,
            description = description,
            createdAt = Timestamp.now(),
            photoUrl = photoUrl
        )

        ref.set(incident).await()
        return incident
    }

    /**
     * Uploads an incident photo to Firebase Storage and returns its download URL.
     *
     * @param uid UID of the user the photo belongs to.
     * @param localPhotoUri Local URI of the image on the device.
     * @return Public download URL of the uploaded image.
     */
    private suspend fun uploadIncidentPhoto(
        uid: String,
        localPhotoUri: String
    ): String {
        val fileName = UUID.randomUUID().toString() + ".jpg"

        val ref = storage.reference
            .child("incidents")
            .child(uid)
            .child(fileName)

        val uri = Uri.parse(localPhotoUri)

        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    /**
     * Deletes an incident document.
     *
     * @param incidentId ID of the incident document to delete.
     */
    suspend fun deleteIncident(incidentId: String) {
        db.collection("users")
            .document(uid)
            .collection("incidents")
            .document(incidentId)
            .delete()
            .await()
    }

    /**
     * Returns all incidents for the current user, ordered by creation time.
     */
    suspend fun getIncidentsOnce(): List<Incident> {
        val snapshot = db.collection("users")
            .document(uid)
            .collection("incidents")
            .orderBy("createdAt")
            .get()
            .await()

        return snapshot.toObjects(Incident::class.java)
    }
}
