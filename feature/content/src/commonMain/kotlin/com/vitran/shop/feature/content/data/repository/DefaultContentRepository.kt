package com.vitran.shop.feature.content.data.repository

import com.vitran.shop.core.database.VitranDatabase
import com.vitran.shop.core.database.entity.StaticPageEntity
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.content.data.mapper.toDomain
import com.vitran.shop.feature.content.data.remote.ContentApi
import com.vitran.shop.feature.content.domain.model.HtmlContent
import com.vitran.shop.feature.content.domain.model.StaticPage
import com.vitran.shop.feature.content.domain.model.StaticPageId
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import com.vitran.shop.feature.content.domain.model.StaticPageSummary
import com.vitran.shop.feature.content.domain.repository.ContentRepository
import kotlin.time.Clock

internal class DefaultContentRepository(
    private val contentApi: ContentApi,
    private val database: VitranDatabase,
) : ContentRepository {

    private val staticPageDao get() = database.staticPageDao()

    override suspend fun getStaticPages(
        forceRefresh: Boolean,
    ): AppResult<List<StaticPageSummary>> {
        if (!forceRefresh) {
            val cached = staticPageDao.getAll()
            if (cached.isNotEmpty()) {
                return AppResult.Success(cached.map { it.toSummary() })
            }
        }

        return when (val result = contentApi.getStaticPages()) {
            is AppResult.Success -> {
                val pages = result.value.staticPages.map { it.toDomain() }
                val now = Clock.System.now().toEpochMilliseconds()
                val existingBodies = staticPageDao.getAll().associate { it.slug to it.bodyHtml }
                staticPageDao.replaceAll(
                    pages.map { summary ->
                        summary.toEntity(
                            bodyHtml = existingBodies[summary.slug.value].orEmpty(),
                            fetchedAt = now,
                        )
                    },
                )
                AppResult.Success(pages)
            }
            is AppResult.Failure -> {
                val cached = staticPageDao.getAll()
                if (cached.isNotEmpty()) {
                    AppResult.Success(cached.map { it.toSummary() })
                } else {
                    AppResult.Failure(result.error)
                }
            }
        }
    }

    override suspend fun getStaticPageBySlug(
        slug: StaticPageSlug,
        forceRefresh: Boolean,
    ): AppResult<StaticPage> {
        if (!forceRefresh) {
            staticPageDao.getBySlug(slug.value)?.takeIf { it.bodyHtml.isNotEmpty() }?.let { entity ->
                return AppResult.Success(entity.toDomain())
            }
        }

        return when (val result = contentApi.getStaticPageBySlug(slug)) {
            is AppResult.Success -> {
                val page = result.value.staticPage.toDomain()
                val now = Clock.System.now().toEpochMilliseconds()
                staticPageDao.upsert(page.toEntity(fetchedAt = now))
                AppResult.Success(page)
            }
            is AppResult.Failure -> {
                val cached = staticPageDao.getBySlug(slug.value)?.takeIf { it.bodyHtml.isNotEmpty() }
                if (cached != null) {
                    AppResult.Success(cached.toDomain())
                } else {
                    AppResult.Failure(result.error)
                }
            }
        }
    }

    override suspend fun invalidate() {
        staticPageDao.deleteAll()
    }
}

private fun StaticPageEntity.toSummary(): StaticPageSummary =
    StaticPageSummary(
        id = StaticPageId(id),
        slug = StaticPageSlug(slug),
        title = title,
        active = active,
        sortOrder = sortOrder,
    )

private fun StaticPageEntity.toDomain(): StaticPage =
    StaticPage(
        id = StaticPageId(id),
        slug = StaticPageSlug(slug),
        title = title,
        bodyHtml = HtmlContent(bodyHtml),
        active = active,
        sortOrder = sortOrder,
    )

private fun StaticPageSummary.toEntity(bodyHtml: String, fetchedAt: Long): StaticPageEntity =
    StaticPageEntity(
        id = id.value,
        slug = slug.value,
        title = title,
        bodyHtml = bodyHtml,
        active = active,
        sortOrder = sortOrder,
        fetchedAt = fetchedAt,
    )

private fun StaticPage.toEntity(fetchedAt: Long): StaticPageEntity =
    StaticPageEntity(
        id = id.value,
        slug = slug.value,
        title = title,
        bodyHtml = bodyHtml.rawHtml,
        active = active,
        sortOrder = sortOrder,
        fetchedAt = fetchedAt,
    )
