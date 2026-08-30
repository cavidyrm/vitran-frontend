package com.vitran.shop.feature.seller.referral.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class ReferralCode(val value: String)

@JvmInline
value class ReferralRecordId(val value: Long)

@JvmInline
value class ReferralCreditId(val value: Long)

@JvmInline
value class ReferredUserId(val value: Long)

sealed class ReferralCodeValidation {
    data object Valid : ReferralCodeValidation()
    data object Invalid : ReferralCodeValidation()
}
