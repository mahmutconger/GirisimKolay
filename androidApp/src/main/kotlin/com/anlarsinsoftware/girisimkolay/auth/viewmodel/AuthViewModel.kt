package com.anlarsinsoftware.girisimkolay.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anlarsinsoftware.girisimkolay.auth.domain.entity.AuthUser
import com.anlarsinsoftware.girisimkolay.auth.domain.repository.AuthRepository
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Sealed UI state used by both Login and Register screens */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: AuthUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // Observe Firebase auth state changes — drives root navigation
    val currentUser: StateFlow<AuthUser?> = authRepository.observeAuthState()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("E-posta ve şifre alanları boş bırakılamaz.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signIn(email, password)
            _uiState.value = if (result is Result.Success) {
                AuthUiState.Success(result.data)
            } else {
                AuthUiState.Error(mapFirebaseError((result as Result.Error).message))
            }
        }
    }

    fun signUp(email: String, password: String, fullName: String) {
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            _uiState.value = AuthUiState.Error("Tüm alanların doldurulması zorunludur.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Şifre en az 6 karakter olmalıdır.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signUp(email, password, fullName)
            _uiState.value = if (result is Result.Success) {
                AuthUiState.Success(result.data)
            } else {
                AuthUiState.Error(mapFirebaseError((result as Result.Error).message))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    /** Converts Firebase error codes to user-friendly Turkish messages */
    private fun mapFirebaseError(message: String): String = when {
        message.contains("email address is already in use") ->
            "Bu e-posta adresi zaten kayıtlı."
        message.contains("invalid email") ->
            "Geçersiz e-posta adresi."
        message.contains("wrong password") || message.contains("invalid credential") ->
            "E-posta veya şifre hatalı."
        message.contains("user not found") ->
            "Bu e-posta adresiyle kayıtlı hesap bulunamadı."
        message.contains("network") ->
            "İnternet bağlantınızı kontrol ediniz."
        else -> message
    }
}
