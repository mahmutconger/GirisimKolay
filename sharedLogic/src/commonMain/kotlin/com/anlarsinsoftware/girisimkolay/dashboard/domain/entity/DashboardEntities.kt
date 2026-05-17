package com.anlarsinsoftware.girisimkolay.dashboard.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserStatus(
    val name: String,
    val companyType: String,
    val entrepreneurType: String
)

@Serializable
data class NewsArticle(
    val id: String,
    val title: String,
    val source: String,
    val summary: String,
    val sentimentScore: Int,
    val sentimentText: String,
    val sourceTag: String = source,
    val sentimentColorHex: String = if (sentimentScore > 0) "#4CAF50" else "#FFC107"
)
