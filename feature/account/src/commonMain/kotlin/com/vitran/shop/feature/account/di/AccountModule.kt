package com.vitran.shop.feature.account.di

import com.vitran.shop.feature.account.data.remote.AccountApi
import com.vitran.shop.feature.account.data.repository.DefaultAccountRepository
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.account.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val accountModule = module {
    single { AccountApi(get(), get(), get()) }
    single<AccountRepository> { DefaultAccountRepository(get(), get(), get()) }
    viewModel { ProfileViewModel(get()) }
}
