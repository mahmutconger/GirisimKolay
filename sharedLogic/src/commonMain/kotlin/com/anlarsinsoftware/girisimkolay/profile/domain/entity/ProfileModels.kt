package com.anlarsinsoftware.girisimkolay.profile.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val uid: String,
    val fullName: String,
    val email: String,
    val companyType: String = "",
    val entrepreneurType: String = "",
    val businessSector: String = "",
    val onboardingCompleted: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
data class ProfilingSnapshot(
    val businessIdea: String? = null,
    val businessSector: String? = null,
    val preferredCompanyType: String? = null,
    val experienceLevel: String? = null,
    val fundingNeed: String? = null,
    val legalConcerns: List<String> = emptyList()
)
