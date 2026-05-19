package com.anlarsinsoftware.girisimkolay.roadmap.data.repository

import com.anlarsinsoftware.girisimkolay.auth.domain.repository.AuthRepository
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import com.anlarsinsoftware.girisimkolay.core.data.MemoryCache
import com.anlarsinsoftware.girisimkolay.core.domain.BearerTokenProvider
import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.Logger
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.roadmap.data.dto.GenerateReportRequest
import com.anlarsinsoftware.girisimkolay.roadmap.data.dto.RoadmapReportDto
import com.anlarsinsoftware.girisimkolay.roadmap.data.source.RoadmapLocalStore
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.ApprovalStatus
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapReport
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapStep
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.RoadmapRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

class LiveRoadmapRepository(
    private val authRepository: AuthRepository,
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val chatRepository: ChatRepository,
    private val authTokenProvider: BearerTokenProvider,
    private val roadmapLocalStore: RoadmapLocalStore,
    clock: Clock,
    private val logger: Logger
) : RoadmapRepository {
    private val latestReportState = MutableStateFlow<RoadmapReport?>(null)
    private val reportCache = MemoryCache<String, RoadmapReport>(clock = clock, ttlMillis = REPORT_CACHE_TTL_MS)

    override fun getRoadmapSteps(): Flow<List<RoadmapStep>> = flowOf(DEFAULT_STEPS)

    override fun getLatestReport(): Flow<RoadmapReport?> = latestReportState.asStateFlow()

    override suspend fun refreshLatestReport(forceRefresh: Boolean): Result<RoadmapReport?> {
        val reportId = roadmapLocalStore.getLatestReportId() ?: return Result.Success(null)
        if (!forceRefresh) {
            reportCache.get(reportId)?.let {
                latestReportState.value = it
                return Result.Success(it)
            }
        }

        return try {
            val token = authTokenProvider.getFreshToken()
                ?: return Result.Error(message = "Kimlik doğrulama gerekli.", code = "missing_token")
            val report: RoadmapReportDto = httpClient.get("$baseUrl/api/v1/reports/$reportId") {
                headers.append(HttpHeaders.Authorization, "Bearer $token")
            }.body()
            val mapped = report.toDomain()
            reportCache.put(mapped.id, mapped)
            latestReportState.value = mapped
            Result.Success(mapped)
        } catch (exception: Exception) {
            logger.error("LiveRoadmapRepository", "Report refresh failed", exception)
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
            val token = authTokenProvider.getFreshToken()
                ?: return Result.Error(message = "Kimlik doğrulama gerekli.", code = "missing_token")
            val reportDto: RoadmapReportDto = httpClient.post("$baseUrl/api/v1/reports") {
                contentType(ContentType.Application.Json)
                headers.append(HttpHeaders.Authorization, "Bearer $token")
                setBody(GenerateReportRequest(sessionId = sessionId))
            }.body()
            val report = reportDto.toDomain()
            persistReport(report)
            Result.Success(report)
        } catch (exception: Exception) {
            logger.error("LiveRoadmapRepository", "Generate report failed", exception)
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

    private fun persistReport(report: RoadmapReport) {
        reportCache.put(report.id, report)
        roadmapLocalStore.saveLatestReportId(report.id)
        latestReportState.value = report
    }

    private fun RoadmapReportDto.toDomain(): RoadmapReport = RoadmapReport(
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
            RoadmapStep(
                id = "1",
                title = "Şirket Tipi Seçimi",
                description = "AI profil analizi ve mevzuat eşleştirmesiyle şirket türünüz netleşir.",
                isCompleted = true,
                isActive = false
            ),
            RoadmapStep(
                id = "2",
                title = "Vergi ve SGK Kontrolleri",
                description = "Vergi avantajları ile zorunlu yükümlülükler iş modelinize göre listelenir.",
                isCompleted = true,
                isActive = false
            ),
            RoadmapStep(
                id = "3",
                title = "Rapor ve Belge Merkezi",
                description = "Canlı chat oturumundan profesyonel bir hazırlık raporu üretilir.",
                isCompleted = false,
                isActive = true
            ),
            RoadmapStep(
                id = "4",
                title = "Uzman Onay Simülasyonu",
                description = "Raporunuzu mali müşavir onayına gönderip kapanış akışını tamamlayın.",
                isCompleted = false,
                isActive = false
            )
        )
    }
}
