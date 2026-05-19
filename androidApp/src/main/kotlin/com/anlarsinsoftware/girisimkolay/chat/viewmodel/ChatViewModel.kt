package com.anlarsinsoftware.girisimkolay.chat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.Message
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMode
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    init {
        Log.i("ChatViewModel", "Chat repository implementation: ${chatRepository::class.qualifiedName}")
    }

    val messages: StateFlow<List<Message>> = chatRepository.getChatHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isTyping: StateFlow<Boolean> = chatRepository.getTypingStatus()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _selectedMode = MutableStateFlow(ChatMode.NORMAL)
    val selectedMode: StateFlow<ChatMode> = _selectedMode.asStateFlow()

    init {
        viewModelScope.launch {
            Log.i("ChatViewModel", "Refreshing chat history.")
            chatRepository.refreshChatHistory()
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        Log.i("ChatViewModel", "sendMessage called with ${text.length} chars.")
        viewModelScope.launch {
            chatRepository.sendMessage(text.trim(), _selectedMode.value)
        }
    }

    fun selectMode(mode: ChatMode) {
        _selectedMode.value = mode
    }
}
