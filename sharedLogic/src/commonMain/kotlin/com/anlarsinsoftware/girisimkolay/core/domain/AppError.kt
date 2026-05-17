package com.anlarsinsoftware.girisimkolay.core.domain

data class AppError(
    val code: String,
    val message: String,
    val isRetryable: Boolean = false
)
