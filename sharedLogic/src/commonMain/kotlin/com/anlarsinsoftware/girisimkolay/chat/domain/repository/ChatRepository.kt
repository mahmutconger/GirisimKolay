package com.anlarsinsoftware.girisimkolay.chat.domain.repository

import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMessage
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatHistory(): Flow<List<ChatMessage>>
    fun getActiveSessionId(): Flow<String?>
    fun currentActiveSessionId(): String?
    suspend fun refreshChatHistory(): Result<List<ChatMessage>>
    suspend fun sendMessage(text: String): Result<ChatMessage>
    fun getTypingStatus(): Flow<Boolean>
}
