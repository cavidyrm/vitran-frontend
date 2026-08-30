package com.vitran.shop.feature.seller.di

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.shop.data.remote.SellerShopApi
import com.vitran.shop.feature.seller.shop.data.repository.DefaultSellerShopRepository
import com.vitran.shop.feature.seller.shop.data.state.SellerShopStateStore
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.shop.domain.usecase.CreateShopUseCase
import com.vitran.shop.feature.seller.shop.domain.usecase.UpdateShopUseCase
import com.vitran.shop.feature.seller.shop.presentation.CreateShopViewModel
import com.vitran.shop.feature.seller.shop.presentation.EditShopViewModel
import com.vitran.shop.feature.seller.shop.presentation.SellerShopDetailsViewModel
import com.vitran.shop.feature.seller.shop.presentation.SellerShopsViewModel
import com.vitran.shop.feature.seller.shop.presentation.ShopApiKeyViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sellerModule = module {
    single { SellerShopStateStore(invalidationListeners = get()) }
    single { SellerShopApi(get(), get(), get()) }
    single<SellerShopRepository> { DefaultSellerShopRepository(get(), get()) }

    factory { CreateShopUseCase(get(), get(), get()) }
    factory {
        UpdateShopUseCase(
            sellerShopRepository = get(),
            sellerShopStateStore = get(),
            publicCacheInvalidator = get(),
        )
    }

    viewModel { CreateShopViewModel(get(), get()) }
    viewModel { SellerShopsViewModel(get()) }

    factory { EditShopViewModelFactory(get(), get()) }
    factory { SellerShopDetailsViewModelFactory(get()) }
    factory { ShopApiKeyViewModelFactory(get()) }
}

class EditShopViewModelFactory(
    private val sellerShopRepository: SellerShopRepository,
    private val updateShopUseCase: UpdateShopUseCase,
) {
    fun create(shopId: Long): EditShopViewModel =
        EditShopViewModel(ShopId(shopId), sellerShopRepository, updateShopUseCase)
}

class SellerShopDetailsViewModelFactory(
    private val sellerShopRepository: SellerShopRepository,
) {
    fun create(shopId: Long): SellerShopDetailsViewModel =
        SellerShopDetailsViewModel(ShopId(shopId), sellerShopRepository)
}

class ShopApiKeyViewModelFactory(
    private val sellerShopRepository: SellerShopRepository,
) {
    fun create(shopId: Long): ShopApiKeyViewModel =
        ShopApiKeyViewModel(ShopId(shopId), sellerShopRepository)
}
