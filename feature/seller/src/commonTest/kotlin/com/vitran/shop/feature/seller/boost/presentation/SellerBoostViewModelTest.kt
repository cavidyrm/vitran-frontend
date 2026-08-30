package com.vitran.shop.feature.seller.boost.presentation

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.boost.domain.model.ActiveBoosts
import com.vitran.shop.feature.seller.boost.domain.model.BoostTarget
import com.vitran.shop.feature.seller.boost.domain.model.CreateBoostCommand
import com.vitran.shop.feature.seller.boost.domain.model.CreatedBoost
import com.vitran.shop.feature.seller.boost.domain.repository.SellerBoostRepository
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SellerBoostViewModelTest {

    @Test
    fun emptyBoosts_areEmptyContent() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo = FakeBoostRepo(ActiveBoosts.Empty)
            val vm =
                SellerBoostsViewModel(
                    FakeShopRepo(listOf(SellerShopSummary(ShopId(1), "S", true, true))),
                    repo,
                )
            advanceUntilIdle()
            assertIs<ActiveBoostsContentState.Empty>(vm.uiState.value.content)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun createBoostSubmit_neverCallsRepository() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val boostRepo = FakeBoostRepo(ActiveBoosts.Empty)
            val vm =
                CreateBoostViewModel(
                    FakeShopRepo(listOf(SellerShopSummary(ShopId(1), "S", true, true))),
                )
            advanceUntilIdle()
            vm.selectProductTarget(ProductId(4))
            vm.submit()
            vm.submit()
            advanceUntilIdle()
            assertEquals(0, boostRepo.createCalls)
            assertIs<BoostTarget.Product>(vm.uiState.value.target)
            assertIs<BoostPricingState.PricingContractUnresolved>(vm.uiState.value.pricing)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

private class FakeBoostRepo(
    private val list: ActiveBoosts,
) : SellerBoostRepository {
    var createCalls = 0

    override suspend fun getActiveBoosts(shopId: ShopId, forceRefresh: Boolean) =
        AppResult.Success(list)

    override suspend fun createBoost(command: CreateBoostCommand): AppResult<CreatedBoost> {
        createCalls += 1
        error("CreateBoostViewModel must not call createBoost")
    }
}

private class FakeShopRepo(
    private val shops: List<SellerShopSummary>,
) : SellerShopRepository {
    override suspend fun checkSlugAvailability(
        slug: com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug,
        excludeId: ShopId?,
    ) = AppResult.Failure(AppError.Unexpected())

    override suspend fun createShop(command: CreateShopCommand) = AppResult.Failure(AppError.Unexpected())

    override suspend fun getMyShops(query: SellerShopListQuery) =
        AppResult.Success(CursorPage(shops, null, false))

    override suspend fun getMyShop(shopId: ShopId) = AppResult.Failure(AppError.NotFound())

    override suspend fun updateShop(command: UpdateShopCommand) = AppResult.Failure(AppError.Unexpected())

    override suspend fun getFulfillmentOptions(shopId: ShopId) =
        AppResult.Success(emptyList<com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode>())

    override suspend fun regenerateApiKey(shopId: ShopId) = AppResult.Failure(AppError.Unexpected())
}
