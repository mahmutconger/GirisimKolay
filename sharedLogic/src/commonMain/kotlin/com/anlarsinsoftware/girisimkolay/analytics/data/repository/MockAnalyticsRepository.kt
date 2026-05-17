package com.anlarsinsoftware.girisimkolay.analytics.data.repository

import com.anlarsinsoftware.girisimkolay.analytics.domain.entity.AnalyticsSnapshot
import com.anlarsinsoftware.girisimkolay.analytics.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockAnalyticsRepository : AnalyticsRepository {
    override fun getAnalyticsData(): Flow<AnalyticsSnapshot> {
        return flowOf(
            AnalyticsSnapshot(
                income = 125000.0,
                expenses = 45000.0,
                taskCompletionRate = 85,
                aiInsight = "Bu ay operasyonel kaynaklarınızı %15 daha verimli kullandınız. Kargo giderleriniz optimize edildi."
            )
        )
    }
}
