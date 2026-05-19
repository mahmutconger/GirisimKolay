package com.anlarsinsoftware.girisimkolay.chat.domain.usecase

import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMessage
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMode
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import com.anlarsinsoftware.girisimkolay.core.domain.Result

class SendChatMessage(private val repository: ChatRepository) {
    suspend operator fun invoke(text: String, mode: ChatMode = ChatMode.NORMAL): Result<ChatMessage> =
        repository.sendMessage(text, mode)
}
