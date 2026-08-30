package com.vitran.shop.feature.seller.referral.data.mapper

import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.referral.data.remote.dto.ReferralCreditDto
import com.vitran.shop.feature.seller.referral.data.remote.dto.ReferralProfileDto
import com.vitran.shop.feature.seller.referral.data.remote.dto.ReferralRecordDto
import com.vitran.shop.feature.seller.referral.data.remote.dto.ReferralStatsDto
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCode
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCredit
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditId
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditSource
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditStatus
import com.vitran.shop.feature.seller.referral.domain.model.ReferralProfile
import com.vitran.shop.feature.seller.referral.domain.model.ReferralRecord
import com.vitran.shop.feature.seller.referral.domain.model.ReferralRecordId
import com.vitran.shop.feature.seller.referral.domain.model.ReferralRecordStatus
import com.vitran.shop.feature.seller.referral.domain.model.ReferralStats
import com.vitran.shop.feature.seller.referral.domain.model.ReferredUserId
import kotlinx.datetime.Instant

internal fun ReferralProfileDto.toDomain(): ReferralProfile =
    ReferralProfile(
        code = ReferralCode(referralCode),
        inviteUrl = inviteUrl,
        stats = stats.toDomain(),
        successfulReferrals = successfulReferrals.map { it.toDomain() },
        pendingReferrals = pendingReferrals.map { it.toDomain() },
        credits = credits.map { it.toDomain() },
    )

internal fun ReferralStatsDto.toDomain(): ReferralStats =
    ReferralStats(
        totalReferrals = totalReferrals,
        rewardedReferrals = rewardedReferrals,
        pendingReferrals = pendingReferrals,
        availableCredits = availableCredits,
    )

internal fun ReferralRecordDto.toDomain(): ReferralRecord =
    ReferralRecord(
        id = ReferralRecordId(id),
        referredUserId = ReferredUserId(referredUserId),
        phoneMasked = phoneMasked,
        status = ReferralRecordStatus.parse(status),
        signedUpAt = Instant.parse(signedUpAt),
        rewardedAt = rewardedAt?.let { runCatching { Instant.parse(it) }.getOrNull() },
    )

internal fun ReferralCreditDto.toDomain(): ReferralCredit =
    ReferralCredit(
        id = ReferralCreditId(id),
        planId = PlanId(planId),
        planTitle = planTitle,
        durationDays = durationDays,
        source = ReferralCreditSource.parse(source),
        status = ReferralCreditStatus.parse(status),
        createdAt = Instant.parse(createdAt),
    )
