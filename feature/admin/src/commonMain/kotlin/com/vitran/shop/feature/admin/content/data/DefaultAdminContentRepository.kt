package com.vitran.shop.feature.admin.content.data

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.content.domain.AdminContentRepository
import com.vitran.shop.feature.admin.content.domain.CreateStaticPageCommand
import com.vitran.shop.feature.admin.content.domain.UpdateStaticPageCommand
import com.vitran.shop.feature.content.domain.model.HtmlContent
import com.vitran.shop.feature.content.domain.model.StaticPage
import com.vitran.shop.feature.content.domain.model.StaticPageId
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import com.vitran.shop.feature.content.domain.model.StaticPageSummary
import com.vitran.shop.feature.content.domain.repository.ContentCacheInvalidator

internal class DefaultAdminContentRepository(
    private val api: AdminContentApi,
    private val cacheInvalidator: ContentCacheInvalidator,
) : AdminContentRepository {
    override suspend fun getPages() = api.getPages().mapSuccess { it.staticPages.map(AdminStaticPageSummaryDto::toDomain) }
    override suspend fun getPage(id: StaticPageId) = api.getPage(id).mapSuccess { it.staticPage.toDomain() }
    override suspend fun create(command: CreateStaticPageCommand) =
        api.create(command).mapMutation { it.staticPage.toDomain() }
    override suspend fun update(command: UpdateStaticPageCommand) =
        api.update(command).mapMutation { it.staticPage.toDomain() }
    override suspend fun delete(id: StaticPageId): AppResult<Unit> =
        when (val result = api.delete(id)) {
            is AppResult.Success -> {
                cacheInvalidator.invalidate()
                result
            }
            is AppResult.Failure -> result
        }

    private suspend inline fun <T, R> AppResult<T>.mapMutation(transform: (T) -> R): AppResult<R> =
        when (this) {
            is AppResult.Success -> {
                val mapped = transform(value)
                cacheInvalidator.invalidate()
                AppResult.Success(mapped)
            }
            is AppResult.Failure -> this
        }
}

private fun AdminStaticPageSummaryDto.toDomain() =
    StaticPageSummary(StaticPageId(id), StaticPageSlug(slug), title, active, sortOrder)

private fun AdminStaticPageDetailsDto.toDomain() =
    StaticPage(StaticPageId(id), StaticPageSlug(slug), title, HtmlContent(body), active, sortOrder)

private inline fun <T, R> AppResult<T>.mapSuccess(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}
