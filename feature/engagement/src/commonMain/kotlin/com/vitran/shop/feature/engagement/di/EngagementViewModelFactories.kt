package com.vitran.shop.feature.engagement.di

import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.comment.domain.repository.ShopCommentRepository
import com.vitran.shop.feature.engagement.comment.domain.usecase.SubmitShopCommentUseCase
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntentId
import com.vitran.shop.feature.engagement.contact.domain.usecase.ContactProductUseCase
import com.vitran.shop.feature.engagement.favorite.domain.usecase.SetShopFavoriteUseCase
import com.vitran.shop.feature.engagement.follow.domain.usecase.SetShopFollowedUseCase
import com.vitran.shop.feature.engagement.presentation.ProductContactViewModel
import com.vitran.shop.feature.engagement.presentation.ProductEngagementViewModel
import com.vitran.shop.feature.engagement.presentation.ProductReviewsViewModel
import com.vitran.shop.feature.engagement.presentation.PublicWishlistViewModel
import com.vitran.shop.feature.engagement.presentation.ShopCommentsViewModel
import com.vitran.shop.feature.engagement.presentation.ShopEngagementViewModel
import com.vitran.shop.feature.engagement.review.domain.repository.ProductReviewRepository
import com.vitran.shop.feature.engagement.review.domain.usecase.SubmitProductReviewUseCase
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSlug
import com.vitran.shop.feature.engagement.wishlist.domain.repository.WishlistRepository
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.SetProductSavedUseCase
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

class ProductEngagementViewModelFactory(
    private val setProductSaved: SetProductSavedUseCase,
    private val setShopFollowed: SetShopFollowedUseCase,
    private val stateStore: EngagementStateStore,
    private val sessionRepository: SessionRepository,
    private val analyticsTracker: MarketplaceAnalyticsTracker,
) {
    fun create(productId: Long, shopId: Long?): ProductEngagementViewModel =
        ProductEngagementViewModel(
            productId = ProductId(productId),
            shopId = shopId?.let(::ShopId),
            setProductSaved = setProductSaved,
            setShopFollowed = setShopFollowed,
            stateStore = stateStore,
            sessionRepository = sessionRepository,
            analyticsTracker = analyticsTracker,
        )
}

class ShopEngagementViewModelFactory(
    private val setShopFollowed: SetShopFollowedUseCase,
    private val setShopFavorite: SetShopFavoriteUseCase,
    private val stateStore: EngagementStateStore,
    private val sessionRepository: SessionRepository,
    private val analyticsTracker: MarketplaceAnalyticsTracker,
) {
    fun create(shopId: Long): ShopEngagementViewModel =
        ShopEngagementViewModel(
            shopId = ShopId(shopId),
            setShopFollowed = setShopFollowed,
            setShopFavorite = setShopFavorite,
            stateStore = stateStore,
            sessionRepository = sessionRepository,
            analyticsTracker = analyticsTracker,
        )
}

class ProductReviewsViewModelFactory(
    private val productReviewRepository: ProductReviewRepository,
    private val submitProductReview: SubmitProductReviewUseCase,
    private val sessionRepository: SessionRepository,
) {
    fun create(productId: Long, intentId: Long? = null): ProductReviewsViewModel =
        ProductReviewsViewModel(
            productId = ProductId(productId),
            productReviewRepository = productReviewRepository,
            submitProductReview = submitProductReview,
            sessionRepository = sessionRepository,
            purchaseIntentId = intentId?.let(::PurchaseIntentId),
        )
}

class ProductContactViewModelFactory(
    private val contactProduct: ContactProductUseCase,
) {
    fun create(productId: Long): ProductContactViewModel =
        ProductContactViewModel(ProductId(productId), contactProduct)
}

class PublicWishlistViewModelFactory(
    private val wishlistRepository: WishlistRepository,
) {
    fun create(shareSlug: String): PublicWishlistViewModel =
        PublicWishlistViewModel(WishlistShareSlug(shareSlug), wishlistRepository)
}

class ShopCommentsViewModelFactory(
    private val shopCommentRepository: ShopCommentRepository,
    private val submitShopComment: SubmitShopCommentUseCase,
    private val sessionRepository: SessionRepository,
) {
    fun create(shopId: Long): ShopCommentsViewModel =
        ShopCommentsViewModel(
            shopId = ShopId(shopId),
            shopCommentRepository = shopCommentRepository,
            submitShopComment = submitShopComment,
            sessionRepository = sessionRepository,
        )
}
