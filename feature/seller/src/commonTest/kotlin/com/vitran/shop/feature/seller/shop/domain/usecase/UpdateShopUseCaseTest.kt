package com.vitran.shop.feature.seller.shop.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopPublicCacheInvalidator
import com.vitran.shop.feature.seller.createSellerRepository
import com.vitran.shop.feature.seller.jsonResponse
import com.vitran.shop.feature.seller.shop.data.state.SellerShopStateStore
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.ShopPublicationState
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.updateShopBody
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class UpdateShopUseCaseTest {

    @Test
    fun update_reapproval_andInvalidatesPublicCache_andRemovesFromActiveFilter() = runTest {
        var invalidated: ShopId? = null
        val store = SellerShopStateStore(mutableListOf())
        store.upsertSummary(
            SellerShopSummary(
                id = ShopId(1),
                title = "Live Shop",
                active = true,
                confirmed = true,
            ),
        )
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, updateShopBody) }
        val (repo, _) = createSellerRepository(engine, stateStore = store)
        val useCase =
            UpdateShopUseCase(
                sellerShopRepository = repo,
                sellerShopStateStore = store,
                publicCacheInvalidator = ShopPublicCacheInvalidator { invalidated = it },
                activeListFilter = { SellerShopFilter.Active },
            )

        val result = useCase(UpdateShopCommand(shopId = ShopId(1), title = "Updated"))

        assertIs<AppResult.Success<*>>(result)
        assertEquals(ShopPublicationState.PendingApproval, (result as AppResult.Success).value.publicationState)
        assertEquals(ShopId(1), invalidated)
        assertTrue(store.summaries.value.none { it.id == ShopId(1) })
    }
}
