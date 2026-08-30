package com.vitran.shop.feature.seller.product.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductPublicCacheInvalidator
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.product.domain.model.CreateProductCommand
import com.vitran.shop.feature.seller.product.domain.model.ProductImageId
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary
import com.vitran.shop.feature.seller.product.domain.model.UpdateProductCommand
import com.vitran.shop.feature.seller.product.domain.query.SellerProductListQuery
import com.vitran.shop.feature.seller.product.domain.repository.SellerProductRepository
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SellerProductUseCaseTest {

    @Test
    fun create_rejectsBlankTitle() = runTest {
        val useCase = CreateProductUseCase(FakeSellerProductRepository())
        val result =
            useCase(
                CreateProductCommand(
                    shopId = ShopId(1),
                    title = "  ",
                    description = "",
                    priceAmount = 1,
                    category = CategorySlug("1"),
                    desiredActive = false,
                ),
            )
        assertIs<AppResult.Failure>(result)
    }

    @Test
    fun setActive_blocksUnconfirmedPublish() = runTest {
        val repo = FakeSellerProductRepository()
        val invalidator = RecordingInvalidator()
        val useCase = SetProductActiveUseCase(repo, invalidator)
        val result = useCase(ProductId(1), active = true, localConfirmed = false)
        assertIs<AppResult.Failure>(result)
        assertEquals(0, repo.setActiveCalls)
        assertTrue(invalidator.ids.isEmpty())
    }

    @Test
    fun update_invalidatesPublicCache() = runTest {
        val repo = FakeSellerProductRepository()
        val invalidator = RecordingInvalidator()
        val useCase = UpdateProductUseCase(repo, invalidator)
        val result = useCase(UpdateProductCommand(productId = ProductId(1), title = "X"))
        assertIs<AppResult.Success<*>>(result)
        assertEquals(listOf(ProductId(1)), invalidator.ids)
    }

    @Test
    fun delete_invalidatesPublicCache() = runTest {
        val repo = FakeSellerProductRepository()
        val invalidator = RecordingInvalidator()
        val useCase = DeleteProductUseCase(repo, invalidator)
        val result = useCase(ProductId(1))
        assertIs<AppResult.Success<*>>(result)
        assertEquals(listOf(ProductId(1)), invalidator.ids)
    }
}

private class RecordingInvalidator : ProductPublicCacheInvalidator {
    val ids = mutableListOf<ProductId>()
    override suspend fun invalidate(productId: ProductId) {
        ids += productId
    }
}

private class FakeSellerProductRepository : SellerProductRepository {
    var setActiveCalls = 0

    override suspend fun getProducts(query: SellerProductListQuery): AppResult<CursorPage<SellerProductSummary>> =
        AppResult.Success(CursorPage(emptyList(), null, false))

    override suspend fun getProduct(productId: ProductId): AppResult<SellerProductDetails> =
        AppResult.Failure(AppError.NotFound())

    override suspend fun createProduct(command: CreateProductCommand): AppResult<SellerProductDetails> =
        AppResult.Success(sampleDetails(command.shopId))

    override suspend fun updateProduct(command: UpdateProductCommand): AppResult<SellerProductDetails> =
        AppResult.Success(sampleDetails(ShopId(1)).copy(id = command.productId, title = command.title.orEmpty()))

    override suspend fun setProductActive(productId: ProductId, active: Boolean): AppResult<SellerProductDetails> {
        setActiveCalls += 1
        return AppResult.Success(sampleDetails(ShopId(1)).copy(id = productId, active = active, confirmed = true))
    }

    override suspend fun deleteProduct(productId: ProductId): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun deleteProductImage(
        productId: ProductId,
        imageId: ProductImageId,
    ): AppResult<SellerProductDetails> = AppResult.Success(sampleDetails(ShopId(1)))

    private fun sampleDetails(shopId: ShopId) =
        SellerProductDetails(
            id = ProductId(1),
            shopId = shopId,
            categorySlug = CategorySlug("1"),
            title = "Sample",
            description = null,
            priceAmount = 1,
            active = false,
            confirmed = false,
            images = emptyList(),
            createdAt = null,
            updatedAt = null,
        )
}
