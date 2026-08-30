package com.vitran.shop.feature.engagement.contact.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.contact.data.mapper.toDomain
import com.vitran.shop.feature.engagement.contact.data.remote.ProductContactApi
import com.vitran.shop.feature.engagement.contact.domain.model.ContactProductResult
import com.vitran.shop.feature.engagement.contact.domain.repository.ProductContactRepository
import com.vitran.shop.feature.engagement.session.VisitorSessionProvider
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

internal class DefaultProductContactRepository(
    private val api: ProductContactApi,
    private val visitorSessionProvider: VisitorSessionProvider,
) : ProductContactRepository {
    override suspend fun contactProduct(productId: ProductId): AppResult<ContactProductResult> =
        when (
            val result = api.contactProduct(
                productId = productId,
                sessionId = visitorSessionProvider.sessionId(),
            )
        ) {
            is AppResult.Success -> AppResult.Success(result.value.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
}
