package com.vitran.shop.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterRequestDto(
    val phone: String,
    val password: String,
    @SerialName("referral_code") val referralCode: String? = null,
)

@Serializable
internal data class RegisterDataDto(
    @SerialName("temp_token") val tempToken: String,
    @SerialName("otp_code") val otpCode: String? = null,
)

@Serializable
internal data class VerifyRequestDto(
    @SerialName("temp_token") val tempToken: String,
    val code: String,
)

@Serializable
internal data class TokenSetDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_at") val expiresAt: String,
)

@Serializable
internal data class VerifyDataDto(
    val tokens: TokenSetDto,
)

@Serializable
internal data class LoginRequestDto(
    val phone: String,
    val password: String,
)

@Serializable
internal data class LoginTokensDataDto(
    val tokens: TokenSetDto,
)

@Serializable
internal data class LoginVerificationRequiredDataDto(
    @SerialName("temp_token") val tempToken: String,
    @SerialName("otp_code") val otpCode: String? = null,
)

@Serializable
internal data class CheckPhoneRequestDto(
    val phone: String,
)

@Serializable
internal data class CheckPhoneDataDto(
    val status: String,
    val exists: Boolean = false,
    @SerialName("can_register") val canRegister: Boolean = false,
    @SerialName("can_login") val canLogin: Boolean = false,
    @SerialName("can_resend") val canResend: Boolean = false,
    @SerialName("can_reset_password") val canResetPassword: Boolean = false,
    @SerialName("next_step") val nextStep: String? = null,
    @SerialName("otp_purpose") val otpPurpose: String? = null,
    @SerialName("otp_ttl") val otpTtl: Int = 0,
    @SerialName("otp_expires_at") val otpExpiresAt: String? = null,
    @SerialName("resend_after") val resendAfter: Int = 0,
)

@Serializable
internal data class ResendOtpRequestDto(
    val phone: String,
)

@Serializable
internal data class ResendOtpDataDto(
    @SerialName("otp_code") val otpCode: String? = null,
)

@Serializable
internal data class ForgotPasswordRequestDto(
    val phone: String,
)

@Serializable
internal data class ForgotPasswordDataDto(
    @SerialName("otp_code") val otpCode: String? = null,
)

@Serializable
internal data class ResetPasswordRequestDto(
    val phone: String,
    val code: String,
    @SerialName("new_password") val newPassword: String,
)
