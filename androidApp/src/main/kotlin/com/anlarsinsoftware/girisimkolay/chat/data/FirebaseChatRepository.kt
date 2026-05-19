package com.anlarsinsoftware.girisimkolay.chat.data

import android.util.Log
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMessage
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMode
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatSessionSummary
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultClock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultIdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.IdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.Logger
import com.anlarsinsoftware.girisimkolay.core.domain.NoopLogger
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.core.domain.SessionStateStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseChatRepository(
    private val auth: FirebaseAuth,
    private val functionsDataSource: FirebaseFunctionsChatDataSource,
    private val historyDataSource: FirestoreChatHistoryDataSource,
    private val sessionsDataSource: FirestoreChatSessionsDataSource,
    private val sessionStateStore: SessionStateStore,
    private val clock: Clock = DefaultClock,
    private val idProvider: IdProvider = DefaultIdProvider,
    private val logger: Logger = NoopLogger
) : ChatRepository {
    init {
        Log.i("FirebaseChatRepository", "Live Firebase chat repository initialized.")
    }

    private val chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val isTyping = MutableStateFlow(false)
    private val activeSessionId = MutableStateFlow(sessionStateStore.getActiveSessionId())
    private val _recentSessions = MutableStateFlow<List<ChatSessionSummary>>(emptyList())

    override fun getChatHistory(): Flow<List<ChatMessage>> = chatHistory.asStateFlow()

    override fun getActiveSessionId(): Flow<String?> = activeSessionId.asStateFlow()

    override fun currentActiveSessionId(): String? = activeSessionId.value

    override fun getTypingStatus(): Flow<Boolean> = isTyping.asStateFlow()

    override fun listRecentSessions(): Flow<List<ChatSessionSummary>> = _recentSessions.asStateFlow()

    override fun getCurrentUserDisplayName(): String? = auth.currentUser?.displayName

    override suspend fun switchSession(sessionId: String) {
        activeSessionId.value = sessionId
        sessionStateStore.saveActiveSessionId(sessionId)
        chatHistory.value = emptyList()
        refreshChatHistory()
    }

    override fun startNewSession() {
        activeSessionId.value = null
        sessionStateStore.clear()
        chatHistory.value = emptyList()
    }

    override suspend fun refreshChatHistory(): Result<List<ChatMessage>> {
        val uid = auth.currentUser?.uid ?: return Result.Success(chatHistory.value)
        val sessionId = activeSessionId.value ?: return Result.Success(chatHistory.value)
        return try {
            val messages = historyDataSource.loadMessages(uid = uid, sessionId = sessionId)
            chatHistory.value = messages
            try {
                _recentSessions.value = sessionsDataSource.loadRecentSessions(uid)
            } catch (e: Exception) {
                logger.error("FirebaseChatRepository", "Session list refresh failed", e)
            }
            Result.Success(messages)
        } catch (exception: Exception) {
            logger.error("FirebaseChatRepository", "Chat history refresh failed", exception)
            Result.Error(
                message = "Sohbet geçmişi alınamadı.",
                throwable = exception,
                code = "chat_history_failed",
                isRetryable = true
            )
        }
    }

    override suspend fun sendMessage(text: String, mode: ChatMode): Result<ChatMessage> {
        Log.i("FirebaseChatRepository", "sendMessage started. mode=${mode.wireValue}")
        val uid = auth.currentUser?.uid
            ?: return Result.Error(message = "Mesaj göndermek için giriş yapmalısınız.", code = "unauthenticated")
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return Result.Error(message = "Boş mesaj gönderilemez.", code = "invalid_message")
        }

        val optimistic = ChatMessage(
            id = "local-${idProvider.randomId()}",
            sessionId = activeSessionId.value ?: "pending",
            text = trimmed,
            isFromUser = true,
            timestamp = clock.nowMillis(),
            mode = mode
        )
        chatHistory.value = chatHistory.value + optimistic
        isTyping.value = true

        return try {
            val response = functionsDataSource.sendMessage(
                sessionId = activeSessionId.value,
                text = trimmed,
                clientRequestId = idProvider.randomId(),
                mode = mode
            )
            Log.i("FirebaseChatRepository", "Functions response received for session ${response.sessionId}.")
            activeSessionId.value = response.sessionId
            sessionStateStore.saveActiveSessionId(response.sessionId)
            val refreshed = historyDataSource.loadMessages(uid = uid, sessionId = response.sessionId)
            chatHistory.value = refreshed
            isTyping.value = false
            Result.Success(refreshed.lastOrNull { !it.isFromUser } ?: response.message.toDomain())
        } catch (exception: Exception) {
            logger.error("FirebaseChatRepository", "Send message failed", exception)
            Log.e("FirebaseChatRepository", "sendMessage failed.", exception)
            isTyping.value = false
            chatHistory.value = chatHistory.value + ChatMessage(
                id = "error-${idProvider.randomId()}",
                sessionId = activeSessionId.value ?: "error",
                text = "Şu anda danışmana ulaşılamıyor. Lütfen tekrar deneyin.",
                isFromUser = false,
                timestamp = clock.nowMillis(),
                mode = mode
            )
            Result.Error(
                message = "AI danışmanına ulaşılamadı.",
                throwable = exception,
                code = "chat_send_failed",
                isRetryable = true
            )
        }
    }
}

private fun com.anlarsinsoftware.girisimkolay.chat.data.dto.ChatResponseMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = id,
    sessionId = sessionId,
    text = text,
    isFromUser = isFromUser,
    timestamp = timestamp,
    citations = citations.map {
        com.anlarsinsoftware.girisimkolay.chat.domain.entity.Citation(
            sourceName = it.sourceName,
            section = it.section,
            snippet = it.snippet,
            sourceUrl = it.sourceUrl
        )
    },
    profileDelta = profileDelta?.let {
        com.anlarsinsoftware.girisimkolay.profile.domain.entity.ProfilingSnapshot(
            businessIdea = it.businessIdea,
            businessSector = it.businessSector,
            preferredCompanyType = it.preferredCompanyType,
            experienceLevel = it.experienceLevel,
            fundingNeed = it.fundingNeed,
            legalConcerns = it.legalConcerns
        )
    },
    confidence = confidence,
    nextActions = nextActions,
    mode = ChatMode.fromWireValue(mode)
)
