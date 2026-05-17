package com.anlarsinsoftware.girisimkolay.roadmap.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class RoadmapStep(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isActive: Boolean
)

@Serializable
enum class ApprovalStatus {
    IDLE,
    PENDING,
    SENT
}

@Serializable
data class RoadmapReport(
    val id: String,
    val userId: String,
    val sessionId: String,
    val title: String,
    val summary: String,
    val fileUrl: String,
    val generatedAt: Long,
    val approvalStatus: ApprovalStatus = ApprovalStatus.IDLE,
    val nextActions: List<String> = emptyList()
)
