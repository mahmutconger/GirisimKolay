package com.anlarsinsoftware.girisimkolay.core.domain

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(
        val error: AppError,
        val throwable: Throwable? = null
    ) : Result<Nothing> {
        constructor(
            message: String,
            throwable: Throwable? = null,
            code: String = "unknown",
            isRetryable: Boolean = false
        ) : this(
            error = AppError(
                code = code,
                message = message,
                isRetryable = isRetryable
            ),
            throwable = throwable
        )

        val message: String
            get() = error.message
    }

    object Loading : Result<Nothing>
}
