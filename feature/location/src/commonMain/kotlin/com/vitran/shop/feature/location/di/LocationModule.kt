package com.vitran.shop.feature.location.di

import com.vitran.shop.feature.location.data.remote.LocationApi
import com.vitran.shop.feature.location.data.repository.DefaultLocationRepository
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import com.vitran.shop.feature.location.presentation.CreateStoreLocationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val locationModule = module {
    single { LocationApi(get(), get(), get()) }
    single<LocationRepository> { DefaultLocationRepository(get()) }
    viewModel { CreateStoreLocationViewModel(get()) }
}
