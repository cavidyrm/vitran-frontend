package com.vitran.shop.feature.taxonomy.di

import com.vitran.shop.feature.taxonomy.data.remote.TaxonomyApi
import com.vitran.shop.feature.taxonomy.data.repository.DefaultTaxonomyRepository
import com.vitran.shop.feature.taxonomy.domain.repository.TaxonomyRepository
import com.vitran.shop.feature.taxonomy.presentation.CategoriesBrowseViewModel
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val taxonomyModule = module {
    single { TaxonomyApi(get(), get(), get()) }
    single<TaxonomyRepository> { DefaultTaxonomyRepository(get(), get()) }
    viewModel { TaxonomyPickerViewModel(get()) }
    viewModel { CategoriesBrowseViewModel(get()) }
}
