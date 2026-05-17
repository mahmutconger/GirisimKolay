package com.anlarsinsoftware.girisimkolay.dashboard.domain.usecase

import com.anlarsinsoftware.girisimkolay.dashboard.domain.repository.NewsRepository

class LoadDashboard(private val repository: NewsRepository) {
    operator fun invoke() = repository.getNewsFeed()
}
