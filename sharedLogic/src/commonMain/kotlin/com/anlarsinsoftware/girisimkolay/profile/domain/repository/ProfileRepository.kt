package com.anlarsinsoftware.girisimkolay.profile.domain.repository

import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.ProfilingSnapshot
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun refreshProfile(forceRefresh: Boolean = false): Result<UserProfile?>
    suspend fun updateProfile(profile: UserProfile): Result<UserProfile>
    suspend fun extractSnapshot(
        freeformText: String,
        currentSnapshot: ProfilingSnapshot? = null
    ): Result<ProfilingSnapshot>
}
