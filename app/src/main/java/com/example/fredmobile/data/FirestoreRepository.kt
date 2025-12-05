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
 * Repository wrapping Firestore access for check-ins and incidents.
 *
 * PM3 goal: demonstrate full CRUD using authenticated user data.
 */
class FirestoreRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // storage lives here, as a property:
    private val storage = FirebaseStorage.getInstance()

    // Convenience to get current UID or throw
    private val uid: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("No logged-in user")

    // ---- CHECK-INS ----

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

    suspend fun deleteCheckIn(checkInId: String) {
        db.collection("users")
            .document(uid)
            .collection("checkins")
            .document(checkInId)
            .delete()
            .await()
    }

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

    suspend fun createIncidentWithOptionalPhoto(
        siteId: String,
        siteName: String,
        severity: String,
        description: String,
        localPhotoUri: String?   // can be null
    ): Incident {
        val ref = db.collection("users")
            .document(uid)
            .collection("incidents")
            .document()

        // 1) Upload photo if we have one
        val photoUrl: String? = if (!localPhotoUri.isNullOrBlank()) {
            uploadIncidentPhoto(uid, localPhotoUri)
        } else {
            null
        }

        // 2) Build incident
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

    suspend fun deleteIncident(incidentId: String) {
        db.collection("users")
            .document(uid)
            .collection("incidents")
            .document(incidentId)
            .delete()
            .await()
    }

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
