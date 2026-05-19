package com.anlarsinsoftware.girisimkolay.chat.domain.repository

import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMessage
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMode
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatSessionSummary
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatHistory(): Flow<List<ChatMessage>>
    fun getActiveSessionId(): Flow<String?>
    fun currentActiveSessionId(): String?
    suspend fun refreshChatHistory(): Result<List<ChatMessage>>
    suspend fun sendMessage(text: String, mode: ChatMode = ChatMode.NORMAL): Result<ChatMessage>
    fun getTypingStatus(): Flow<Boolean>
    fun listRecentSessions(): Flow<List<ChatSessionSummary>>
    suspend fun switchSession(sessionId: String)
    fun startNewSession()
    fun getCurrentUserDisplayName(): String?
}
