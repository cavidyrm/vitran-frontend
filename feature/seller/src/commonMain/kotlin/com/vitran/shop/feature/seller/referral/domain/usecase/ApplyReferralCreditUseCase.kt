package com.vitran.shop.feature.seller.referral.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopPublicCacheInvalidator
import com.vitran.shop.feature.seller.referral.domain.ReferralCreditEligibility
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditId
import com.vitran.shop.feature.seller.referral.domain.model.ReferralProfile
import com.vitran.shop.feature.seller.referral.domain.repository.ReferralRepository
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository

data class ApplyReferralCreditResult(
    val profile: ReferralProfile,
    val subscription: ShopSubscription?,
)

/**
 * Applies an available referral credit, then refreshes subscription + profile.
 * Non-idempotent — do not auto-retry. Does not locally invent expiry.
 */
class ApplyReferralCreditUseCase(
    private val referralRepository: ReferralRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val shopPublicCacheInvalidator: ShopPublicCacheInvalidator,
) {
    suspend operator fun invoke(
        creditId: ReferralCreditId,
        shopId: ShopId,
    ): AppResult<ApplyReferralCreditResult> {
        val profile =
            when (val result = referralRepository.getProfile()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return AppResult.Failure(result.error)
            }

        val credit = profile.credits.firstOrNull { it.id == creditId }
            ?: return AppResult.Failure(AppError.NotFound(message = "Credit not found"))

        if (!credit.isAvailable) {
            return AppResult.Failure(AppError.Validation(message = "Credit is not available"))
        }

        val subscription =
            when (val sub = subscriptionRepository.getSubscription(shopId)) {
                is AppResult.Success -> sub.value
                is AppResult.Failure -> null
            }

        if (subscription != null &&
            !ReferralCreditEligibility.isEligibleOrUnknown(subscription.plan.slug)
        ) {
            return AppResult.Failure(
                AppError.Validation(message = "Shop plan is not eligible for this credit"),
            )
        }

        when (val apply = referralRepository.applyCredit(creditId, shopId)) {
            is AppResult.Failure -> return AppResult.Failure(apply.error)
            is AppResult.Success -> Unit
        }

        val refreshedSub =
            when (val sub = subscriptionRepository.refreshSubscription(shopId)) {
                is AppResult.Success -> sub.value
                is AppResult.Failure -> null
            }

        val refreshedProfile =
            when (val p = referralRepository.getProfile(forceRefresh = true)) {
                is AppResult.Success -> p.value
                is AppResult.Failure -> profile
            }

        runCatching { shopPublicCacheInvalidator.invalidate(shopId) }

        return AppResult.Success(
            ApplyReferralCreditResult(
                profile = refreshedProfile,
                subscription = refreshedSub,
            ),
        )
    }
}
