package com.vitran.shop.feature.seller.product.data.state

import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory, user-scoped seller-owned product cache.
 * Cleared on logout and terminal session invalidation.
 * Must not be mixed with public marketplace product cache.
 */
class SellerProductStateStore(
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : SessionInvalidationListener {

    private val _summaries = MutableStateFlow<List<SellerProductSummary>>(emptyList())
    val summaries: StateFlow<List<SellerProductSummary>> = _summaries.asStateFlow()

    private val _detailsById = MutableStateFlow<Map<ProductId, SellerProductDetails>>(emptyMap())
    val detailsById: StateFlow<Map<ProductId, SellerProductDetails>> = _detailsById.asStateFlow()

    init {
        invalidationListeners.add(this)
    }

    fun replaceSummaries(items: List<SellerProductSummary>) {
        _summaries.value = items
    }

    fun upsertSummary(summary: SellerProductSummary) {
        _summaries.update { current ->
            val without = current.filterNot { it.id == summary.id }
            listOf(summary) + without
        }
    }

    fun updateSummary(summary: SellerProductSummary) {
        _summaries.update { current ->
            current.map { if (it.id == summary.id) summary else it }
        }
    }

    fun removeSummary(productId: ProductId) {
        _summaries.update { it.filterNot { item -> item.id == productId } }
    }

    fun putDetails(details: SellerProductDetails) {
        _detailsById.update { it + (details.id to details) }
    }

    fun getDetails(productId: ProductId): SellerProductDetails? = _detailsById.value[productId]

    fun removeDetails(productId: ProductId) {
        _detailsById.update { it - productId }
    }

    fun clear() {
        _summaries.value = emptyList()
        _detailsById.value = emptyMap()
    }

    override suspend fun onSessionInvalidated() {
        clear()
    }
}
