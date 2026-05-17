package com.anlarsinsoftware.girisimkolay.core.domain

import com.anlarsinsoftware.girisimkolay.core.platform.PlatformIdProvider
import com.anlarsinsoftware.girisimkolay.core.platform.PlatformTimeProvider

interface Clock {
    fun nowMillis(): Long
}

object DefaultClock : Clock {
    override fun nowMillis(): Long = PlatformTimeProvider.nowMillis()
}

interface IdProvider {
    fun randomId(): String
}

object DefaultIdProvider : IdProvider {
    override fun randomId(): String = PlatformIdProvider.randomId()
}

enum class CachePolicy {
    CACHE_ONLY,
    NETWORK_FIRST,
    CACHE_FIRST
}

interface Logger {
    fun debug(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

interface BearerTokenProvider {
    suspend fun getFreshToken(): String?
}

interface SessionStateStore {
    fun getActiveSessionId(): String?
    fun saveActiveSessionId(sessionId: String)
    fun clear()
}

object NoopLogger : Logger {
    override fun debug(tag: String, message: String) = Unit

    override fun error(tag: String, message: String, throwable: Throwable?) = Unit
}
