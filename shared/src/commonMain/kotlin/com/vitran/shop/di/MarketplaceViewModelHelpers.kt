package com.vitran.shop.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vitran.shop.feature.marketplace.di.ProductDetailsViewModelFactory
import com.vitran.shop.feature.marketplace.di.ProductSearchViewModelFactory
import com.vitran.shop.feature.marketplace.di.ShopDetailsViewModelFactory
import com.vitran.shop.feature.marketplace.product.presentation.ProductDetailsViewModel
import com.vitran.shop.feature.marketplace.product.presentation.ProductSearchViewModel
import com.vitran.shop.feature.marketplace.shop.presentation.ShopDetailsViewModel
import org.koin.compose.koinInject

@Composable
fun rememberProductDetailsViewModel(productId: String): ProductDetailsViewModel {
    val factory: ProductDetailsViewModelFactory = koinInject()
    val id = productId.toLongOrNull() ?: 0L
    return remember(productId) { factory.create(id) }
}

@Composable
fun rememberShopDetailsViewModel(shopNavigationKey: String): ShopDetailsViewModel {
    val factory: ShopDetailsViewModelFactory = koinInject()
    return remember(shopNavigationKey) { factory.create(shopNavigationKey) }
}

@Composable
fun rememberProductSearchViewModel(initialQuery: String): ProductSearchViewModel {
    val factory: ProductSearchViewModelFactory = koinInject()
    return remember(initialQuery) { factory.create(initialQuery) }
}
