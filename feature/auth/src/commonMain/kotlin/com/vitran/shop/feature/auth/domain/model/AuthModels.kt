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

data class PhoneCheckResult(
    val status: PhoneCheckStatus,
    val exists: Boolean,
    val canRegister: Boolean,
    val canLogin: Boolean,
    val canResend: Boolean,
    val canResetPassword: Boolean,
    val nextStep: PhoneCheckNextStep,
    val otpPurpose: String? = null,
    val otpTtlSeconds: Int = 0,
    val otpExpiresAt: String? = null,
    val resendAfterSeconds: Int = 0,
)

enum class PhoneCheckStatus {
    Available,
    Registered,
    PendingVerification,
    Unverified,
    Inactive,
    Unknown,
    ;

    companion object {
        fun fromBackend(value: String?): PhoneCheckStatus =
            when (value?.lowercase()) {
                "available" -> Available
                "registered" -> Registered
                "pending_verification" -> PendingVerification
                "unverified" -> Unverified
                "inactive" -> Inactive
                else -> Unknown
            }
    }
}

enum class PhoneCheckNextStep {
    Register,
    Login,
    Verify,
    Unknown,
    ;

    companion object {
        fun fromBackend(value: String?): PhoneCheckNextStep =
            when (value?.lowercase()) {
                "register" -> Register
                "login" -> Login
                "verify" -> Verify
                else -> Unknown
            }
    }
}
