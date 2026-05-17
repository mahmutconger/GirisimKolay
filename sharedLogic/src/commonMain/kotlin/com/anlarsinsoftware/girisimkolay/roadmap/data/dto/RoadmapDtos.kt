package com.anlarsinsoftware.girisimkolay.roadmap.data.dto

import com.anlarsinsoftware.girisimkolay.chat.data.dto.ProfilingSnapshotDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateReportRequest(
    @SerialName("session_id") val sessionId: String,
    @SerialName("profile") val profile: ProfilingSnapshotDto? = null,
    @SerialName("user_id") val userId: String? = null
)

@Serializable
data class RoadmapReportDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("session_id") val sessionId: String,
    @SerialName("title") val title: String,
    @SerialName("summary") val summary: String,
    @SerialName("file_url") val fileUrl: String,
    @SerialName("generated_at") val generatedAt: Long,
    @SerialName("approval_status") val approvalStatus: String = "IDLE",
    @SerialName("next_actions") val nextActions: List<String> = emptyList()
)
