package com.vitran.shop.feature.seller.referral.data.repository

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.referral.data.mapper.toDomain
import com.vitran.shop.feature.seller.referral.data.remote.ReferralApi
import com.vitran.shop.feature.seller.referral.data.state.ReferralStateStore
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCode
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCodeValidation
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditId
import com.vitran.shop.feature.seller.referral.domain.model.ReferralProfile
import com.vitran.shop.feature.seller.referral.domain.repository.ReferralRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultReferralRepository(
    private val api: ReferralApi,
    private val stateStore: ReferralStateStore,
) : ReferralRepository {

    private val applyLock = Mutex()

    override val cachedProfile: StateFlow<ReferralProfile?> = stateStore.profile

    override suspend fun validateCode(code: ReferralCode): AppResult<ReferralCodeValidation> {
        val normalized = ReferralCode(code.value.trim())
        if (normalized.value.isEmpty()) {
            return AppResult.Success(ReferralCodeValidation.Invalid)
        }
        return when (val result = api.validateCode(normalized)) {
            is AppResult.Success ->
                AppResult.Success(
                    if (result.value.valid) ReferralCodeValidation.Valid
                    else ReferralCodeValidation.Invalid,
                )
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun getProfile(forceRefresh: Boolean): AppResult<ReferralProfile> {
        if (!forceRefresh) {
            stateStore.profile.value?.let { return AppResult.Success(it) }
        }
        return when (val result = api.getProfile()) {
            is AppResult.Success -> {
                val profile = result.value.referral.toDomain()
                stateStore.put(profile)
                AppResult.Success(profile)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun applyCredit(creditId: ReferralCreditId, shopId: ShopId): AppResult<Unit> {
        if (!applyLock.tryLock()) {
            return AppResult.Failure(AppError.Conflict(message = "Credit apply already in progress"))
        }
        return try {
            api.applyCredit(creditId, shopId)
        } finally {
            applyLock.unlock()
        }
    }
}
