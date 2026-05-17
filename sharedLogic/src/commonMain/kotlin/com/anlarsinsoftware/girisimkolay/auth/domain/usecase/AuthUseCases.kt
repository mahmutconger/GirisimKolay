package com.anlarsinsoftware.girisimkolay.auth.domain.usecase

import com.anlarsinsoftware.girisimkolay.auth.domain.entity.AuthUser
import com.anlarsinsoftware.girisimkolay.auth.domain.repository.AuthRepository
import com.anlarsinsoftware.girisimkolay.core.domain.Result

class SignIn(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AuthUser> =
        repository.signIn(email, password)
}

class SignUp(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, fullName: String): Result<AuthUser> =
        repository.signUp(email, password, fullName)
}
