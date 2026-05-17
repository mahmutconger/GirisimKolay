package com.anlarsinsoftware.girisimkolay.calendar.data.repository

import com.anlarsinsoftware.girisimkolay.calendar.domain.entity.CalendarEvent
import com.anlarsinsoftware.girisimkolay.calendar.domain.entity.EventType
import com.anlarsinsoftware.girisimkolay.calendar.domain.repository.CalendarRepository
import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockCalendarRepository(
    private val clock: Clock = DefaultClock
) : CalendarRepository {
    
    private val mockEvents = listOf(
        CalendarEvent(
            id = "1",
            title = "Geçici Vergi Beyannamesi",
            dateMillis = clock.nowMillis() + 86400000 * 2,
            type = EventType.TAX_DEADLINE,
            isCritical = true
        ),
        CalendarEvent(
            id = "2",
            title = "KOSGEB Kadın Girişimci Desteği Kapanış",
            dateMillis = clock.nowMillis() + 86400000 * 5,
            type = EventType.GRANT_WINDOW,
            isCritical = true
        ),
        CalendarEvent(
            id = "3",
            title = "Mali Müşavirle Evrak Paylaşımı",
            dateMillis = clock.nowMillis(),
            type = EventType.DAILY_TASK,
            isCritical = false
        )
    )

    override fun getEventsForMonth(year: Int, month: Int): Flow<List<CalendarEvent>> {
        return flowOf(mockEvents)
    }

    override fun getEventsForDay(year: Int, month: Int, day: Int): Flow<List<CalendarEvent>> {
        // Simplified for mock
        return flowOf(mockEvents)
    }
}
