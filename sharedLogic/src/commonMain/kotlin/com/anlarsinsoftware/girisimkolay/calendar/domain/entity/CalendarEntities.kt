package com.anlarsinsoftware.girisimkolay.calendar.domain.entity

import kotlinx.serialization.Serializable

enum class EventType {
    TAX_DEADLINE,    // Kırmızı
    GRANT_WINDOW,    // Yeşil
    DAILY_TASK       // Sarı
}

@Serializable
data class CalendarEvent(
    val id: String,
    val title: String,
    val dateMillis: Long,
    val type: EventType,
    val isCritical: Boolean = false,
    val colorHex: String = when (type) {
        EventType.TAX_DEADLINE -> "#F44336"
        EventType.GRANT_WINDOW -> "#4CAF50"
        EventType.DAILY_TASK -> "#FFC107"
    }
)
