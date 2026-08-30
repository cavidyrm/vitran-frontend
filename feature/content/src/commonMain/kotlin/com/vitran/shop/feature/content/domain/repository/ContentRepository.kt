package com.vitran.shop.feature.content.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.content.domain.model.StaticPage
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import com.vitran.shop.feature.content.domain.model.StaticPageSummary

interface ContentRepository {
    suspend fun getStaticPages(forceRefresh: Boolean = false): AppResult<List<StaticPageSummary>>

    suspend fun getStaticPageBySlug(
        slug: StaticPageSlug,
        forceRefresh: Boolean = false,
    ): AppResult<StaticPage>

    suspend fun invalidate()
}
