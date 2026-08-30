package com.vitran.shop.feature.seller.referral.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCode
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCodeValidation
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditId
import com.vitran.shop.feature.seller.referral.domain.model.ReferralProfile
import kotlinx.coroutines.flow.StateFlow

interface ReferralRepository {
    val cachedProfile: StateFlow<ReferralProfile?>

    suspend fun validateCode(code: ReferralCode): AppResult<ReferralCodeValidation>

    suspend fun getProfile(forceRefresh: Boolean = false): AppResult<ReferralProfile>

    /**
     * Consumptive. Do not auto-retry.
     */
    suspend fun applyCredit(creditId: ReferralCreditId, shopId: ShopId): AppResult<Unit>
}
