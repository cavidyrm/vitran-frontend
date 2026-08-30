package com.vitran.shop.feature.seller.shop.presentation

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.seller.createShopWithTokenBody
import com.vitran.shop.feature.seller.createSellerRepository
import com.vitran.shop.feature.seller.jsonResponse
import com.vitran.shop.feature.seller.slugAvailableBody
import com.vitran.shop.feature.seller.slugTakenBody
import com.vitran.shop.feature.seller.FakeAccountRepository
import com.vitran.shop.feature.seller.FakeSessionRepository
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.ShopApiKey
import com.vitran.shop.feature.seller.shop.domain.model.ShopSlugAvailability
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.shop.domain.usecase.CreateShopUseCase
import com.vitran.shop.feature.seller.fulfillmentBody
import com.vitran.shop.feature.seller.sellerGetPendingBody
import com.vitran.shop.feature.seller.apiKeyBody
import com.vitran.shop.core.domain.pagination.CursorPage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SellerViewModelTest {

    @Test
    fun createShopViewModel_slugDebounceAndCancellation() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            var lastSlug: String? = null
            val repo =
                object : SellerShopRepository by ThrowingSellerShopRepository() {
                    override suspend fun checkSlugAvailability(
                        slug: ShopSlug,
                        excludeId: ShopId?,
                    ): AppResult<ShopSlugAvailability> {
                        lastSlug = slug.value
                        return AppResult.Success(ShopSlugAvailability(slug, isAvailable = true))
                    }
                }
            val vm =
                CreateShopViewModel(
                    createShopUseCase =
                        CreateShopUseCase(repo, FakeSessionRepository(), FakeAccountRepository()),
                    sellerShopRepository = repo,
                    slugDebounceMs = 400,
                )
            vm.onSlugInputChanged("my")
            vm.onSlugInputChanged("my-s")
            vm.onSlugInputChanged("my-shop")
            advanceTimeBy(399)
            assertEquals(null, lastSlug)
            advanceTimeBy(2)
            advanceUntilIdle()
            assertEquals("my-shop", lastSlug)
            assertIs<SlugCheckUiStatus.Available>(vm.uiState.value.slugCheck)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun createShopViewModel_emptySlug_skipsNetwork() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            var checks = 0
            val repo =
                object : SellerShopRepository by ThrowingSellerShopRepository() {
                    override suspend fun checkSlugAvailability(
                        slug: ShopSlug,
                        excludeId: ShopId?,
                    ): AppResult<ShopSlugAvailability> {
                        checks += 1
                        return AppResult.Success(ShopSlugAvailability(slug, true))
                    }
                }
            val vm =
                CreateShopViewModel(
                    CreateShopUseCase(repo, FakeSessionRepository(), FakeAccountRepository()),
                    repo,
                )
            vm.onSlugInputChanged("")
            advanceUntilIdle()
            assertEquals(0, checks)
            assertIs<SlugCheckUiStatus.Idle>(vm.uiState.value.slugCheck)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun createShopViewModel_duplicateSubmitPrevented() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            var creates = 0
            val repo =
                object : SellerShopRepository by ThrowingSellerShopRepository() {
                    override suspend fun createShop(command: CreateShopCommand) =
                        AppResult.Success(
                            com.vitran.shop.feature.seller.shop.domain.model.CreateShopResult(
                                shop =
                                    SellerShopDetails(
                                        id = ShopId(1),
                                        slug = ShopSlug("a"),
                                        active = false,
                                        confirmed = false,
                                        title = command.title,
                                    ),
                            ),
                        ).also { creates += 1 }
                }
            val vm =
                CreateShopViewModel(
                    CreateShopUseCase(repo, FakeSessionRepository(), FakeAccountRepository()),
                    repo,
                )
            val command =
                CreateShopCommand(title = "Shop", type = "retailer", cityId = CityId(1))
            vm.submit(command)
            vm.submit(command)
            advanceUntilIdle()
            assertEquals(1, creates)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun sellerShopDetails_fulfillmentFailureDoesNotClearShop() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo =
                object : SellerShopRepository by ThrowingSellerShopRepository() {
                    override suspend fun getMyShop(shopId: ShopId) =
                        AppResult.Success(
                            SellerShopDetails(
                                id = shopId,
                                slug = ShopSlug("my-shop"),
                                active = false,
                                confirmed = false,
                            ),
                        )

                    override suspend fun getFulfillmentOptions(shopId: ShopId) =
                        AppResult.Failure(AppError.Network.Timeout())
                }
            val vm = SellerShopDetailsViewModel(ShopId(1), repo)
            advanceUntilIdle()
            assertNotNullShop(vm)
            assertIs<FulfillmentOptionsUiState.Error>(vm.uiState.value.fulfillment)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun apiKeyViewModel_duplicateRegeneratePrevented_andDismissClears() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            var calls = 0
            val repo =
                object : SellerShopRepository by ThrowingSellerShopRepository() {
                    override suspend fun regenerateApiKey(shopId: ShopId): AppResult<ShopApiKey> {
                        calls += 1
                        return AppResult.Success(ShopApiKey.of("vt_live_xxxxxxxx"))
                    }
                }
            val vm = ShopApiKeyViewModel(ShopId(1), repo)
            vm.requestRegeneration()
            vm.confirmRegeneration()
            vm.confirmRegeneration()
            advanceUntilIdle()
            assertEquals(1, calls)
            assertIs<ShopApiKeyUiState.Generated>(vm.uiState.value)
            vm.dismiss()
            assertIs<ShopApiKeyUiState.Hidden>(vm.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun assertNotNullShop(vm: SellerShopDetailsViewModel) {
        assertTrue(vm.uiState.value.shop != null)
    }
}

private open class ThrowingSellerShopRepository : SellerShopRepository {
    override suspend fun checkSlugAvailability(slug: ShopSlug, excludeId: ShopId?) =
        error("unexpected")

    override suspend fun createShop(command: CreateShopCommand) = error("unexpected")

    override suspend fun getMyShops(query: SellerShopListQuery) = error("unexpected")

    override suspend fun getMyShop(shopId: ShopId) = error("unexpected")

    override suspend fun updateShop(command: UpdateShopCommand) = error("unexpected")

    override suspend fun getFulfillmentOptions(shopId: ShopId) = error("unexpected")

    override suspend fun regenerateApiKey(shopId: ShopId) = error("unexpected")
}
