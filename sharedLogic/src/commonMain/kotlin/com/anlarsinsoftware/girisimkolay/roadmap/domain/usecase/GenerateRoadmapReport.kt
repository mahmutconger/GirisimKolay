package com.anlarsinsoftware.girisimkolay.roadmap.domain.usecase

import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapReport
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.RoadmapRepository

class GenerateRoadmapReport(private val repository: RoadmapRepository) {
    suspend operator fun invoke(): Result<RoadmapReport> = repository.generateRoadmapReport()
}
