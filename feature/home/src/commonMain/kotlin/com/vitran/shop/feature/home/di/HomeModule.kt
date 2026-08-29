package com.vitran.shop.feature.home.di

import com.vitran.shop.feature.home.data.remote.HomeApi
import com.vitran.shop.feature.home.data.repository.DefaultHomeRepository
import com.vitran.shop.feature.home.domain.repository.HomeRepository
import com.vitran.shop.feature.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    single { HomeApi(get(), get(), get()) }
    single<HomeRepository> { DefaultHomeRepository(get()) }
    viewModel { HomeViewModel(get(), get(), cityId = null) }
}
