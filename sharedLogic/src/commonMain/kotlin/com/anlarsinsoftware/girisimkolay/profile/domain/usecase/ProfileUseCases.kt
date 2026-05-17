package com.anlarsinsoftware.girisimkolay.profile.domain.usecase

import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.UserProfile
import com.anlarsinsoftware.girisimkolay.profile.domain.repository.ProfileRepository

class LoadProfile(private val repository: ProfileRepository) {
    suspend operator fun invoke(forceRefresh: Boolean = false) = repository.refreshProfile(forceRefresh)
}

class SaveProfile(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: UserProfile): Result<UserProfile> = repository.updateProfile(profile)
}
