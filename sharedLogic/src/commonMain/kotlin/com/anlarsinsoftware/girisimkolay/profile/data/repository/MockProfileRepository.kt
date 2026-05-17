package com.anlarsinsoftware.girisimkolay.profile.data.repository

import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultClock
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.ProfilingSnapshot
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.UserProfile
import com.anlarsinsoftware.girisimkolay.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockProfileRepository(
    private val clock: Clock = DefaultClock
) : ProfileRepository {
    private val profileState = MutableStateFlow(
        UserProfile(
            uid = "mock-user",
            fullName = "Mahmut Can",
            email = "mahmut@example.com",
            companyType = "Şahıs Şirketi",
            entrepreneurType = "Evden E-Ticaret",
            businessSector = "Perakende",
            onboardingCompleted = true,
            createdAt = clock.nowMillis(),
            updatedAt = clock.nowMillis()
        )
    )

    override fun observeProfile(): Flow<UserProfile?> = profileState.asStateFlow()

    override suspend fun refreshProfile(forceRefresh: Boolean): Result<UserProfile?> =
        Result.Success(profileState.value)

    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> {
        val updated = profile.copy(updatedAt = clock.nowMillis())
        profileState.value = updated
        return Result.Success(updated)
    }

    override suspend fun extractSnapshot(
        freeformText: String,
        currentSnapshot: ProfilingSnapshot?
    ): Result<ProfilingSnapshot> {
        val normalized = freeformText.lowercase()
        val snapshot = ProfilingSnapshot(
            businessIdea = freeformText,
            businessSector = when {
                "ihracat" in normalized -> "İhracat"
                "yemek" in normalized -> "Gıda"
                else -> currentSnapshot?.businessSector ?: "Genel Girişim"
            },
            preferredCompanyType = if ("şahıs" in normalized) "Şahıs Şirketi" else currentSnapshot?.preferredCompanyType,
            experienceLevel = currentSnapshot?.experienceLevel ?: "Yeni Başlayan",
            fundingNeed = if ("hibe" in normalized) "KOSGEB / TÜBİTAK Desteği" else currentSnapshot?.fundingNeed,
            legalConcerns = (currentSnapshot?.legalConcerns ?: emptyList()) + listOfNotNull(
                "Vergi ve şirket kuruluşu"
                    .takeIf { "vergi" in normalized || "şirket" in normalized }
            )
        )
        return Result.Success(snapshot)
    }
}
