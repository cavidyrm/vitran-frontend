package com.vitran.shop.feature.marketplace.di

import com.vitran.shop.feature.marketplace.product.data.remote.PublicProductApi
import com.vitran.shop.feature.marketplace.product.data.repository.DefaultProductRepository
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductRepository
import com.vitran.shop.feature.marketplace.shop.data.remote.PublicShopApi
import com.vitran.shop.feature.marketplace.shop.data.repository.DefaultShopRepository
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopRepository
import org.koin.dsl.module

val marketplaceModule = module {
    single { PublicShopApi(get(), get(), get()) }
    single { PublicProductApi(get(), get(), get()) }
    single<ShopRepository> { DefaultShopRepository(get()) }
    single<ProductRepository> { DefaultProductRepository(get()) }

    factory { ProductDetailsViewModelFactory(get()) }
    factory { ShopDetailsViewModelFactory(get(), get()) }
    factory { ProductSearchViewModelFactory(get()) }
    factory { ProductListViewModelFactory(get()) }
    factory { ShopBrowseViewModelFactory(get()) }
}
