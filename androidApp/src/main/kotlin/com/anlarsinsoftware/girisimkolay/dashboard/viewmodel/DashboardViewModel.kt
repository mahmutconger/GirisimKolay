package com.anlarsinsoftware.girisimkolay.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anlarsinsoftware.girisimkolay.dashboard.domain.entity.NewsArticle
import com.anlarsinsoftware.girisimkolay.dashboard.domain.entity.UserStatus
import com.anlarsinsoftware.girisimkolay.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    dashboardRepository: DashboardRepository
) : ViewModel() {

    val userStatus: StateFlow<UserStatus?> = dashboardRepository.getUserStatus()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val newsFeed: StateFlow<List<NewsArticle>> = dashboardRepository.getNewsFeed()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
