package com.vitran.shop.feature.seller.subscription.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentSession
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import kotlinx.coroutines.flow.StateFlow

interface SubscriptionRepository {
    val subscriptionsByShopId: StateFlow<Map<ShopId, ShopSubscription>>

    suspend fun getSubscription(shopId: ShopId, forceRefresh: Boolean = false): AppResult<ShopSubscription>

    suspend fun refreshSubscription(shopId: ShopId): AppResult<ShopSubscription>

    /**
     * Initiates plan purchase. Non-idempotent — callers must not auto-retry.
     * Does not mutate local subscription state.
     */
    suspend fun startPlanPurchase(shopId: ShopId, planId: PlanId): AppResult<PaymentSession>
}
