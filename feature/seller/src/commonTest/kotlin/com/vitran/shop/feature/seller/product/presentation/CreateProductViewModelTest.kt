package com.vitran.shop.feature.seller.product.presentation

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.product.domain.model.CreateProductCommand
import com.vitran.shop.feature.seller.product.domain.model.ProductImageId
import com.vitran.shop.feature.seller.product.domain.model.ProductPublicationState
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary
import com.vitran.shop.feature.seller.product.domain.model.UpdateProductCommand
import com.vitran.shop.feature.seller.product.domain.query.SellerProductListQuery
import com.vitran.shop.feature.seller.product.domain.repository.SellerProductRepository
import com.vitran.shop.feature.seller.product.domain.usecase.CreateProductUseCase
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class CreateProductViewModelTest {

    @Test
    fun loadShops_selectsFirst() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm =
                CreateProductViewModel(
                    CreateProductUseCase(FakeProductRepo()),
                    FakeShopRepo(
                        listOf(
                            SellerShopSummary(ShopId(9), "فروشگاه من", false, false),
                        ),
                    ),
                    FakePicker(),
                )
            advanceUntilIdle()
            assertEquals(ShopId(9), vm.uiState.value.selectedShopId)
            assertEquals("فروشگاه من", vm.uiState.value.storeName)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun pickImages_cancelLeavesFormUnchanged() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val picker = FakePicker(emptyList())
            val vm =
                CreateProductViewModel(
                    CreateProductUseCase(FakeProductRepo()),
                    FakeShopRepo(listOf(SellerShopSummary(ShopId(1), "S", true, true))),
                    picker,
                )
            advanceUntilIdle()
            var received: List<LocalProductImage>? = null
            vm.pickImages(0) { received = it }
            advanceUntilIdle()
            assertEquals(emptyList(), received)
            assertTrue(vm.uiState.value.localFilesByMediaId.isEmpty())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun pickAndRemoveLocal_noUploadUntilSubmit() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val file = SelectedFile.fromBytes("a.jpg", byteArrayOf(1), "image/jpeg")
            val vm =
                CreateProductViewModel(
                    CreateProductUseCase(FakeProductRepo()),
                    FakeShopRepo(listOf(SellerShopSummary(ShopId(1), "S", true, true))),
                    FakePicker(listOf(file)),
                )
            advanceUntilIdle()
            var locals = emptyList<LocalProductImage>()
            vm.pickImages(0) { locals = it }
            advanceUntilIdle()
            assertEquals(1, locals.size)
            assertEquals(1, vm.uiState.value.localFilesByMediaId.size)
            vm.removeLocalImage(locals.first().id)
            assertTrue(vm.uiState.value.localFilesByMediaId.isEmpty())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun submit_successUsesServerPublicationState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo = FakeProductRepo()
            val vm =
                CreateProductViewModel(
                    CreateProductUseCase(repo),
                    FakeShopRepo(listOf(SellerShopSummary(ShopId(1), "S", true, true))),
                    FakePicker(),
                )
            advanceUntilIdle()
            vm.submit(
                title = "Widget",
                description = "Desc",
                priceText = "1000",
                categoryId = "aa-1",
                orderedMediaIds = emptyList(),
                mode = CreateProductSubmitMode.Publish,
            )
            advanceUntilIdle()
            assertFalse(vm.uiState.value.isSubmitting)
            assertEquals(
                ProductPublicationState.PendingApproval,
                vm.uiState.value.createdProduct?.publicationState,
            )
            assertTrue(repo.lastCreate?.desiredActive == true)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun submit_duplicatePreventedWhileInFlight() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo = FakeProductRepo(delayCreate = true)
            val vm =
                CreateProductViewModel(
                    CreateProductUseCase(repo),
                    FakeShopRepo(listOf(SellerShopSummary(ShopId(1), "S", true, true))),
                    FakePicker(),
                )
            advanceUntilIdle()
            vm.submit("T", "", "10", "1", emptyList(), CreateProductSubmitMode.Draft)
            testScheduler.runCurrent()
            assertTrue(vm.uiState.value.isSubmitting)
            vm.submit("T2", "", "10", "1", emptyList(), CreateProductSubmitMode.Draft)
            assertEquals(1, repo.createCalls)
            repo.completeCreate()
            advanceUntilIdle()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun submit_mapsFieldErrors() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo =
                FakeProductRepo(
                    createResult =
                        AppResult.Failure(
                            AppError.Validation(
                                message = "invalid",
                                fieldErrors =
                                    listOf(
                                        com.vitran.shop.core.domain.error.FieldError(
                                            reason = "title",
                                            messages = listOf("عنوان کوتاه است"),
                                        ),
                                    ),
                            ),
                        ),
                )
            val vm =
                CreateProductViewModel(
                    CreateProductUseCase(repo),
                    FakeShopRepo(listOf(SellerShopSummary(ShopId(1), "S", true, true))),
                    FakePicker(),
                )
            advanceUntilIdle()
            vm.submit("T", "", "10", "1", emptyList(), CreateProductSubmitMode.Draft)
            advanceUntilIdle()
            assertEquals("عنوان کوتاه است", vm.uiState.value.fieldErrors.title)
            assertNull(vm.uiState.value.createdProduct)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

private class FakePicker(
    private val files: List<SelectedFile> = emptyList(),
) : ImagePicker {
    override suspend fun pickImages(maxCount: Int): List<SelectedFile> = files.take(maxCount)
}

private class FakeShopRepo(
    private val shops: List<SellerShopSummary>,
) : SellerShopRepository {
    override suspend fun checkSlugAvailability(
        slug: com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug,
        excludeId: ShopId?,
    ) = AppResult.Failure(AppError.Unexpected())

    override suspend fun createShop(command: com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand) =
        AppResult.Failure(AppError.Unexpected())

    override suspend fun getMyShops(query: SellerShopListQuery) =
        AppResult.Success(CursorPage(shops, null, false))

    override suspend fun getMyShop(shopId: ShopId) = AppResult.Failure(AppError.NotFound())

    override suspend fun updateShop(command: com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand) =
        AppResult.Failure(AppError.Unexpected())

    override suspend fun getFulfillmentOptions(shopId: ShopId) =
        AppResult.Success(emptyList<com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode>())

    override suspend fun regenerateApiKey(shopId: ShopId) =
        AppResult.Failure(AppError.Unexpected())
}

private class FakeProductRepo(
    private val createResult: AppResult<SellerProductDetails>? = null,
    private val delayCreate: Boolean = false,
) : SellerProductRepository {
    var createCalls = 0
    var lastCreate: CreateProductCommand? = null
    private var pending: CompletableDeferred<Unit>? = null

    fun completeCreate() {
        pending?.complete(Unit)
    }

    override suspend fun getProducts(query: SellerProductListQuery) =
        AppResult.Success(CursorPage<SellerProductSummary>(emptyList(), null, false))

    override suspend fun getProduct(productId: ProductId) = AppResult.Failure(AppError.NotFound())

    override suspend fun createProduct(command: CreateProductCommand): AppResult<SellerProductDetails> {
        createCalls += 1
        lastCreate = command
        if (delayCreate) {
            pending = CompletableDeferred()
            pending!!.await()
        }
        return createResult
            ?: AppResult.Success(
                SellerProductDetails(
                    id = ProductId(1),
                    shopId = command.shopId,
                    categorySlug = command.category,
                    title = command.title,
                    description = command.description,
                    priceAmount = command.priceAmount,
                    active = false,
                    confirmed = false,
                    images = emptyList(),
                    createdAt = null,
                    updatedAt = null,
                ),
            )
    }

    override suspend fun updateProduct(command: UpdateProductCommand) =
        AppResult.Failure(AppError.Unexpected())

    override suspend fun setProductActive(productId: ProductId, active: Boolean) =
        AppResult.Failure(AppError.Unexpected())

    override suspend fun deleteProduct(productId: ProductId) = AppResult.Success(Unit)

    override suspend fun deleteProductImage(productId: ProductId, imageId: ProductImageId) =
        AppResult.Failure(AppError.Unexpected())
}
