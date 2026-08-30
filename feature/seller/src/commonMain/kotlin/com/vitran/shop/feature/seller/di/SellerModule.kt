package com.vitran.shop.feature.seller.di

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.data.remote.PlanApi
import com.vitran.shop.feature.seller.plan.data.repository.DefaultPlanRepository
import com.vitran.shop.feature.seller.plan.domain.repository.PlanRepository
import com.vitran.shop.feature.seller.product.data.remote.SellerProductApi
import com.vitran.shop.feature.seller.product.data.repository.DefaultSellerProductRepository
import com.vitran.shop.feature.seller.product.data.state.SellerProductStateStore
import com.vitran.shop.feature.seller.product.domain.repository.SellerProductRepository
import com.vitran.shop.feature.seller.product.domain.usecase.CreateProductUseCase
import com.vitran.shop.feature.seller.product.domain.usecase.DeleteProductUseCase
import com.vitran.shop.feature.seller.product.domain.usecase.SetProductActiveUseCase
import com.vitran.shop.feature.seller.product.domain.usecase.UpdateProductUseCase
import com.vitran.shop.feature.seller.product.presentation.CreateProductViewModel
import com.vitran.shop.feature.seller.referral.data.remote.ReferralApi
import com.vitran.shop.feature.seller.referral.data.repository.DefaultReferralRepository
import com.vitran.shop.feature.seller.referral.data.state.ReferralStateStore
import com.vitran.shop.feature.seller.referral.domain.repository.ReferralRepository
import com.vitran.shop.feature.seller.referral.domain.usecase.ApplyReferralCreditUseCase
import com.vitran.shop.feature.seller.referral.presentation.ReferralsViewModel
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
import com.vitran.shop.feature.seller.subscription.data.remote.SellerSubscriptionApi
import com.vitran.shop.feature.seller.subscription.data.repository.DefaultSubscriptionRepository
import com.vitran.shop.feature.seller.subscription.data.state.SubscriptionStateStore
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository
import com.vitran.shop.feature.seller.subscription.domain.usecase.GetShopEntitlementsUseCase
import com.vitran.shop.feature.seller.subscription.domain.usecase.PurchasePlanUseCase
import com.vitran.shop.feature.seller.subscription.domain.usecase.VerifyPendingPaymentUseCase
import com.vitran.shop.feature.seller.subscription.presentation.StorePlanUpgradeViewModel
import com.vitran.shop.feature.seller.subscription.presentation.StorePlanViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sellerModule = module {
    single { SellerShopStateStore(invalidationListeners = get()) }
    single { SellerShopApi(get(), get(), get()) }
    single<SellerShopRepository> { DefaultSellerShopRepository(get(), get()) }

    single { SellerProductStateStore(invalidationListeners = get()) }
    single { SellerProductApi(get(), get(), get()) }
    single<SellerProductRepository> { DefaultSellerProductRepository(get(), get()) }

    // Phase 9 — Plans (public cache, not user-scoped)
    single { PlanApi(get(), get(), get()) }
    single<PlanRepository> { DefaultPlanRepository(get()) }

    // Phase 9 — Subscriptions (user-scoped)
    single { SubscriptionStateStore(invalidationListeners = get()) }
    single { SellerSubscriptionApi(get(), get(), get()) }
    single<SubscriptionRepository> { DefaultSubscriptionRepository(get(), get()) }

    // Phase 9 — Referrals (user-scoped)
    single { ReferralStateStore(invalidationListeners = get()) }
    single { ReferralApi(get(), get(), get()) }
    single<ReferralRepository> { DefaultReferralRepository(get(), get()) }

    factory { CreateShopUseCase(get(), get(), get()) }
    factory {
        UpdateShopUseCase(
            sellerShopRepository = get(),
            sellerShopStateStore = get(),
            publicCacheInvalidator = get(),
        )
    }
    factory { CreateProductUseCase(get()) }
    factory {
        UpdateProductUseCase(
            sellerProductRepository = get(),
            publicCacheInvalidator = get(),
        )
    }
    factory {
        SetProductActiveUseCase(
            sellerProductRepository = get(),
            publicCacheInvalidator = get(),
        )
    }
    factory {
        DeleteProductUseCase(
            sellerProductRepository = get(),
            publicCacheInvalidator = get(),
        )
    }

    factory { GetShopEntitlementsUseCase(get(), get()) }
    factory { PurchasePlanUseCase(get(), get()) }
    factory { VerifyPendingPaymentUseCase(get()) }
    factory {
        ApplyReferralCreditUseCase(
            referralRepository = get(),
            subscriptionRepository = get(),
            shopPublicCacheInvalidator = get(),
        )
    }

    viewModel { CreateShopViewModel(get(), get()) }
    viewModel { SellerShopsViewModel(get()) }
    viewModel { CreateProductViewModel(get(), get(), get(), get()) }
    viewModel { StorePlanViewModel(get(), get(), get()) }
    viewModel {
        StorePlanUpgradeViewModel(
            sellerShopRepository = get(),
            planRepository = get(),
            subscriptionRepository = get(),
            purchasePlanUseCase = get(),
            verifyPendingPaymentUseCase = get(),
            referralRepository = get(),
            shopPublicCacheInvalidator = get(),
        )
    }
    viewModel {
        ReferralsViewModel(
            referralRepository = get(),
            applyReferralCreditUseCase = get(),
            sellerShopRepository = get(),
            subscriptionRepository = get(),
        )
    }

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
