package com.anlarsinsoftware.girisimkolay.analytics.domain.repository

import com.anlarsinsoftware.girisimkolay.analytics.domain.entity.AnalyticsSnapshot
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getAnalyticsData(): Flow<AnalyticsSnapshot>
}
