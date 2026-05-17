package com.anlarsinsoftware.girisimkolay.analytics.domain.usecase

import com.anlarsinsoftware.girisimkolay.analytics.domain.repository.AnalyticsRepository

class LoadAnalytics(private val repository: AnalyticsRepository) {
    operator fun invoke() = repository.getAnalyticsData()
}
