package com.anlarsinsoftware.girisimkolay.analytics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anlarsinsoftware.girisimkolay.analytics.domain.entity.AnalyticsData
import com.anlarsinsoftware.girisimkolay.analytics.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AnalyticsViewModel(
    analyticsRepository: AnalyticsRepository
) : ViewModel() {

    val analyticsData: StateFlow<AnalyticsData?> = analyticsRepository.getAnalyticsData()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}
