package com.vitran.shop.feature.seller.shop.data.state

import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory, user-scoped seller-owned shop cache.
 * Cleared on logout and terminal session invalidation.
 * Must not be mixed with public marketplace shop cache.
 */
class SellerShopStateStore(
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : SessionInvalidationListener {

    private val _summaries = MutableStateFlow<List<SellerShopSummary>>(emptyList())
    val summaries: StateFlow<List<SellerShopSummary>> = _summaries.asStateFlow()

    private val _detailsById = MutableStateFlow<Map<ShopId, SellerShopDetails>>(emptyMap())
    val detailsById: StateFlow<Map<ShopId, SellerShopDetails>> = _detailsById.asStateFlow()

    init {
        invalidationListeners.add(this)
    }

    fun replaceSummaries(items: List<SellerShopSummary>) {
        _summaries.value = items
    }

    fun upsertSummary(summary: SellerShopSummary) {
        _summaries.update { current ->
            val without = current.filterNot { it.id == summary.id }
            listOf(summary) + without
        }
    }

    fun updateSummary(summary: SellerShopSummary) {
        _summaries.update { current ->
            current.map { if (it.id == summary.id) summary else it }
        }
    }

    fun removeSummary(shopId: ShopId) {
        _summaries.update { it.filterNot { item -> item.id == shopId } }
    }

    fun putDetails(details: SellerShopDetails) {
        _detailsById.update { it + (details.id to details) }
    }

    fun getDetails(shopId: ShopId): SellerShopDetails? = _detailsById.value[shopId]

    fun clear() {
        _summaries.value = emptyList()
        _detailsById.value = emptyMap()
    }

    override suspend fun onSessionInvalidated() {
        clear()
    }
}
