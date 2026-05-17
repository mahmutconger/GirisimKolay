package com.anlarsinsoftware.girisimkolay.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.Message
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val messages: StateFlow<List<Message>> = chatRepository.getChatHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isTyping: StateFlow<Boolean> = chatRepository.getTypingStatus()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        viewModelScope.launch {
            chatRepository.refreshChatHistory()
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            chatRepository.sendMessage(text.trim())
        }
    }
}
