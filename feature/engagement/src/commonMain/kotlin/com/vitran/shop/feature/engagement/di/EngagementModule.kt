package com.vitran.shop.feature.engagement.di

import com.vitran.shop.feature.engagement.analytics.data.DefaultMarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.data.remote.ShopAnalyticsApi
import com.vitran.shop.feature.engagement.analytics.data.remote.UserEventApi
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.comment.data.remote.ShopCommentApi
import com.vitran.shop.feature.engagement.comment.data.repository.DefaultShopCommentRepository
import com.vitran.shop.feature.engagement.comment.domain.repository.ShopCommentRepository
import com.vitran.shop.feature.engagement.comment.domain.usecase.SubmitShopCommentUseCase
import com.vitran.shop.feature.engagement.contact.data.remote.ProductContactApi
import com.vitran.shop.feature.engagement.contact.data.repository.DefaultProductContactRepository
import com.vitran.shop.feature.engagement.contact.domain.repository.ProductContactRepository
import com.vitran.shop.feature.engagement.contact.domain.usecase.ContactProductUseCase
import com.vitran.shop.feature.engagement.data.remote.EngagementApi
import com.vitran.shop.feature.engagement.favorite.data.repository.DefaultShopFavoriteRepository
import com.vitran.shop.feature.engagement.favorite.domain.repository.ShopFavoriteRepository
import com.vitran.shop.feature.engagement.favorite.domain.usecase.SetShopFavoriteUseCase
import com.vitran.shop.feature.engagement.follow.data.repository.DefaultFollowRepository
import com.vitran.shop.feature.engagement.follow.domain.repository.FollowRepository
import com.vitran.shop.feature.engagement.follow.domain.usecase.SetShopFollowedUseCase
import com.vitran.shop.feature.engagement.presentation.CatalogEngagementViewModel
import com.vitran.shop.feature.engagement.presentation.FavoriteShopsViewModel
import com.vitran.shop.feature.engagement.presentation.WishlistViewModel
import com.vitran.shop.feature.engagement.review.data.remote.ProductReviewApi
import com.vitran.shop.feature.engagement.review.data.repository.DefaultProductReviewRepository
import com.vitran.shop.feature.engagement.review.domain.repository.ProductReviewRepository
import com.vitran.shop.feature.engagement.review.domain.usecase.SubmitProductReviewUseCase
import com.vitran.shop.feature.engagement.session.DefaultVisitorSessionProvider
import com.vitran.shop.feature.engagement.session.VisitorSessionProvider
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.wishlist.data.repository.DefaultWishlistRepository
import com.vitran.shop.feature.engagement.wishlist.domain.repository.WishlistRepository
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.SetProductSavedUseCase
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.UpdateWishlistSharingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val engagementModule = module {
    single<VisitorSessionProvider> { DefaultVisitorSessionProvider() }
    single { EngagementStateStore(invalidationListeners = get()) }

    single { EngagementApi(get(), get(), get()) }
    single { ProductReviewApi(get(), get(), get()) }
    single { ShopCommentApi(get(), get(), get()) }
    single { ProductContactApi(get(), get(), get()) }
    single { UserEventApi(get(), get(), get()) }
    single { ShopAnalyticsApi(get(), get(), get()) }

    single<FollowRepository> { DefaultFollowRepository(get()) }
    single<ShopFavoriteRepository> { DefaultShopFavoriteRepository(get()) }
    single<WishlistRepository> { DefaultWishlistRepository(get(), get()) }
    single<ProductReviewRepository> { DefaultProductReviewRepository(get()) }
    single<ShopCommentRepository> { DefaultShopCommentRepository(get()) }
    single<ProductContactRepository> { DefaultProductContactRepository(get(), get()) }

    single(named(EngagementQualifiers.AnalyticsScope)) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    single<MarketplaceAnalyticsTracker> {
        DefaultMarketplaceAnalyticsTracker(
            userEventApi = get(),
            shopAnalyticsApi = get(),
            visitorSessionProvider = get(),
            logger = get(),
            scope = get(named(EngagementQualifiers.AnalyticsScope)),
        )
    }

    factory { SetShopFollowedUseCase(get(), get(), get()) }
    factory { SetShopFavoriteUseCase(get(), get()) }
    factory { SetProductSavedUseCase(get(), get(), get()) }
    factory { UpdateWishlistSharingUseCase(get(), get()) }
    factory { SubmitProductReviewUseCase(get()) }
    factory { SubmitShopCommentUseCase(get()) }
    factory { ContactProductUseCase(get(), get()) }

    factory { ProductEngagementViewModelFactory(get(), get(), get(), get(), get()) }
    factory { ShopEngagementViewModelFactory(get(), get(), get(), get(), get()) }
    factory { ProductReviewsViewModelFactory(get(), get(), get()) }
    factory { ProductContactViewModelFactory(get()) }
    factory { PublicWishlistViewModelFactory(get()) }
    factory { ShopCommentsViewModelFactory(get(), get(), get()) }

    viewModel { CatalogEngagementViewModel(get(), get(), get()) }
    viewModel { WishlistViewModel(get(), get(), get(), get(), get()) }
    viewModel { FavoriteShopsViewModel(get(), get(), get(), get()) }
}

object EngagementQualifiers {
    const val AnalyticsScope: String = "engagementAnalyticsScope"
}
