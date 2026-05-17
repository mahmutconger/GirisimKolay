package com.anlarsinsoftware.girisimkolay.roadmap.domain.repository

import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.ApprovalStatus
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapReport
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapStep
import kotlinx.coroutines.flow.Flow

interface RoadmapRepository : DocumentRepository {
    fun getRoadmapSteps(): Flow<List<RoadmapStep>>
    suspend fun generateRoadmapReport(): Result<RoadmapReport>
    suspend fun sendToExpert(): Result<ApprovalStatus>
}
