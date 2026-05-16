package com.anlarsinsoftware.girisimkolay

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform