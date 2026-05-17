package com.anlarsinsoftware.girisimkolay.calendar.domain.repository

import com.anlarsinsoftware.girisimkolay.calendar.domain.entity.CalendarEvent
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getEventsForMonth(year: Int, month: Int): Flow<List<CalendarEvent>>
    fun getEventsForDay(year: Int, month: Int, day: Int): Flow<List<CalendarEvent>>
}
