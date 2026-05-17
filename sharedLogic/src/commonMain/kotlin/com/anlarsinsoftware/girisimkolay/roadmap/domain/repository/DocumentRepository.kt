package com.anlarsinsoftware.girisimkolay.roadmap.domain.repository

import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapReport
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getLatestReport(): Flow<RoadmapReport?>
    suspend fun refreshLatestReport(forceRefresh: Boolean = false): Result<RoadmapReport?>
}
