package com.anlarsinsoftware.girisimkolay.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anlarsinsoftware.girisimkolay.calendar.domain.entity.CalendarEvent
import com.anlarsinsoftware.girisimkolay.calendar.domain.repository.CalendarRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CalendarViewModel(
    calendarRepository: CalendarRepository
) : ViewModel() {

    // For simplicity, we just fetch events for the current month in mock
    val events: StateFlow<List<CalendarEvent>> = calendarRepository.getEventsForMonth(2026, 5)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
