package com.anlarsinsoftware.girisimkolay.chat.data.repository

import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMessage
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMode
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatSessionSummary
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
import kotlinx.coroutines.flow.flowOf
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

    override suspend fun sendMessage(text: String, mode: ChatMode): Result<ChatMessage> {
        val userMessage = ChatMessage(
            id = idProvider.randomId(),
            sessionId = sessionId.value,
            text = text,
            isFromUser = true,
            timestamp = clock.nowMillis(),
            mode = mode
        )
        _messages.update { it + userMessage }

        val aiMessage = simulateAiResponse(text, mode)
        return Result.Success(aiMessage)
    }

    override fun getTypingStatus(): Flow<Boolean> = _isTyping.asStateFlow()

    override fun listRecentSessions(): Flow<List<ChatSessionSummary>> = flowOf(emptyList())

    override suspend fun switchSession(sessionId: String) {}

    override fun startNewSession() {
        sessionId.value = idProvider.randomId()
        _messages.value = emptyList()
    }

    override fun getCurrentUserDisplayName(): String? = "Test Kullanıcısı"

    private suspend fun simulateAiResponse(userText: String, mode: ChatMode): ChatMessage {
        _isTyping.value = true
        delay(1500)

        val (textReply, nextActions, citations) = when {
            userText.contains("el sanatları", ignoreCase = true) || userText.contains("kosgeb", ignoreCase = true) -> {
                Triple(
                    "[KOSGEB_TEMPLATE]Harika bir fikir! Evden yürüteceğiniz el sanatları işiniz için KOSGEB'in özellikle kadın girişimciler ve yeni kurulan işletmeler için uygun destek programları bulunmaktadır.",
                    listOf("Rapor Oluştur", "Uzmana Sor"),
                    listOf(Citation(sourceName = "KOSGEB 2026 Destek Rehberi"))
                )
            }
            userText.contains("blockchain", ignoreCase = true) || userText.contains("ölçeklenebilirlik", ignoreCase = true) -> {
                Triple(
                    "[BLOCKCHAIN_TEMPLATE]Ölçeklenebilirlik (Scalability), blockchain ağlarının geniş çapta benimsenmesinin önündeki en temel engellerden biridir. Ağ üzerindeki işlem hacmi arttıkça, hız düşer ve maliyetler yükselir. Bu sorunu aşmak için geliştirilen stratejiler temel olarak üç ana kategoriye ayrılır:",
                    listOf("Rapor Oluştur", "Detaylı İncele"),
                    emptyList()
                )
            }
            userText.contains("sgk", ignoreCase = true) || userText.contains("teşvik", ignoreCase = true) -> {
                Triple(
                    "[SGK_TEMPLATE]Merhaba! İşletmeniz için faydalanabileceğiniz başlıca SGK teşviklerini sizin için derledim:",
                    listOf("Rapor Oluştur", "Uzmana Sor"),
                    emptyList()
                )
            }
            userText.contains("vergi", ignoreCase = true) || userText.contains("muafiyet", ignoreCase = true) -> {
                Triple(
                    "[VERGI_TEMPLATE]Türkiye'de girişimciler için sağlanan başlıca vergi muafiyetleri ve istisnaları şunlardır:",
                    listOf("Rapor Oluştur", "Uzmana Sor"),
                    emptyList()
                )
            }
            else -> {
                Triple(
                    mockResponseFor(mode),
                    listOf("Şirket tipi seçimini doğrulayın", "Vergi yükümlülüklerini kontrol edin"),
                    listOf(
                        Citation(
                            sourceName = "GİB 2026 E-Ticaret Tebliği",
                            section = "Madde 4",
                            snippet = "Şahıs şirketi ile başlanması düşük maliyetlidir."
                        )
                    )
                )
            }
        }

        val aiMessage = ChatMessage(
            id = idProvider.randomId(),
            sessionId = sessionId.value,
            text = textReply,
            isFromUser = false,
            timestamp = clock.nowMillis(),
            citations = citations,
            nextActions = nextActions,
            mode = mode
        )
        
        _isTyping.value = false
        _messages.update { it + aiMessage }
        return aiMessage
    }

    private fun mockResponseFor(mode: ChatMode): String =
        when (mode) {
            ChatMode.NORMAL ->
                "Kısa cevap: Başlangıç aşamasında düşük maliyet ve hızlı kurulum istiyorsanız şahıs şirketi çoğu girişimci için daha pratik olabilir. Yine de gelir beklentiniz, ortaklık yapınız ve risk seviyeniz netleşmeden kesin karar vermeyin."
            ChatMode.ROADMAP ->
                "Kısa Değerlendirme\nŞirket kurulumunda önce faaliyet alanınızı ve şirket tipinizi netleştirmeniz gerekir.\n\nAdım Adım Yol Haritası\n1. Faaliyet kodunuzu ve satış kanalınızı belirleyin.\n2. Şirket tipini seçin.\n3. Vergi dairesi ve e-belge süreçlerini tamamlayın.\n4. Banka, ödeme altyapısı ve fatura süreçlerini hazırlayın.\n\nSonraki En Mantıklı Adım\nBir SMMM ile şirket tipi seçimini doğrulayın."
            ChatMode.DEEP_RESEARCH ->
                "Bu konuda güvenli ilerlemek için önce ilgili resmi kaynakları kontrol etmek gerekir. Elimdeki örnek kaynak, şahıs şirketinin düşük başlangıç maliyeti açısından avantajlı olabileceğini gösteriyor; ancak kesin karar için güncel mevzuat ve uzman teyidi gerekir."
        }
}
