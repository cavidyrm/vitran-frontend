package com.vitran.shop.feature.seller.subscription.data.repository

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.subscription.data.mapper.toDomain
import com.vitran.shop.feature.seller.subscription.data.remote.SellerSubscriptionApi
import com.vitran.shop.feature.seller.subscription.data.state.SubscriptionStateStore
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentSession
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultSubscriptionRepository(
    private val api: SellerSubscriptionApi,
    private val stateStore: SubscriptionStateStore,
) : SubscriptionRepository {

    private val fetchMutexByShop = mutableMapOf<ShopId, Mutex>()
    private val purchaseLocks = mutableMapOf<ShopId, Mutex>()

    override val subscriptionsByShopId: StateFlow<Map<ShopId, ShopSubscription>> =
        stateStore.byShopId

    override suspend fun getSubscription(
        shopId: ShopId,
        forceRefresh: Boolean,
    ): AppResult<ShopSubscription> {
        if (!forceRefresh) {
            stateStore.get(shopId)?.let { return AppResult.Success(it) }
        }
        return fetchSubscription(shopId)
    }

    override suspend fun refreshSubscription(shopId: ShopId): AppResult<ShopSubscription> =
        getSubscription(shopId, forceRefresh = true)

    override suspend fun startPlanPurchase(shopId: ShopId, planId: PlanId): AppResult<PaymentSession> {
        val lock = purchaseLocks.getOrPut(shopId) { Mutex() }
        if (!lock.tryLock()) {
            return AppResult.Failure(
                AppError.Conflict(message = "Purchase already in progress for this shop"),
            )
        }
        return try {
            when (val result = api.purchasePlan(shopId, planId)) {
                is AppResult.Success -> AppResult.Success(result.value.payment.toDomain())
                is AppResult.Failure -> AppResult.Failure(result.error)
            }
        } finally {
            lock.unlock()
        }
    }

    private suspend fun fetchSubscription(shopId: ShopId): AppResult<ShopSubscription> {
        val lock = fetchMutexByShop.getOrPut(shopId) { Mutex() }
        return lock.withLock {
            when (val result = api.getSubscription(shopId)) {
                is AppResult.Success -> {
                    val subscription = result.value.subscription.toDomain()
                    stateStore.put(subscription)
                    AppResult.Success(subscription)
                }
                is AppResult.Failure -> AppResult.Failure(result.error)
            }
        }
    }
}
