package com.vitran.shop.feature.seller.referral.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReferralValidationDataDto(
    val valid: Boolean,
)

@Serializable
internal data class ReferralProfileDataDto(
    val referral: ReferralProfileDto,
)

@Serializable
internal data class ReferralProfileDto(
    @SerialName("referral_code") val referralCode: String,
    @SerialName("invite_url") val inviteUrl: String,
    val stats: ReferralStatsDto,
    @SerialName("successful_referrals") val successfulReferrals: List<ReferralRecordDto> = emptyList(),
    @SerialName("pending_referrals") val pendingReferrals: List<ReferralRecordDto> = emptyList(),
    val credits: List<ReferralCreditDto> = emptyList(),
)

@Serializable
internal data class ReferralStatsDto(
    @SerialName("total_referrals") val totalReferrals: Int,
    @SerialName("rewarded_referrals") val rewardedReferrals: Int,
    @SerialName("pending_referrals") val pendingReferrals: Int,
    @SerialName("available_credits") val availableCredits: Int,
)

@Serializable
internal data class ReferralRecordDto(
    val id: Long,
    @SerialName("referred_user_id") val referredUserId: Long,
    @SerialName("phone_masked") val phoneMasked: String,
    val status: String,
    @SerialName("signed_up_at") val signedUpAt: String,
    @SerialName("rewarded_at") val rewardedAt: String? = null,
)

@Serializable
internal data class ReferralCreditDto(
    val id: Long,
    @SerialName("plan_id") val planId: Long,
    @SerialName("plan_title") val planTitle: String,
    @SerialName("duration_days") val durationDays: Int,
    val source: String,
    val status: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
internal data class ApplyReferralCreditRequestDto(
    @SerialName("shop_id") val shopId: Long,
)
