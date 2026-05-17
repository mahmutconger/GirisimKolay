package com.anlarsinsoftware.girisimkolay.analytics.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsSnapshot(
    val income: Double,
    val expenses: Double,
    val taskCompletionRate: Int,
    val aiInsight: String
)

typealias AnalyticsData = AnalyticsSnapshot
