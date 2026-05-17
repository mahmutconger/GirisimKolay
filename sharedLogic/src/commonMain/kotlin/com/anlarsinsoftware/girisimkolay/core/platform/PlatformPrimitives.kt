package com.anlarsinsoftware.girisimkolay.core.platform

expect object PlatformTimeProvider {
    fun nowMillis(): Long
}

expect object PlatformIdProvider {
    fun randomId(): String
}
