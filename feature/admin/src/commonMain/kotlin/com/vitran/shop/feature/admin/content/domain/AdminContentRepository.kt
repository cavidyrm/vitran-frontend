package com.vitran.shop.feature.admin.content.domain

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.content.domain.model.HtmlContent
import com.vitran.shop.feature.content.domain.model.StaticPage
import com.vitran.shop.feature.content.domain.model.StaticPageId
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import com.vitran.shop.feature.content.domain.model.StaticPageSummary

data class CreateStaticPageCommand(
    val slug: StaticPageSlug,
    val title: String,
    val bodyHtml: HtmlContent,
    val active: Boolean,
    val sortOrder: Int,
)

data class UpdateStaticPageCommand(
    val id: StaticPageId,
    val slug: StaticPageSlug? = null,
    val title: String? = null,
    val bodyHtml: HtmlContent? = null,
    val active: Boolean? = null,
    val sortOrder: Int? = null,
)

interface AdminContentRepository {
    suspend fun getPages(): AppResult<List<StaticPageSummary>>
    suspend fun getPage(id: StaticPageId): AppResult<StaticPage>
    suspend fun create(command: CreateStaticPageCommand): AppResult<StaticPage>
    suspend fun update(command: UpdateStaticPageCommand): AppResult<StaticPage>
    suspend fun delete(id: StaticPageId): AppResult<Unit>
}
