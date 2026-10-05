package com.vitran.shop.feature.seller.product.presentation

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.product.domain.model.CreateProductCommand
import com.vitran.shop.feature.seller.product.domain.model.ProductImageId
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary
import com.vitran.shop.feature.seller.product.domain.model.UpdateProductCommand
import com.vitran.shop.feature.seller.product.domain.query.SellerProductActiveFilter
import com.vitran.shop.feature.seller.product.domain.query.SellerProductListQuery
import com.vitran.shop.feature.seller.product.domain.repository.SellerProductRepository
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SellerShopProductsViewModelTest {

    @Test
    fun initialLoad_scopesShopAndPageSize() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo = FakeListRepo(page(nextCursor = null, hasMore = false))
            val vm = SellerShopProductsViewModel(ShopId(1), repo)
            advanceUntilIdle()
            val query = repo.queries.single()
            assertEquals(ShopId(1), query.shopId)
            assertEquals(SellerProductActiveFilter.All, query.activeFilter)
            assertNull(query.categorySlug)
            assertNull(query.confirmed)
            assertEquals(CursorPagination.DEFAULT_PER_PAGE, query.pagination.perPage)
            assertNull(query.pagination.cursor)
            assertEquals(1, vm.uiState.value.list.items.size)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun filters_restartFromFirstPage() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo = FakeListRepo(page(nextCursor = "c2", hasMore = true))
            val vm = SellerShopProductsViewModel(ShopId(7), repo)
            advanceUntilIdle()
            vm.setActiveFilter(SellerProductActiveFilter.Inactive)
            advanceUntilIdle()
            vm.setCategorySlug(CategorySlug("aa-1-2-3-4"))
            advanceUntilIdle()
            val latest = repo.queries.last()
            assertEquals(ShopId(7), latest.shopId)
            assertEquals(SellerProductActiveFilter.Inactive, latest.activeFilter)
            assertEquals(CategorySlug("aa-1-2-3-4"), latest.categorySlug)
            assertNull(latest.pagination.cursor)
            assertEquals(20, latest.pagination.perPage)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun loadNextPage_keepsFiltersAndCursor() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo =
                FakeListRepo(
                    firstPage = page(nextCursor = "c2", hasMore = true),
                    nextPage = page(nextCursor = null, hasMore = false, id = 2),
                )
            val vm = SellerShopProductsViewModel(ShopId(1), repo)
            advanceUntilIdle()
            vm.setActiveFilter(SellerProductActiveFilter.Active)
            advanceUntilIdle()
            vm.setCategorySlug(CategorySlug("aa-1-2-3-4"))
            advanceUntilIdle()
            vm.loadNextPage()
            advanceUntilIdle()
            val latest = repo.queries.last()
            assertEquals("c2", latest.pagination.cursor)
            assertEquals(20, latest.pagination.perPage)
            assertEquals(SellerProductActiveFilter.Active, latest.activeFilter)
            assertEquals(CategorySlug("aa-1-2-3-4"), latest.categorySlug)
            assertEquals(ShopId(1), latest.shopId)
            assertEquals(2, vm.uiState.value.list.items.size)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

private class FakeListRepo(
    private val firstPage: CursorPage<SellerProductSummary>,
    private val nextPage: CursorPage<SellerProductSummary> = firstPage,
) : SellerProductRepository {
    val queries = mutableListOf<SellerProductListQuery>()

    override suspend fun getProducts(query: SellerProductListQuery): AppResult<CursorPage<SellerProductSummary>> {
        queries += query
        val page = if (query.pagination.cursor == null) firstPage else nextPage
        return AppResult.Success(page)
    }

    override suspend fun getProduct(productId: ProductId) = AppResult.Failure(AppError.NotFound())

    override suspend fun createProduct(command: CreateProductCommand) =
        AppResult.Failure(AppError.Unexpected())

    override suspend fun updateProduct(command: UpdateProductCommand) =
        AppResult.Failure(AppError.Unexpected())

    override suspend fun setProductActive(productId: ProductId, active: Boolean) =
        AppResult.Failure(AppError.Unexpected())

    override suspend fun deleteProduct(productId: ProductId) = AppResult.Success(Unit)

    override suspend fun deleteProductImage(productId: ProductId, imageId: ProductImageId) =
        AppResult.Failure(AppError.Unexpected())
}

private fun page(
    nextCursor: String?,
    hasMore: Boolean,
    id: Long = 1,
) = CursorPage(
    items = listOf(
        SellerProductSummary(
            id = ProductId(id),
            shopId = ShopId(1),
            title = "ویجت",
            active = false,
            confirmed = false,
        ),
    ),
    nextCursor = nextCursor,
    hasMore = hasMore,
)
