package com.anlarsinsoftware.girisimkolay.chat.data.repository

import com.anlarsinsoftware.girisimkolay.chat.data.dto.ChatRequest
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ChatResponse
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ChatResponseMessageDto
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ChatSessionResponse
import com.anlarsinsoftware.girisimkolay.chat.data.dto.CitationDto
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ProfilingSnapshotDto
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMessage
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.Citation
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import com.anlarsinsoftware.girisimkolay.core.domain.BearerTokenProvider
import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultClock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultIdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.IdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.Logger
import com.anlarsinsoftware.girisimkolay.core.domain.NoopLogger
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.core.domain.SessionStateStore
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.ProfilingSnapshot
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkChatRepository(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val authTokenProvider: BearerTokenProvider,
    private val sessionStateStore: SessionStateStore,
    private val clock: Clock = DefaultClock,
    private val idProvider: IdProvider = DefaultIdProvider,
    private val logger: Logger = NoopLogger
) : ChatRepository {
    private val chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val isTyping = MutableStateFlow(false)
    private val activeSessionId = MutableStateFlow<String?>(sessionStateStore.getActiveSessionId())

    override fun getChatHistory(): Flow<List<ChatMessage>> = chatHistory.asStateFlow()

    override fun getActiveSessionId(): Flow<String?> = activeSessionId.asStateFlow()

    override fun currentActiveSessionId(): String? = activeSessionId.value

    override fun getTypingStatus(): Flow<Boolean> = isTyping.asStateFlow()

    override suspend fun refreshChatHistory(): Result<List<ChatMessage>> {
        val currentSessionId = activeSessionId.value ?: return Result.Success(chatHistory.value)
        return try {
            val token = authTokenProvider.getFreshToken()
                ?: return Result.Error(message = "Kimlik doğrulama gerekli.", code = "missing_token")
            val response: ChatSessionResponse = httpClient.get("$baseUrl/api/v1/chat/sessions/$currentSessionId") {
                headers.append(HttpHeaders.Authorization, "Bearer $token")
            }.body()
            val mapped = response.messages.map { it.toDomain() }
            chatHistory.value = mapped
            Result.Success(mapped)
        } catch (exception: Exception) {
            logger.error("NetworkChatRepository", "Chat history refresh failed", exception)
            Result.Error(
                message = "Sohbet geçmişi alınamadı.",
                throwable = exception,
                code = "chat_history_failed",
                isRetryable = true
            )
        }
    }

    override suspend fun sendMessage(text: String): Result<ChatMessage> {
        val userMessage = ChatMessage(
            id = "local-${chatHistory.value.size + 1}",
            sessionId = activeSessionId.value ?: "pending",
            text = text,
            isFromUser = true,
            timestamp = clock.nowMillis()
        )
        chatHistory.value = chatHistory.value + userMessage
        isTyping.value = true

        val token = authTokenProvider.getFreshToken()
        if (token == null) {
            isTyping.value = false
            return Result.Error(message = "Kimlik doğrulama gerekli.", code = "missing_token")
        }

        return try {
            val clientRequestId = idProvider.randomId()
            val response: ChatResponse = httpClient.post("$baseUrl/api/v1/chat/messages") {
                contentType(ContentType.Application.Json)
                headers.append(HttpHeaders.Authorization, "Bearer $token")
                setBody(
                    ChatRequest(
                        text = text,
                        sessionId = activeSessionId.value,
                        clientRequestId = clientRequestId
                    )
                )
            }.body()

            activeSessionId.value = response.sessionId
            sessionStateStore.saveActiveSessionId(response.sessionId)
            val aiMessage = response.message.toDomain().copy(
                citations = response.citations.map { it.toDomain() },
                profileDelta = response.profileDelta?.toDomain(),
                confidence = response.confidence,
                nextActions = response.nextActions
            )
            val withRealSession = chatHistory.value.dropLast(1) + userMessage.copy(sessionId = response.sessionId) + aiMessage
            chatHistory.value = withRealSession
            isTyping.value = false
            Result.Success(aiMessage)
        } catch (exception: Exception) {
            logger.error("NetworkChatRepository", "Send message failed", exception)
            isTyping.value = false
            val fallback = ChatMessage(
                id = "error",
                sessionId = activeSessionId.value ?: "error",
                text = "Şu anda danışmana ulaşılamıyor. İnternet bağlantınızı kontrol edip tekrar deneyin.",
                isFromUser = false,
                timestamp = clock.nowMillis()
            )
            chatHistory.value = chatHistory.value + fallback
            Result.Error(
                message = "AI danışmanına ulaşılamadı.",
                throwable = exception,
                code = "chat_send_failed",
                isRetryable = true
            )
        }
    }
}

private fun ChatResponseMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = id,
    sessionId = sessionId,
    text = text,
    isFromUser = isFromUser,
    timestamp = timestamp,
    citations = citations.map { it.toDomain() },
    profileDelta = profileDelta?.toDomain(),
    confidence = confidence,
    nextActions = nextActions
)

private fun CitationDto.toDomain(): Citation = Citation(
    sourceName = sourceName,
    section = section,
    snippet = snippet,
    sourceUrl = sourceUrl
)

private fun ProfilingSnapshotDto.toDomain(): ProfilingSnapshot = ProfilingSnapshot(
    businessIdea = businessIdea,
    businessSector = businessSector,
    preferredCompanyType = preferredCompanyType,
    experienceLevel = experienceLevel,
    fundingNeed = fundingNeed,
    legalConcerns = legalConcerns
)
