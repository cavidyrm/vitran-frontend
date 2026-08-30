package com.vitran.shop.feature.seller.boost.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopPublicCacheInvalidator
import com.vitran.shop.feature.seller.boost.domain.model.ActiveBoosts
import com.vitran.shop.feature.seller.boost.domain.model.BoostId
import com.vitran.shop.feature.seller.boost.domain.model.BoostTarget
import com.vitran.shop.feature.seller.boost.domain.model.CreateBoostCommand
import com.vitran.shop.feature.seller.boost.domain.model.CreatedBoost
import com.vitran.shop.feature.seller.boost.domain.repository.SellerBoostRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class CreatePlacementBoostUseCaseTest {

    @Test
    fun success_invalidatesPublicShopCache() = runTest {
        var invalidated: ShopId? = null
        val repo =
            object : SellerBoostRepository {
                override suspend fun getActiveBoosts(shopId: ShopId, forceRefresh: Boolean) =
                    AppResult.Success(ActiveBoosts.Empty)

                override suspend fun createBoost(command: CreateBoostCommand) =
                    AppResult.Success(CreatedBoost(BoostId(1), command.shopId, command.days))
            }
        val useCase =
            CreatePlacementBoostUseCase(
                boostRepository = repo,
                shopPublicCacheInvalidator = ShopPublicCacheInvalidator { invalidated = it },
            )
        val result =
            useCase(
                CreateBoostCommand(ShopId(3), BoostTarget.Shop, days = 7, pricePaid = 1),
            )
        val created = assertIs<AppResult.Success<CreatedBoost>>(result).value
        assertEquals(ShopId(3), invalidated)
        assertEquals(BoostId(1), created.id)
    }
}
