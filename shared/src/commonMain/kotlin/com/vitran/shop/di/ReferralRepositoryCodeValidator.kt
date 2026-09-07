package com.vitran.shop.di

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.auth.domain.ReferralCodeValidator
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCode
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCodeValidation
import com.vitran.shop.feature.seller.referral.domain.repository.ReferralRepository

class ReferralRepositoryCodeValidator(
    private val referralRepository: ReferralRepository,
) : ReferralCodeValidator {
    override suspend fun validate(code: String): AppResult<Boolean> =
        when (val result = referralRepository.validateCode(ReferralCode(code))) {
            is AppResult.Success -> AppResult.Success(result.value is ReferralCodeValidation.Valid)
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
}
