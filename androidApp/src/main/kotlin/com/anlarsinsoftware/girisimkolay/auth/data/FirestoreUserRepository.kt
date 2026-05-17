package com.anlarsinsoftware.girisimkolay.auth.data

import com.anlarsinsoftware.girisimkolay.chat.data.dto.ProfileExtractRequest
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ProfileExtractResponse
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ProfilingSnapshotDto
import com.anlarsinsoftware.girisimkolay.core.domain.BearerTokenProvider
import com.anlarsinsoftware.girisimkolay.core.data.MemoryCache
import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.Logger
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.ProfilingSnapshot
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.UserProfile
import com.anlarsinsoftware.girisimkolay.profile.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirestoreProfileRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val authTokenProvider: BearerTokenProvider,
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
        val token = authTokenProvider.getFreshToken()
            ?: return Result.Error(message = "Kimlik doğrulama gerekli.", code = "missing_token")
        return try {
            val response: ProfileExtractResponse = httpClient.post("$baseUrl/api/v1/profile/extract") {
                contentType(ContentType.Application.Json)
                headers.append(HttpHeaders.Authorization, "Bearer $token")
                setBody(
                    ProfileExtractRequest(
                        text = freeformText,
                        currentProfile = currentSnapshot?.toDto()
                    )
                )
            }.body()
            Result.Success(response.snapshot.toDomain())
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

    private fun ProfilingSnapshotDto.toDomain(): ProfilingSnapshot = ProfilingSnapshot(
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
