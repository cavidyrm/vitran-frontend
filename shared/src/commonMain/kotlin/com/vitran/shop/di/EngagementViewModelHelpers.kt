package com.vitran.shop.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vitran.shop.feature.engagement.di.ProductContactViewModelFactory
import com.vitran.shop.feature.engagement.di.ProductEngagementViewModelFactory
import com.vitran.shop.feature.engagement.di.ProductReviewsViewModelFactory
import com.vitran.shop.feature.engagement.di.ShopEngagementViewModelFactory
import com.vitran.shop.feature.engagement.presentation.CatalogEngagementViewModel
import com.vitran.shop.feature.engagement.presentation.ProductContactViewModel
import com.vitran.shop.feature.engagement.presentation.ProductEngagementViewModel
import com.vitran.shop.feature.engagement.presentation.ProductReviewsViewModel
import com.vitran.shop.feature.engagement.presentation.ShopEngagementViewModel
import org.koin.compose.koinInject

@Composable
fun rememberProductEngagementViewModel(
    productId: String,
    shopId: String?,
): ProductEngagementViewModel {
    val factory: ProductEngagementViewModelFactory = koinInject()
    val productKey = productId.toLongOrNull() ?: 0L
    val shopKey = shopId?.toLongOrNull()
    return remember(productId, shopId) { factory.create(productKey, shopKey) }
}

@Composable
fun rememberShopEngagementViewModel(shopId: Long): ShopEngagementViewModel {
    val factory: ShopEngagementViewModelFactory = koinInject()
    return remember(shopId) { factory.create(shopId) }
}

@Composable
fun rememberProductReviewsViewModel(productId: String): ProductReviewsViewModel {
    val factory: ProductReviewsViewModelFactory = koinInject()
    val id = productId.toLongOrNull() ?: 0L
    return remember(productId) { factory.create(id) }
}

@Composable
fun rememberProductContactViewModel(productId: String): ProductContactViewModel {
    val factory: ProductContactViewModelFactory = koinInject()
    val id = productId.toLongOrNull() ?: 0L
    return remember(productId) { factory.create(id) }
}

@Composable
fun rememberCatalogEngagementViewModel(): CatalogEngagementViewModel = vitranKoinViewModel()
