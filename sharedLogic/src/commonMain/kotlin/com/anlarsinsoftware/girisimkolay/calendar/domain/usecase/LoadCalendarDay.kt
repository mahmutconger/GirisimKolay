package com.anlarsinsoftware.girisimkolay.calendar.domain.usecase

import com.anlarsinsoftware.girisimkolay.calendar.domain.repository.CalendarRepository

class LoadCalendarDay(private val repository: CalendarRepository) {
    operator fun invoke(year: Int, month: Int, day: Int) = repository.getEventsForDay(year, month, day)
}
