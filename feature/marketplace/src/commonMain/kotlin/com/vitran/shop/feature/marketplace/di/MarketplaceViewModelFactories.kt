package com.vitran.shop.feature.marketplace.di

import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductRepository
import com.vitran.shop.feature.marketplace.product.presentation.ProductDetailsViewModel
import com.vitran.shop.feature.marketplace.product.presentation.ProductListViewModel
import com.vitran.shop.feature.marketplace.product.presentation.ProductSearchViewModel
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopRepository
import com.vitran.shop.feature.marketplace.shop.presentation.ShopBrowseViewModel
import com.vitran.shop.feature.marketplace.shop.presentation.ShopDetailsViewModel
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

class ProductListViewModelFactory(
    private val productRepository: ProductRepository,
) {
    fun create(categorySlug: CategorySlug?): ProductListViewModel =
        ProductListViewModel(productRepository, categorySlug)
}

class ProductSearchViewModelFactory(
    private val productRepository: ProductRepository,
) {
    fun create(initialQuery: String): ProductSearchViewModel =
        ProductSearchViewModel(productRepository, initialQuery)
}

class ProductDetailsViewModelFactory(
    private val productRepository: ProductRepository,
) {
    fun create(productId: Long): ProductDetailsViewModel =
        ProductDetailsViewModel(productRepository, ProductId(productId))
}

class ShopDetailsViewModelFactory(
    private val shopRepository: ShopRepository,
    private val productRepository: ProductRepository,
) {
    fun create(shopNavigationKey: String): ShopDetailsViewModel =
        ShopDetailsViewModel(shopRepository, productRepository, shopNavigationKey)
}

class ShopBrowseViewModelFactory(
    private val shopRepository: ShopRepository,
) {
    fun create(categorySlug: CategorySlug?): ShopBrowseViewModel =
        ShopBrowseViewModel(shopRepository, categorySlug)
}
