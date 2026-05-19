package com.anlarsinsoftware.girisimkolay.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfilingSnapshotDto(
    @SerialName("business_idea") val businessIdea: String? = null,
    @SerialName("business_sector") val businessSector: String? = null,
    @SerialName("preferred_company_type") val preferredCompanyType: String? = null,
    @SerialName("experience_level") val experienceLevel: String? = null,
    @SerialName("funding_need") val fundingNeed: String? = null,
    @SerialName("legal_concerns") val legalConcerns: List<String> = emptyList()
)

@Serializable
data class CitationDto(
    @SerialName("source_name") val sourceName: String,
    @SerialName("section") val section: String? = null,
    @SerialName("snippet") val snippet: String? = null,
    @SerialName("source_url") val sourceUrl: String? = null
)

@Serializable
data class ChatRequest(
    @SerialName("text") val text: String,
    @SerialName("session_id") val sessionId: String? = null,
    @SerialName("client_request_id") val clientRequestId: String
)

@Serializable
data class ChatResponseMessageDto(
    @SerialName("id") val id: String,
    @SerialName("session_id") val sessionId: String,
    @SerialName("text") val text: String,
    @SerialName("is_from_user") val isFromUser: Boolean,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("citations") val citations: List<CitationDto> = emptyList(),
    @SerialName("profile_delta") val profileDelta: ProfilingSnapshotDto? = null,
    @SerialName("confidence") val confidence: Double? = null,
    @SerialName("next_actions") val nextActions: List<String> = emptyList(),
    @SerialName("mode") val mode: String = "NORMAL"
)

@Serializable
data class ChatResponse(
    @SerialName("session_id") val sessionId: String,
    @SerialName("message") val message: ChatResponseMessageDto,
    @SerialName("answer") val answer: String,
    @SerialName("citations") val citations: List<CitationDto> = emptyList(),
    @SerialName("profile_delta") val profileDelta: ProfilingSnapshotDto? = null,
    @SerialName("confidence") val confidence: Double = 0.0,
    @SerialName("next_actions") val nextActions: List<String> = emptyList(),
    @SerialName("insufficient_evidence") val insufficientEvidence: Boolean = false
)

@Serializable
data class ChatSessionResponse(
    @SerialName("session_id") val sessionId: String,
    @SerialName("messages") val messages: List<ChatResponseMessageDto>
)

@Serializable
data class ProfileExtractRequest(
    @SerialName("text") val text: String,
    @SerialName("current_profile") val currentProfile: ProfilingSnapshotDto? = null
)

@Serializable
data class ProfileExtractResponse(
    @SerialName("snapshot") val snapshot: ProfilingSnapshotDto
)
