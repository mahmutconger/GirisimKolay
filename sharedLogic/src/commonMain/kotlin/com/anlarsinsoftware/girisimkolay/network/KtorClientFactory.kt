package com.anlarsinsoftware.girisimkolay.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Creates a fully configured Ktor [HttpClient].
 * Platform-specific engine is provided by each platform's androidMain/iosMain.
 *
 * Configuration:
 * - JSON: ignores unknown keys for forward-compatibility with API changes
 * - Logging: full (headers + body) in Debug builds, disabled in Release
 * - Timeouts: 15s connect, 30s request (good defaults for mobile)
 */
fun createHttpClient(enableLogging: Boolean = false): HttpClient = HttpClient {

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true   // Safe against API schema changes
            isLenient = true           // Tolerates minor JSON malformations
            encodeDefaults = true
        })
    }

    if (enableLogging) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL       // Headers + body — only for debug
        }
    }

    install(HttpTimeout) {
        connectTimeoutMillis = 15_000  // 15s to establish connection
        requestTimeoutMillis = 30_000  // 30s for a full request/response cycle
        socketTimeoutMillis  = 30_000
    }
}
