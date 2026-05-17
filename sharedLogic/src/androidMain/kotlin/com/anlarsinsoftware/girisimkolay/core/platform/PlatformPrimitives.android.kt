package com.anlarsinsoftware.girisimkolay.core.platform

import java.util.UUID

actual object PlatformTimeProvider {
    actual fun nowMillis(): Long = System.currentTimeMillis()
}

actual object PlatformIdProvider {
    actual fun randomId(): String = UUID.randomUUID().toString()
}
