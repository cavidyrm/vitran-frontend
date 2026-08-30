package com.vitran.shop.ui.sections.account

import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditStatus
import com.vitran.shop.feature.seller.referral.domain.model.ReferralProfile as DomainReferralProfile
import com.vitran.shop.feature.seller.referral.domain.model.ReferralRecordStatus

fun mapReferralProfileToUi(profile: DomainReferralProfile): ReferralProfile =
    ReferralProfile(
        code = profile.code.value,
        inviteUrl = profile.inviteUrl,
        stats =
            ReferralStats(
                total = profile.stats.totalReferrals,
                rewarded = profile.stats.rewardedReferrals,
                pending = profile.stats.pendingReferrals,
                remainingSellerDays = 0,
                availableCreditToman = 0,
            ),
        successful =
            profile.successfulReferrals.map { record ->
                ReferralEntry(
                    id = record.id.value.toString(),
                    referredUserId = record.referredUserId.value.toString(),
                    phoneMasked = record.phoneMasked,
                    status =
                        when (record.status) {
                            ReferralRecordStatus.Successful -> ReferralStatus.Successful
                            ReferralRecordStatus.Pending -> ReferralStatus.Pending
                            is ReferralRecordStatus.Unknown -> ReferralStatus.Pending
                        },
                    signedUpAt = record.signedUpAt.toString(),
                    rewardedAt = record.rewardedAt?.toString(),
                )
            },
        pending =
            profile.pendingReferrals.map { record ->
                ReferralEntry(
                    id = record.id.value.toString(),
                    referredUserId = record.referredUserId.value.toString(),
                    phoneMasked = record.phoneMasked,
                    status = ReferralStatus.Pending,
                    signedUpAt = record.signedUpAt.toString(),
                    rewardedAt = null,
                )
            },
        credits =
            profile.credits.map { credit ->
                ReferralCredit(
                    id = credit.id.value.toString(),
                    planId = credit.planId.value.toString(),
                    planTitle = credit.planTitle,
                    durationDays = credit.durationDays,
                    totalDays = credit.durationDays,
                    source = when (val source = credit.source) {
                        is com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditSource.ReferralReferrer ->
                            "دعوت شما"
                        is com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditSource.Unknown ->
                            source.raw
                    },
                    status =
                        when (credit.status) {
                            ReferralCreditStatus.Available -> CreditStatus.Available
                            is ReferralCreditStatus.Unknown -> CreditStatus.Used
                        },
                    createdAt = credit.createdAt.toString(),
                    isActive = credit.isAvailable,
                )
            },
    )
