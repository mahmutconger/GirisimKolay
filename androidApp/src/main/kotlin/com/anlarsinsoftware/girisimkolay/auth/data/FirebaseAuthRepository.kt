package com.anlarsinsoftware.girisimkolay.auth.data

import com.anlarsinsoftware.girisimkolay.auth.domain.entity.AuthUser
import com.anlarsinsoftware.girisimkolay.auth.domain.repository.AuthRepository
import com.anlarsinsoftware.girisimkolay.core.domain.SessionStateStore
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.roadmap.data.RoadmapLocalStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sessionStateStore: SessionStateStore,
    private val roadmapLocalStore: RoadmapLocalStore
) : AuthRepository {

    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            trySend(
                user?.let {
                    AuthUser(
                        uid = it.uid,
                        email = it.email ?: "",
                        displayName = it.displayName ?: ""
                    )
                }
            )
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): Result<AuthUser> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user!!
        Result.Success(
            AuthUser(uid = user.uid, email = user.email ?: "", displayName = user.displayName ?: "")
        )
    } catch (e: Exception) {
        Result.Error(e.message ?: "Giriş başarısız.")
    }

    override suspend fun signUp(email: String, password: String, fullName: String): Result<AuthUser> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user!!

        // Update display name
        user.updateProfile(userProfileChangeRequest { displayName = fullName }).await()

        // Save user profile to Firestore
        firestore.collection("users").document(user.uid).set(
            mapOf(
                "uid" to user.uid,
                "email" to email,
                "fullName" to fullName,
                "companyType" to "",
                "entrepreneurType" to "",
                "businessSector" to "",
                "onboardingCompleted" to false,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
        ).await()

        Result.Success(
            AuthUser(uid = user.uid, email = email, displayName = fullName)
        )
    } catch (e: Exception) {
        Result.Error(e.message ?: "Kayıt başarısız.")
    }

    override suspend fun signOut(): Result<Unit> = try {
        auth.signOut()
        sessionStateStore.clear()
        roadmapLocalStore.clear()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Çıkış başarısız.")
    }

    override fun currentUserId(): String? = auth.currentUser?.uid
}
