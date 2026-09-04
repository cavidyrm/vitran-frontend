package com.vitran.shop.feature.auth.domain.error

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.error.FieldError
import com.vitran.shop.core.domain.error.FormFieldErrorSplit
import com.vitran.shop.core.domain.error.splitForForm

sealed interface AuthError {
    val message: String?

    data class InvalidCredentials(override val message: String?) : AuthError
    data class InvalidOtp(override val message: String?, val fieldErrors: List<FieldError> = emptyList()) : AuthError
    data class VerificationExpired(override val message: String?) : AuthError
    data class PhoneAlreadyRegistered(override val message: String?) : AuthError
    data class Validation(override val message: String?, val fieldErrors: List<FieldError>) : AuthError
    data class RateLimited(override val message: String?) : AuthError
    data class Network(override val message: String?) : AuthError
    data class SessionExpired(override val message: String?) : AuthError
    data class Forbidden(override val message: String?) : AuthError
    data class Unexpected(override val message: String?) : AuthError
}

fun AppError.toAuthError(): AuthError = when (this) {
    is AppError.Validation -> AuthError.Validation(message, fieldErrors)
    is AppError.Authentication.SessionExpired -> AuthError.SessionExpired(message)
    is AppError.Authentication.Unauthorized -> AuthError.InvalidCredentials(message)
    is AppError.Forbidden -> AuthError.Forbidden(message)
    is AppError.Network -> AuthError.Network(message)
    else -> {
        val credentialError = fieldErrors.firstOrNull { it.reason == "credentials" }
        if (credentialError != null) {
            AuthError.InvalidCredentials(credentialError.messages.firstOrNull() ?: message)
        } else {
            AuthError.Unexpected(message)
        }
    }
}

fun AuthError.splitForForm(
    knownReasons: Set<String>,
    reasonAliases: Map<String, String> = emptyMap(),
    fallbackMessage: String? = message,
): FormFieldErrorSplit {
    val errors = when (this) {
        is AuthError.Validation -> fieldErrors
        is AuthError.InvalidOtp -> fieldErrors
        else -> emptyList()
    }
    return errors.splitForForm(
        knownReasons = knownReasons,
        reasonAliases = reasonAliases,
        fallbackMessage = fallbackMessage,
    )
}
