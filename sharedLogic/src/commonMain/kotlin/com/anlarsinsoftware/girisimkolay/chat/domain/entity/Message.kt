package com.anlarsinsoftware.girisimkolay.chat.domain.entity

import com.anlarsinsoftware.girisimkolay.profile.domain.entity.ProfilingSnapshot
import kotlinx.serialization.Serializable

@Serializable
enum class ChatMode(val wireValue: String, val displayName: String) {
    NORMAL("NORMAL", "Normal"),
    ROADMAP("ROADMAP", "Yol Haritası"),
    DEEP_RESEARCH("DEEP_RESEARCH", "Derin Tarama");

    companion object {
        fun fromWireValue(value: String?): ChatMode =
            entries.firstOrNull { it.wireValue == value } ?: NORMAL
    }
}

@Serializable
data class Citation(
    val sourceName: String,
    val section: String? = null,
    val snippet: String? = null,
    val sourceUrl: String? = null
)

@Serializable
data class ChatMessage(
    val id: String,
    val sessionId: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val citations: List<Citation> = emptyList(),
    val profileDelta: ProfilingSnapshot? = null,
    val confidence: Double? = null,
    val nextActions: List<String> = emptyList(),
    val mode: ChatMode = ChatMode.NORMAL
) {
    val sources: List<String>
        get() = citations.map { it.sourceName }
}

typealias Message = ChatMessage
