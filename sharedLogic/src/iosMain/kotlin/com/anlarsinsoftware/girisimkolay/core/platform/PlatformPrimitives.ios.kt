package com.anlarsinsoftware.girisimkolay.core.platform

import platform.Foundation.NSDate
import platform.Foundation.NSUUID
import platform.Foundation.timeIntervalSince1970

actual object PlatformTimeProvider {
    actual fun nowMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
}

actual object PlatformIdProvider {
    actual fun randomId(): String = NSUUID().UUIDString()
}
