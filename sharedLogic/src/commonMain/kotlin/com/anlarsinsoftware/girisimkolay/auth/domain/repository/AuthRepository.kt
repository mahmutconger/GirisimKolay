package com.anlarsinsoftware.girisimkolay.auth.domain.repository

import com.anlarsinsoftware.girisimkolay.auth.domain.entity.AuthUser
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Emits the current user on auth state change; null = signed out */
    fun observeAuthState(): Flow<AuthUser?>

    suspend fun signIn(email: String, password: String): Result<AuthUser>

    suspend fun signUp(email: String, password: String, fullName: String): Result<AuthUser>

    suspend fun signOut(): Result<Unit>

    fun currentUserId(): String?
}
