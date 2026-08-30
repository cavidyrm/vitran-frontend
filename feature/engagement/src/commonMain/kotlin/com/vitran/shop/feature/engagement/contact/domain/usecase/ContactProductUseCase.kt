package com.vitran.shop.feature.engagement.contact.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.engagement.contact.domain.model.ContactProductResult
import com.vitran.shop.feature.engagement.contact.domain.repository.ProductContactRepository
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

/**
 * Contact already records shop analytics on the backend.
 * [UserPersonalizationEvent.PurchaseIntent] is a separate personalization signal.
 */
class ContactProductUseCase(
    private val productContactRepository: ProductContactRepository,
    private val analyticsTracker: MarketplaceAnalyticsTracker,
) {
    suspend operator fun invoke(productId: ProductId): AppResult<ContactProductResult> =
        when (val result = productContactRepository.contactProduct(productId)) {
            is AppResult.Success -> {
                analyticsTracker.track(
                    UserPersonalizationEvent.PurchaseIntent(
                        productId = result.value.intent.productId,
                        shopId = result.value.intent.shopId,
                    ),
                )
                result
            }
            is AppResult.Failure -> result
        }
}
