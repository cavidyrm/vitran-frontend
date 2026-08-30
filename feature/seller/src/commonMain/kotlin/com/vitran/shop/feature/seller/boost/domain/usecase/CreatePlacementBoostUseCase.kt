package com.vitran.shop.feature.seller.boost.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopPublicCacheInvalidator
import com.vitran.shop.feature.seller.boost.domain.model.CreateBoostCommand
import com.vitran.shop.feature.seller.boost.domain.model.CreatedBoost
import com.vitran.shop.feature.seller.boost.domain.repository.SellerBoostRepository

/**
 * Creates a placement boost once, then invalidates public shop cache.
 * Does not reorder rankings. Does not emit promotion impression events.
 * [CreateBoostCommand.pricePaid] is an opaque transport field, not a pricing policy.
 */
class CreatePlacementBoostUseCase(
    private val boostRepository: SellerBoostRepository,
    private val shopPublicCacheInvalidator: ShopPublicCacheInvalidator,
) {
    suspend operator fun invoke(command: CreateBoostCommand): AppResult<CreatedBoost> =
        when (val result = boostRepository.createBoost(command)) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                shopPublicCacheInvalidator.invalidate(command.shopId)
                result
            }
        }
}
