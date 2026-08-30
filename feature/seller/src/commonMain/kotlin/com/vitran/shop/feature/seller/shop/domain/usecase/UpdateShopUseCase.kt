package com.vitran.shop.feature.seller.shop.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopPublicCacheInvalidator
import com.vitran.shop.feature.seller.shop.data.state.SellerShopStateStore
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository

/**
 * Updates an owned shop, keeps seller list cache in sync with the active filter,
 * and invalidates public shop cache because update resets active/confirmed.
 */
class UpdateShopUseCase(
    private val sellerShopRepository: SellerShopRepository,
    private val sellerShopStateStore: SellerShopStateStore,
    private val publicCacheInvalidator: ShopPublicCacheInvalidator,
    private val activeListFilter: () -> SellerShopFilter = { SellerShopFilter.All },
) {
    suspend operator fun invoke(command: UpdateShopCommand): AppResult<SellerShopDetails> =
        when (val result = sellerShopRepository.updateShop(command)) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                val details = result.value
                publicCacheInvalidator.invalidate(details.id)
                val summary = details.toListSummary()
                when (val filter = activeListFilter()) {
                    SellerShopFilter.Active -> {
                        if (!details.active) {
                            sellerShopStateStore.removeSummary(details.id)
                        } else {
                            sellerShopStateStore.updateSummary(summary)
                        }
                    }
                    SellerShopFilter.Inactive -> {
                        if (details.active) {
                            sellerShopStateStore.removeSummary(details.id)
                        } else {
                            sellerShopStateStore.updateSummary(summary)
                        }
                    }
                    SellerShopFilter.All -> {
                        sellerShopStateStore.updateSummary(summary)
                    }
                }
                AppResult.Success(details)
            }
        }
}

private fun SellerShopDetails.toListSummary(): SellerShopSummary =
    SellerShopSummary(
        id = id,
        title = title.orEmpty(),
        active = active,
        confirmed = confirmed,
        publicationState = publicationState,
    )
