package com.vitran.shop.feature.seller.boost.data.repository

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.boost.data.mapper.toDomain
import com.vitran.shop.feature.seller.boost.data.mapper.toRequestDto
import com.vitran.shop.feature.seller.boost.data.remote.SellerBoostApi
import com.vitran.shop.feature.seller.boost.data.state.SellerBoostStateStore
import com.vitran.shop.feature.seller.boost.domain.model.ActiveBoosts
import com.vitran.shop.feature.seller.boost.domain.model.CreateBoostCommand
import com.vitran.shop.feature.seller.boost.domain.model.CreatedBoost
import com.vitran.shop.feature.seller.boost.domain.repository.SellerBoostRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultSellerBoostRepository(
    private val api: SellerBoostApi,
    private val stateStore: SellerBoostStateStore,
) : SellerBoostRepository {

    private val createLocks = mutableMapOf<ShopId, Mutex>()
    private val fetchLocks = mutableMapOf<ShopId, Mutex>()

    override suspend fun getActiveBoosts(
        shopId: ShopId,
        forceRefresh: Boolean,
    ): AppResult<ActiveBoosts> {
        if (!forceRefresh) {
            stateStore.get(shopId)?.let { return AppResult.Success(it) }
        }
        val lock = fetchLocks.getOrPut(shopId) { Mutex() }
        return lock.withLock {
            when (val result = api.listActiveBoosts(shopId)) {
                is AppResult.Success -> {
                    val mapped = result.value.toDomain()
                    stateStore.put(shopId, mapped)
                    AppResult.Success(mapped)
                }
                is AppResult.Failure -> AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun createBoost(command: CreateBoostCommand): AppResult<CreatedBoost> {
        val lock = createLocks.getOrPut(command.shopId) { Mutex() }
        if (!lock.tryLock()) {
            return AppResult.Failure(
                AppError.Conflict(message = "Boost creation already in progress for this shop"),
            )
        }
        return try {
            when (val result = api.createBoost(command.shopId, command.toRequestDto())) {
                is AppResult.Success -> {
                    val created = result.value.boost.toDomain()
                    getActiveBoosts(command.shopId, forceRefresh = true)
                    AppResult.Success(created)
                }
                is AppResult.Failure -> AppResult.Failure(result.error)
            }
        } finally {
            lock.unlock()
        }
    }
}
