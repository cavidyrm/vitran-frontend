package com.vitran.shop.feature.seller.referral.domain.model

import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import kotlinx.datetime.Instant

data class ReferralStats(
    val totalReferrals: Int,
    val rewardedReferrals: Int,
    val pendingReferrals: Int,
    val availableCredits: Int,
)

sealed class ReferralRecordStatus {
    data object Successful : ReferralRecordStatus()
    data object Pending : ReferralRecordStatus()
    data class Unknown(val raw: String) : ReferralRecordStatus()

    companion object {
        fun parse(raw: String): ReferralRecordStatus =
            when (raw.lowercase()) {
                "successful" -> Successful
                "pending" -> Pending
                else -> Unknown(raw)
            }
    }
}

data class ReferralRecord(
    val id: ReferralRecordId,
    val referredUserId: ReferredUserId,
    val phoneMasked: String,
    val status: ReferralRecordStatus,
    val signedUpAt: Instant,
    val rewardedAt: Instant?,
)

sealed class ReferralCreditStatus {
    data object Available : ReferralCreditStatus()
    data class Unknown(val raw: String) : ReferralCreditStatus()

    companion object {
        fun parse(raw: String): ReferralCreditStatus =
            when (raw.lowercase()) {
                "available" -> Available
                else -> Unknown(raw)
            }
    }
}

sealed class ReferralCreditSource {
    data object ReferralReferrer : ReferralCreditSource()
    data class Unknown(val raw: String) : ReferralCreditSource()

    companion object {
        fun parse(raw: String): ReferralCreditSource =
            when (raw.lowercase()) {
                "referral_referrer" -> ReferralReferrer
                else -> Unknown(raw)
            }
    }
}

data class ReferralCredit(
    val id: ReferralCreditId,
    val planId: PlanId,
    val planTitle: String,
    val durationDays: Int,
    val source: ReferralCreditSource,
    val status: ReferralCreditStatus,
    val createdAt: Instant,
) {
    val isAvailable: Boolean get() = status is ReferralCreditStatus.Available
}

data class ReferralProfile(
    val code: ReferralCode,
    val inviteUrl: String,
    val stats: ReferralStats,
    val successfulReferrals: List<ReferralRecord>,
    val pendingReferrals: List<ReferralRecord>,
    val credits: List<ReferralCredit>,
)
