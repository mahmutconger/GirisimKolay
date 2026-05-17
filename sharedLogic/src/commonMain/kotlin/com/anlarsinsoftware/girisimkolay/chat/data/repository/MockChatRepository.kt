package com.anlarsinsoftware.girisimkolay.chat.data.repository

import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMessage
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.Citation
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultClock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultIdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.IdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MockChatRepository(
    private val clock: Clock = DefaultClock,
    private val idProvider: IdProvider = DefaultIdProvider
) : ChatRepository {
    private val sessionId = MutableStateFlow(idProvider.randomId())

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = idProvider.randomId(),
                sessionId = sessionId.value,
                text = "Merhaba! Ben GirişimKolay AI Danışmanınız. Ne yapmak istersiniz?",
                isFromUser = false,
                timestamp = clock.nowMillis()
            )
        )
    )

    private val _isTyping = MutableStateFlow(false)

    override fun getChatHistory(): Flow<List<ChatMessage>> = _messages.asStateFlow()

    override fun getActiveSessionId(): Flow<String?> = sessionId.asStateFlow()

    override fun currentActiveSessionId(): String? = sessionId.value

    override suspend fun refreshChatHistory(): Result<List<ChatMessage>> =
        Result.Success(_messages.value)

    override suspend fun sendMessage(text: String): Result<ChatMessage> {
        val userMessage = ChatMessage(
            id = idProvider.randomId(),
            sessionId = sessionId.value,
            text = text,
            isFromUser = true,
            timestamp = clock.nowMillis()
        )
        _messages.update { it + userMessage }

        val aiMessage = simulateAiResponse(text)
        return Result.Success(aiMessage)
    }

    override fun getTypingStatus(): Flow<Boolean> = _isTyping.asStateFlow()

    private suspend fun simulateAiResponse(userText: String): ChatMessage {
        _isTyping.value = true
        delay(1500)
        
        val aiMessage = ChatMessage(
            id = idProvider.randomId(),
            sessionId = sessionId.value,
            text = "Anladım. İhtiyacınıza yönelik yol haritasını oluşturuyorum. Bu konuda mevzuat gereği şahıs şirketi kurmanız avantajlı olabilir.",
            isFromUser = false,
            timestamp = clock.nowMillis(),
            citations = listOf(
                Citation(
                    sourceName = "GİB 2026 E-Ticaret Tebliği",
                    section = "Madde 4",
                    snippet = "Şahıs şirketi ile başlanması düşük maliyetlidir."
                )
            ),
            nextActions = listOf("Şirket tipi seçimini doğrulayın", "Vergi yükümlülüklerini kontrol edin")
        )
        
        _isTyping.value = false
        _messages.update { it + aiMessage }
        return aiMessage
    }
}
