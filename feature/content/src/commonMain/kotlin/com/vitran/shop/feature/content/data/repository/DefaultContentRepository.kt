package com.vitran.shop.feature.content.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.content.data.mapper.toDomain
import com.vitran.shop.feature.content.data.remote.ContentApi
import com.vitran.shop.feature.content.domain.model.StaticPage
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import com.vitran.shop.feature.content.domain.model.StaticPageSummary
import com.vitran.shop.feature.content.domain.repository.ContentRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultContentRepository(
    private val contentApi: ContentApi,
) : ContentRepository {

    private val cacheMutex = Mutex()
    private var cachedPages: List<StaticPageSummary>? = null
    private val cachedDetails = mutableMapOf<StaticPageSlug, StaticPage>()

    override suspend fun getStaticPages(
        forceRefresh: Boolean,
    ): AppResult<List<StaticPageSummary>> {
        if (!forceRefresh) {
            cacheMutex.withLock {
                cachedPages?.let { return AppResult.Success(it) }
            }
        }

        return when (val result = contentApi.getStaticPages()) {
            is AppResult.Success -> {
                val pages = result.value.staticPages.map { it.toDomain() }
                cacheMutex.withLock { cachedPages = pages }
                AppResult.Success(pages)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun getStaticPageBySlug(
        slug: StaticPageSlug,
        forceRefresh: Boolean,
    ): AppResult<StaticPage> {
        if (!forceRefresh) {
            cacheMutex.withLock {
                cachedDetails[slug]?.let { return AppResult.Success(it) }
            }
        }

        return when (val result = contentApi.getStaticPageBySlug(slug)) {
            is AppResult.Success -> {
                val page = result.value.staticPage.toDomain()
                cacheMutex.withLock { cachedDetails[slug] = page }
                AppResult.Success(page)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun invalidate() {
        cacheMutex.withLock {
            cachedPages = null
            cachedDetails.clear()
        }
    }
}
