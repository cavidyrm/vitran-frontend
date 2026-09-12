package com.vitran.shop.feature.account.di

import com.vitran.shop.feature.account.data.remote.AccountApi
import com.vitran.shop.feature.account.data.remote.ProductMatchApi
import com.vitran.shop.feature.account.data.remote.ProfileApi
import com.vitran.shop.feature.account.data.repository.DefaultAccountRepository
import com.vitran.shop.feature.account.data.repository.DefaultProductMatchRepository
import com.vitran.shop.feature.account.data.repository.DefaultProfileRepository
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.account.domain.repository.ProductMatchRepository
import com.vitran.shop.feature.account.domain.repository.ProfileRepository
import com.vitran.shop.feature.account.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val accountModule = module {
    single { AccountApi(get(), get(), get()) }
    single { ProfileApi(get(), get(), get()) }
    single { ProductMatchApi(get(), get(), get()) }
    single<AccountRepository> { DefaultAccountRepository(get(), get(), get()) }
    single<ProfileRepository> { DefaultProfileRepository(get()) }
    single<ProductMatchRepository> { DefaultProductMatchRepository(get()) }
    viewModel { ProfileViewModel(get()) }
}
