package com.vitran.shop.feature.engagement.presentation

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.FakeSessionRepository
import com.vitran.shop.feature.engagement.RecordingAnalyticsTracker
import com.vitran.shop.feature.engagement.comment.domain.model.PublicShopComment
import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.engagement.comment.domain.model.SubmitShopCommentCommand
import com.vitran.shop.feature.engagement.comment.domain.model.SubmittedShopComment
import com.vitran.shop.feature.engagement.comment.domain.repository.ShopCommentRepository
import com.vitran.shop.feature.engagement.comment.domain.usecase.SubmitShopCommentUseCase
import com.vitran.shop.feature.engagement.contact.domain.model.ContactProductResult
import com.vitran.shop.feature.engagement.contact.domain.model.ContactRoute
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntent
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntentId
import com.vitran.shop.feature.engagement.contact.domain.repository.ProductContactRepository
import com.vitran.shop.feature.engagement.contact.domain.usecase.ContactProductUseCase
import com.vitran.shop.feature.engagement.follow.domain.repository.FollowRepository
import com.vitran.shop.feature.engagement.follow.domain.usecase.SetShopFollowedUseCase
import com.vitran.shop.feature.engagement.review.domain.model.ProductReview
import com.vitran.shop.feature.engagement.review.domain.model.ProductReviewId
import com.vitran.shop.feature.engagement.review.domain.model.Rating
import com.vitran.shop.feature.engagement.review.domain.model.SubmitReviewCommand
import com.vitran.shop.feature.engagement.review.domain.model.SubmittedProductReview
import com.vitran.shop.feature.engagement.review.domain.repository.ProductReviewRepository
import com.vitran.shop.feature.engagement.review.domain.usecase.SubmitProductReviewUseCase
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.SaveStatus
import com.vitran.shop.feature.engagement.wishlist.domain.repository.WishlistRepository
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.SetProductSavedUseCase
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EngagementViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun anonymousSave_requestsLogin_andDoesNotCallApi() = runTest {
        var apiCalls = 0
        val store = EngagementStateStore(mutableListOf())
        val viewModel = ProductEngagementViewModel(
            productId = ProductId(1),
            shopId = ShopId(1),
            setProductSaved = SetProductSavedUseCase(
                wishlistRepository = object : WishlistRepository by UnsupportedWishlistRepository() {
                    override suspend fun setSaved(productId: ProductId, saved: Boolean): AppResult<Unit> {
                        apiCalls += 1
                        return AppResult.Success(Unit)
                    }
                },
                stateStore = store,
                analyticsTracker = RecordingAnalyticsTracker(),
            ),
            setShopFollowed = unusedFollowUseCase(),
            stateStore = store,
            sessionRepository = FakeSessionRepository(initiallyAuthenticated = false),
            analyticsTracker = RecordingAnalyticsTracker(),
        )
        viewModel.onSaveClick()
        advanceUntilIdle()

        assertEquals(0, apiCalls)
        assertEquals(SaveStatus.Unknown, store.saveStatus(ProductId(1)))
    }

    @Test
    fun save_rollback_restoresPreviousStatus() = runTest {
        val store = EngagementStateStore(mutableListOf())
        store.setSaveStatus(ProductId(1), SaveStatus.NotSaved)
        val viewModel = ProductEngagementViewModel(
            productId = ProductId(1),
            shopId = ShopId(1),
            setProductSaved = SetProductSavedUseCase(
                wishlistRepository = object : WishlistRepository by UnsupportedWishlistRepository() {
                    override suspend fun setSaved(productId: ProductId, saved: Boolean): AppResult<Unit> =
                        AppResult.Failure(AppError.Network.ServerUnavailable(message = "down"))
                },
                stateStore = store,
                analyticsTracker = RecordingAnalyticsTracker(),
            ),
            setShopFollowed = unusedFollowUseCase(),
            stateStore = store,
            sessionRepository = FakeSessionRepository(initiallyAuthenticated = true),
            analyticsTracker = RecordingAnalyticsTracker(),
        )

        viewModel.onSaveClick()
        advanceUntilIdle()

        assertEquals(SaveStatus.NotSaved, store.saveStatus(ProductId(1)))
    }

    @Test
    fun reviewValidation_rejectsBadRating() = runTest {
        val viewModel = ProductReviewsViewModel(
            productId = ProductId(1),
            productReviewRepository = object : ProductReviewRepository {
                override suspend fun getReviews(
                    productId: ProductId,
                    pagination: CursorPagination,
                ) = AppResult.Success(CursorPage<ProductReview>(emptyList(), nextCursor = null, hasMore = false))

                override suspend fun submitReview(command: SubmitReviewCommand) =
                    error("should not submit")
            },
            submitProductReview = SubmitProductReviewUseCase(
                object : ProductReviewRepository {
                    override suspend fun getReviews(
                        productId: ProductId,
                        pagination: CursorPagination,
                    ) = error("unused")

                    override suspend fun submitReview(command: SubmitReviewCommand) =
                        AppResult.Success(
                            SubmittedProductReview(
                                id = ProductReviewId(1),
                                productId = command.productId,
                                rating = command.rating,
                                comment = command.comment,
                            ),
                        )
                },
            ),
            sessionRepository = FakeSessionRepository(initiallyAuthenticated = true),
        )
        advanceUntilIdle()

        viewModel.submit(ratingValue = 0, comment = "ok")
        advanceUntilIdle()
        assertEquals("Rating must be between 1 and 5", viewModel.uiState.value.validationMessage)

        viewModel.submit(ratingValue = 5, comment = "Great")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.submitted || viewModel.uiState.value.validationMessage == null)
    }

    @Test
    fun commentSubmit_doesNotAppendUnconfirmedToPublicList() = runTest {
        val viewModel = ShopCommentsViewModel(
            shopId = ShopId(1),
            shopCommentRepository = object : ShopCommentRepository {
                override suspend fun getComments(shopId: ShopId, pagination: CursorPagination) =
                    AppResult.Success(
                        CursorPage(
                            items = listOf(
                                PublicShopComment(ShopCommentId(1), title = "Great shop", confirmed = true),
                            ),
                            nextCursor = null,
                            hasMore = false,
                        ),
                    )

                override suspend fun submitComment(command: SubmitShopCommentCommand) =
                    AppResult.Success(
                        SubmittedShopComment(
                            id = ShopCommentId(99),
                            shopId = command.shopId,
                            authorUserId = 2,
                            title = command.title,
                            description = command.description,
                            confirmed = false,
                            createdAt = Instant.parse("2026-06-09T12:00:00Z"),
                        ),
                    )
            },
            submitShopComment = SubmitShopCommentUseCase(
                object : ShopCommentRepository {
                    override suspend fun getComments(shopId: ShopId, pagination: CursorPagination) =
                        error("unused")

                    override suspend fun submitComment(command: SubmitShopCommentCommand) =
                        AppResult.Success(
                            SubmittedShopComment(
                                id = ShopCommentId(99),
                                shopId = command.shopId,
                                authorUserId = 2,
                                title = command.title,
                                description = command.description,
                                confirmed = false,
                                createdAt = Instant.parse("2026-06-09T12:00:00Z"),
                            ),
                        )
                },
            ),
            sessionRepository = FakeSessionRepository(initiallyAuthenticated = true),
        )
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.list.items.size)

        viewModel.submit("Great shop", "Fast delivery and friendly staff.")
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.list.items.size)
        assertEquals(99L, viewModel.uiState.value.pendingModeration?.id?.value)
        assertEquals(false, viewModel.uiState.value.pendingModeration?.confirmed)
        assertFalse(viewModel.uiState.value.list.items.any { it.id.value == 99L })
    }

    @Test
    fun contact_duplicateTap_sendsOnce() = runTest {
        var calls = 0
        val viewModel = ProductContactViewModel(
            productId = ProductId(1),
            contactProduct = ContactProductUseCase(
                productContactRepository = object : ProductContactRepository {
                    override suspend fun contactProduct(productId: ProductId): AppResult<ContactProductResult> {
                        calls += 1
                        return AppResult.Success(
                            ContactProductResult(
                                route = ContactRoute.WhatsApp("https://wa.me/989123456789"),
                                intent = PurchaseIntent(
                                    id = PurchaseIntentId(1),
                                    productId = productId,
                                    shopId = ShopId(1),
                                    route = ContactRoute.WhatsApp("https://wa.me/989123456789"),
                                ),
                            ),
                        )
                    }
                },
                analyticsTracker = RecordingAnalyticsTracker(),
            ),
        )

        viewModel.contact()
        viewModel.contact()
        advanceUntilIdle()

        assertEquals(1, calls)
        assertIs<ProductContactUiState.RouteReady>(viewModel.uiState.value)
    }

    @Test
    fun catalogSave_ignoresInFlightDuplicate() = runTest {
        var calls = 0
        val session = FakeSessionRepository(initiallyAuthenticated = true)
        val viewModel = CatalogEngagementViewModel(
            setProductSaved = SetProductSavedUseCase(
                wishlistRepository = object : WishlistRepository by UnsupportedWishlistRepository() {
                    override suspend fun setSaved(productId: ProductId, saved: Boolean): AppResult<Unit> {
                        calls += 1
                        return AppResult.Success(Unit)
                    }
                },
                stateStore = EngagementStateStore(mutableListOf()),
                analyticsTracker = RecordingAnalyticsTracker(),
            ),
            stateStore = EngagementStateStore(mutableListOf()),
            sessionRepository = session,
        )

        viewModel.onSaveClick(1)
        viewModel.onSaveClick(1)
        advanceUntilIdle()

        assertEquals(1, calls)
    }

    private fun unusedFollowUseCase() = SetShopFollowedUseCase(
        followRepository = object : FollowRepository {
            override suspend fun setFollowed(shopId: ShopId, followed: Boolean) = AppResult.Success(Unit)
        },
        stateStore = EngagementStateStore(mutableListOf()),
        analyticsTracker = RecordingAnalyticsTracker(),
    )
}

private open class UnsupportedWishlistRepository : WishlistRepository {
    override suspend fun getWishlist(pagination: CursorPagination) = error("unused")
    override suspend fun setSaved(productId: ProductId, saved: Boolean) = error("unused")
    override suspend fun getShareSettings() = error("unused")
    override suspend fun updateShareVisibility(isPublic: Boolean) = error("unused")
    override suspend fun getPublicWishlist(
        shareSlug: com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSlug,
        pagination: CursorPagination,
    ) = error("unused")
}
