package com.anlarsinsoftware.girisimkolay.auth.data

import com.anlarsinsoftware.girisimkolay.core.domain.BearerTokenProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseIdTokenProvider(
    private val auth: FirebaseAuth
) : BearerTokenProvider {
    override suspend fun getFreshToken(): String? =
        auth.currentUser?.getIdToken(true)?.await()?.token
}
