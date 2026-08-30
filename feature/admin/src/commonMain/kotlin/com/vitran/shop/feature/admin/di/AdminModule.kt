package com.vitran.shop.feature.admin.di

import com.vitran.shop.feature.admin.catalog.location.data.remote.AdminLocationApi
import com.vitran.shop.feature.admin.catalog.location.data.repository.DefaultAdminLocationRepository
import com.vitran.shop.feature.admin.catalog.location.domain.AdminLocationRepository
import com.vitran.shop.feature.admin.catalog.location.presentation.AdminCitiesViewModel
import com.vitran.shop.feature.admin.catalog.location.presentation.AdminCityCreateViewModel
import com.vitran.shop.feature.admin.catalog.location.presentation.AdminCityDetailViewModel
import com.vitran.shop.feature.admin.catalog.taxonomy.data.remote.AdminTaxonomyApi
import com.vitran.shop.feature.admin.catalog.taxonomy.data.repository.DefaultAdminTaxonomyRepository
import com.vitran.shop.feature.admin.catalog.taxonomy.domain.AdminTaxonomyRepository
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.AttributeNameEditViewModel
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.CategoryEditViewModel
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.TaxonomyImportViewModel
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.ValueNameEditViewModel
import com.vitran.shop.feature.admin.content.data.AdminContentApi
import com.vitran.shop.feature.admin.content.data.DefaultAdminContentRepository
import com.vitran.shop.feature.admin.content.domain.AdminContentRepository
import com.vitran.shop.feature.admin.content.presentation.AdminStaticPageEditorViewModel
import com.vitran.shop.feature.admin.content.presentation.AdminStaticPagesViewModel
import com.vitran.shop.feature.admin.moderation.data.AdminModerationApi
import com.vitran.shop.feature.admin.moderation.data.DefaultAdminModerationRepository
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationRepository
import com.vitran.shop.feature.admin.moderation.presentation.AdminCommentsViewModel
import com.vitran.shop.feature.admin.moderation.presentation.AdminProductDetailsViewModel
import com.vitran.shop.feature.admin.moderation.presentation.AdminProductsViewModel
import com.vitran.shop.feature.admin.moderation.presentation.AdminShopsViewModel
import com.vitran.shop.feature.admin.plans.data.AdminPlanApi
import com.vitran.shop.feature.admin.plans.data.DefaultAdminPlanRepository
import com.vitran.shop.feature.admin.plans.domain.AdminPlanRepository
import com.vitran.shop.feature.admin.plans.presentation.AdminPlansViewModel
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import com.vitran.shop.feature.admin.state.AdminSessionStateStore
import com.vitran.shop.feature.admin.users.data.remote.AdminUserApi
import com.vitran.shop.feature.admin.users.data.repository.DefaultAdminUserRepository
import com.vitran.shop.feature.admin.users.domain.repository.AdminUserRepository
import com.vitran.shop.feature.admin.users.presentation.AdminUserDetailViewModel
import com.vitran.shop.feature.admin.users.presentation.AdminUsersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val adminModule = module {
    single { AdminPermissions() }
    single { AdminSessionStateStore(invalidationListeners = get()) }
    single { AdminLocationApi(get(), get(), get()) }
    single<AdminLocationRepository> { DefaultAdminLocationRepository(get(), get()) }
    single { AdminTaxonomyApi(get(), get(), get()) }
    single<AdminTaxonomyRepository> { DefaultAdminTaxonomyRepository(get(), get()) }
    single { AdminUserApi(get(), get(), get()) }
    single<AdminUserRepository> { DefaultAdminUserRepository(get()) }
    single { AdminModerationApi(get(), get(), get()) }
    single<AdminModerationRepository> { DefaultAdminModerationRepository(get(), get(), get()) }
    single { AdminPlanApi(get(), get(), get()) }
    single<AdminPlanRepository> { DefaultAdminPlanRepository(get(), get()) }
    single { AdminContentApi(get(), get(), get()) }
    single<AdminContentRepository> { DefaultAdminContentRepository(get(), get()) }

    viewModel { AdminUsersViewModel(get(), get(), get()) }
    viewModel { AdminCitiesViewModel(get(), get()) }
    viewModel { AdminCityCreateViewModel(get(), get()) }
    viewModel { parameters ->
        AdminCityDetailViewModel(parameters.get(), get(), get(), get(), get(), get())
    }
    viewModel { TaxonomyImportViewModel(get(), get(), get(), get()) }
    viewModel { parameters -> CategoryEditViewModel(parameters.get(), get(), get()) }
    viewModel { parameters -> AttributeNameEditViewModel(parameters.get(), get()) }
    viewModel { parameters -> ValueNameEditViewModel(parameters.get(), get()) }
    viewModel { parameters ->
        AdminUserDetailViewModel(
            userId = parameters.get(),
            repository = get(),
            accountRepository = get(),
            permissions = get(),
        )
    }
    viewModel { AdminShopsViewModel(get()) }
    viewModel { AdminProductsViewModel(get()) }
    viewModel { AdminCommentsViewModel(get()) }
    viewModel { parameters -> AdminProductDetailsViewModel(parameters.get(), get()) }
    viewModel { AdminPlansViewModel(get(), get(), get()) }
    viewModel { AdminStaticPagesViewModel(get(), get(), get()) }
    viewModel { parameters -> AdminStaticPageEditorViewModel(parameters.getOrNull(), get()) }
}
