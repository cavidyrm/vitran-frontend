package com.vitran.shop.feature.content.di

import com.vitran.shop.feature.content.data.remote.ContentApi
import com.vitran.shop.feature.content.data.repository.DefaultContentRepository
import com.vitran.shop.feature.content.domain.html.AllowlistHtmlSanitizer
import com.vitran.shop.feature.content.domain.html.HtmlSanitizer
import com.vitran.shop.feature.content.domain.repository.ContentCacheInvalidator
import com.vitran.shop.feature.content.domain.repository.ContentRepository
import com.vitran.shop.feature.content.presentation.PublicStaticPageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val contentModule = module {
    single<HtmlSanitizer> { AllowlistHtmlSanitizer() }
    single { ContentApi(get(), get(), get()) }
    single<ContentRepository> { DefaultContentRepository(get(), get()) }
    single<ContentCacheInvalidator> {
        ContentCacheInvalidator { get<ContentRepository>().invalidate() }
    }
    viewModel { parameters ->
        PublicStaticPageViewModel(
            slug = parameters.get(),
            contentRepository = get(),
            sanitizer = get(),
        )
    }
}
