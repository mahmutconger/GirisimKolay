package com.anlarsinsoftware.girisimkolay.dashboard.domain.repository

import com.anlarsinsoftware.girisimkolay.dashboard.domain.entity.NewsArticle
import com.anlarsinsoftware.girisimkolay.dashboard.domain.entity.UserStatus
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getUserStatus(): Flow<UserStatus>
    fun getNewsFeed(): Flow<List<NewsArticle>>
}
