package com.anlarsinsoftware.girisimkolay.roadmap.data.repository

import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultClock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultIdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.IdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.ApprovalStatus
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapReport
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapStep
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class MockRoadmapRepository(
    private val clock: Clock = DefaultClock,
    private val idProvider: IdProvider = DefaultIdProvider
) : RoadmapRepository {
    private val latestReport = MutableStateFlow<RoadmapReport?>(null)

    override fun getRoadmapSteps(): Flow<List<RoadmapStep>> = flowOf(
        listOf(
            RoadmapStep(
                id = "1",
                title = "Şirket Tipi Seçimi",
                description = "Yapay zeka analizine göre Şahıs Şirketi önerildi.",
                isCompleted = true,
                isActive = false
            ),
            RoadmapStep(
                id = "2",
                title = "Gerekli Evrakların Toplanması",
                description = "Kimlik fotokopisi, ikametgah, imza sirküleri.",
                isCompleted = true,
                isActive = false
            ),
            RoadmapStep(
                id = "3",
                title = "Mali Müşavir Ataması",
                description = "Sistem üzerinden uygun mali müşavir eşleştirmesi bekleniyor.",
                isCompleted = false,
                isActive = true
            ),
            RoadmapStep(
                id = "4",
                title = "KOSGEB İş Planı Gönderimi",
                description = "Yapay zeka tarafından hazırlanan iş planı KOSGEB'e iletilecek.",
                isCompleted = false,
                isActive = false
            )
        )
    )

    override fun getLatestReport(): Flow<RoadmapReport?> = latestReport.asStateFlow()

    override suspend fun refreshLatestReport(forceRefresh: Boolean): Result<RoadmapReport?> =
        Result.Success(latestReport.value)

    override suspend fun generateRoadmapReport(): Result<RoadmapReport> {
        delay(2000)
        val report = RoadmapReport(
            id = idProvider.randomId(),
            userId = "mock-user",
            sessionId = "mock-session",
            title = "Girişim Hazırlık Raporu",
            summary = "Şahıs şirketi kuruluşu ve KOSGEB başvuru adımları derlendi.",
            fileUrl = "https://dummy-pdf-url.com/girisim_raporu.pdf",
            generatedAt = clock.nowMillis(),
            nextActions = listOf("Mali müşavir ile görüşün", "Vergi dairesi kaydını tamamlayın")
        )
        latestReport.value = report
        return Result.Success(report)
    }

    override suspend fun sendToExpert(): Result<ApprovalStatus> {
        delay(1500)
        latestReport.value = latestReport.value?.copy(approvalStatus = ApprovalStatus.SENT)
        return Result.Success(ApprovalStatus.SENT)
    }
}
