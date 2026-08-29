package com.vitran.shop.core.domain.result

import com.vitran.shop.core.domain.error.AppError

/**
 * Explicit success/failure result for data-layer operations.
 */
sealed class AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>()

    data class Failure(val error: AppError) : AppResult<Nothing>()

    val isSuccess: Boolean get() = this is Success

    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = (this as? Success)?.value

    fun errorOrNull(): AppError? = (this as? Failure)?.error
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(value)
    return this
}

inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error)
    return this
}
