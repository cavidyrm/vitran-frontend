package com.vitran.shop.feature.auth.domain.model

data class VerificationChallenge(
    val phone: String,
    val tempToken: String,
    val developmentOtp: String? = null,
)

sealed interface LoginResult {
    data object Authenticated : LoginResult
    data class VerificationRequired(val challenge: VerificationChallenge) : LoginResult
}

data class RegisterCommand(
    val phone: String,
    val password: String,
    val referralCode: String? = null,
)

data class PasswordResetContext(
    val phone: String,
    val developmentOtp: String? = null,
)
