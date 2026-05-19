package com.anlarsinsoftware.girisimkolay.roadmap.data

import com.anlarsinsoftware.girisimkolay.auth.domain.repository.AuthRepository
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import com.anlarsinsoftware.girisimkolay.core.data.MemoryCache
import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.Logger
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.roadmap.data.source.RoadmapLocalStore
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.ApprovalStatus
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapReport
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapStep
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class FirebaseRoadmapRepository(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val functionsDataSource: FirebaseFunctionsReportDataSource,
    private val reportDataSource: FirestoreReportDataSource,
    private val roadmapLocalStore: RoadmapLocalStore,
    clock: Clock,
    private val logger: Logger
) : RoadmapRepository {
    private val latestReportState = MutableStateFlow<RoadmapReport?>(null)
    private val reportCache = MemoryCache<String, RoadmapReport>(clock = clock, ttlMillis = REPORT_CACHE_TTL_MS)

    override fun getRoadmapSteps(): Flow<List<RoadmapStep>> = flowOf(DEFAULT_STEPS)

    override fun getLatestReport(): Flow<RoadmapReport?> = latestReportState.asStateFlow()

    override suspend fun refreshLatestReport(forceRefresh: Boolean): Result<RoadmapReport?> {
        val uid = authRepository.currentUserId() ?: return Result.Success(null)
        val reportId = roadmapLocalStore.getLatestReportId() ?: return Result.Success(null)
        if (!forceRefresh) {
            reportCache.get(reportId)?.let {
                latestReportState.value = it
                return Result.Success(it)
            }
        }
        return try {
            val report = reportDataSource.loadReport(uid = uid, reportId = reportId)?.toDomain()
            report?.let {
                reportCache.put(it.id, it)
                latestReportState.value = it
            }
            Result.Success(report)
        } catch (exception: Exception) {
            logger.error("FirebaseRoadmapRepository", "Report refresh failed", exception)
            Result.Error(
                message = "Rapor bilgileri alınamadı.",
                throwable = exception,
                code = "report_refresh_failed",
                isRetryable = true
            )
        }
    }

    override suspend fun generateRoadmapReport(): Result<RoadmapReport> {
        authRepository.currentUserId()
            ?: return Result.Error(message = "Rapor üretmek için giriş yapmalısınız.", code = "unauthenticated")
        val sessionId = chatRepository.currentActiveSessionId()
            ?: return Result.Error(message = "Önce bir AI sohbet oturumu başlatın.", code = "missing_session")
        return try {
            val report = functionsDataSource.generateReport(sessionId).toDomain()
            latestReportState.value = report
            reportCache.put(report.id, report)
            roadmapLocalStore.saveLatestReportId(report.id)
            Result.Success(report)
        } catch (exception: Exception) {
            logger.error("FirebaseRoadmapRepository", "Generate report failed", exception)
            Result.Error(
                message = "Girişim raporu üretilemedi.",
                throwable = exception,
                code = "report_generate_failed",
                isRetryable = true
            )
        }
    }

    override suspend fun sendToExpert(): Result<ApprovalStatus> {
        authRepository.currentUserId()
            ?: return Result.Error(message = "Onay göndermek için giriş yapmalısınız.", code = "unauthenticated")
        val currentReport = latestReportState.value
            ?: return Result.Error(message = "Önce rapor üretmelisiniz.", code = "missing_report")

        val updated = currentReport.copy(approvalStatus = ApprovalStatus.SENT)
        latestReportState.value = updated
        reportCache.put(updated.id, updated)
        roadmapLocalStore.saveLatestReportId(updated.id)
        return Result.Success(ApprovalStatus.SENT)
    }

    private fun com.anlarsinsoftware.girisimkolay.roadmap.data.dto.RoadmapReportDto.toDomain(): RoadmapReport = RoadmapReport(
        id = id,
        userId = userId,
        sessionId = sessionId,
        title = title,
        summary = summary,
        fileUrl = fileUrl,
        generatedAt = generatedAt,
        approvalStatus = ApprovalStatus.valueOf(approvalStatus),
        nextActions = nextActions
    )

    private companion object {
        const val REPORT_CACHE_TTL_MS = 5 * 60 * 1000L

        val DEFAULT_STEPS = listOf(
            RoadmapStep("1", "Şirket Tipi Seçimi", "AI profil analizi ve mevzuat eşleştirmesiyle şirket türünüz netleşir.", true, false),
            RoadmapStep("2", "Vergi ve SGK Kontrolleri", "Vergi avantajları ile zorunlu yükümlülükler iş modelinize göre listelenir.", true, false),
            RoadmapStep("3", "Rapor ve Belge Merkezi", "Canlı chat oturumundan profesyonel bir hazırlık raporu üretilir.", false, true),
            RoadmapStep("4", "Uzman Onay Simülasyonu", "Raporunuzu mali müşavir onayına gönderip kapanış akışını tamamlayın.", false, false)
        )
    }
}
