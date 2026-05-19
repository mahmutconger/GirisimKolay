package com.anlarsinsoftware.girisimkolay.auth.data

import android.util.Log
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ProfileExtractRequest
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ProfilingSnapshotDto
import com.anlarsinsoftware.girisimkolay.core.data.MemoryCache
import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.Logger
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.ProfilingSnapshot
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.UserProfile
import com.anlarsinsoftware.girisimkolay.profile.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirestoreProfileRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions,
    clock: Clock,
    private val logger: Logger
) : ProfileRepository {
    private val profileState = MutableStateFlow<UserProfile?>(null)
    private val cache = MemoryCache<String, UserProfile>(clock = clock, ttlMillis = PROFILE_CACHE_TTL_MS)

    override fun observeProfile(): Flow<UserProfile?> = profileState.asStateFlow()

    override suspend fun refreshProfile(forceRefresh: Boolean): Result<UserProfile?> {
        val uid = auth.currentUser?.uid ?: return Result.Success(null)
        if (!forceRefresh) {
            cache.get(uid)?.let {
                profileState.value = it
                return Result.Success(it)
            }
        }

        return try {
            val snapshot = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            if (!snapshot.exists()) {
                profileState.value = null
                Result.Success(null)
            } else {
                val profile = snapshot.toUserProfile(uid)
                cache.put(uid, profile)
                profileState.value = profile
                Result.Success(profile)
            }
        } catch (exception: Exception) {
            logger.error("FirestoreProfileRepository", "Profile refresh failed", exception)
            Result.Error(
                message = "Profil bilgileri alınamadı.",
                throwable = exception,
                code = "profile_refresh_failed",
                isRetryable = true
            )
        }
    }

    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> {
        val uid = auth.currentUser?.uid ?: profile.uid
        return try {
            val updated = profile.copy(
                uid = uid,
                updatedAt = System.currentTimeMillis()
            )
            firestore.collection(USERS_COLLECTION).document(uid).set(updated.toFirestore()).await()
            cache.put(uid, updated)
            profileState.value = updated
            Result.Success(updated)
        } catch (exception: Exception) {
            logger.error("FirestoreProfileRepository", "Profile update failed", exception)
            Result.Error(
                message = "Profil güncellenemedi.",
                throwable = exception,
                code = "profile_update_failed",
                isRetryable = true
            )
        }
    }

    override suspend fun extractSnapshot(
        freeformText: String,
        currentSnapshot: ProfilingSnapshot?
    ): Result<ProfilingSnapshot> {
        return try {
            // Force token refresh to ensure authentication is sent correctly to the function
            try {
                auth.currentUser?.getIdToken(true)?.await()
                Log.d("FirestoreProfileRepo", "Auth token refreshed successfully.")
            } catch (e: Exception) {
                Log.w("FirestoreProfileRepo", "Failed to refresh auth token: ${e.message}")
            }

            val result = functions
                .getHttpsCallable("extractProfileSnapshot")
                .call(
                    mapOf(
                        "text" to freeformText,
                        "currentProfile" to currentSnapshot?.toDto()?.toMap()
                    )
                )
                .await()
                .data as? Map<*, *> ?: error("Malformed profile extract response.")
            val snapshotMap = result["snapshot"] as? Map<*, *> ?: error("Missing snapshot payload.")
            Result.Success(snapshotMap.toDomain())
        } catch (exception: Exception) {
            logger.error("FirestoreProfileRepository", "Profile extraction failed", exception)
            Result.Error(
                message = "Profil analizi şu anda üretilemiyor.",
                throwable = exception,
                code = "profile_extract_failed",
                isRetryable = true
            )
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toUserProfile(uid: String): UserProfile = UserProfile(
        uid = uid,
        fullName = getString("fullName").orEmpty(),
        email = getString("email").orEmpty(),
        companyType = getString("companyType").orEmpty(),
        entrepreneurType = getString("entrepreneurType").orEmpty(),
        businessSector = getString("businessSector").orEmpty(),
        onboardingCompleted = getBoolean("onboardingCompleted") ?: false,
        createdAt = getTimestamp("createdAt")?.toDate()?.time ?: 0L,
        updatedAt = getTimestamp("updatedAt")?.toDate()?.time ?: 0L
    )

    private fun UserProfile.toFirestore(): Map<String, Any> = mapOf(
        "uid" to uid,
        "fullName" to fullName,
        "email" to email,
        "companyType" to companyType,
        "entrepreneurType" to entrepreneurType,
        "businessSector" to businessSector,
        "onboardingCompleted" to onboardingCompleted,
        "createdAt" to com.google.firebase.Timestamp(createdAt / 1000, 0),
        "updatedAt" to com.google.firebase.Timestamp.now()
    )

    private fun ProfilingSnapshot.toDto(): ProfilingSnapshotDto = ProfilingSnapshotDto(
        businessIdea = businessIdea,
        businessSector = businessSector,
        preferredCompanyType = preferredCompanyType,
        experienceLevel = experienceLevel,
        fundingNeed = fundingNeed,
        legalConcerns = legalConcerns
    )

    private companion object {
        const val USERS_COLLECTION = "users"
        const val PROFILE_CACHE_TTL_MS = 5 * 60 * 1000L
    }
}

private fun ProfilingSnapshotDto.toMap(): Map<String, Any?> = mapOf(
    "businessIdea" to businessIdea,
    "businessSector" to businessSector,
    "preferredCompanyType" to preferredCompanyType,
    "experienceLevel" to experienceLevel,
    "fundingNeed" to fundingNeed,
    "legalConcerns" to legalConcerns
)

private fun Map<*, *>.toDomain(): ProfilingSnapshot = ProfilingSnapshot(
    businessIdea = this["businessIdea"] as? String ?: this["business_idea"] as? String,
    businessSector = this["businessSector"] as? String ?: this["business_sector"] as? String,
    preferredCompanyType = this["preferredCompanyType"] as? String ?: this["preferred_company_type"] as? String,
    experienceLevel = this["experienceLevel"] as? String ?: this["experience_level"] as? String,
    fundingNeed = this["fundingNeed"] as? String ?: this["funding_need"] as? String,
    legalConcerns = (this["legalConcerns"] as? List<*>)?.mapNotNull { it as? String }
        ?: (this["legal_concerns"] as? List<*>)?.mapNotNull { it as? String }
        ?: emptyList()
)
