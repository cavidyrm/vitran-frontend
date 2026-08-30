package com.vitran.shop.feature.content.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class StaticPageId(val value: Long)

@JvmInline
value class StaticPageSlug(val value: String)

@JvmInline
value class HtmlContent(val rawHtml: String)

data class StaticPageSummary(
    val id: StaticPageId,
    val slug: StaticPageSlug,
    val title: String,
    val active: Boolean,
    val sortOrder: Int,
)

data class StaticPage(
    val id: StaticPageId,
    val slug: StaticPageSlug,
    val title: String,
    val bodyHtml: HtmlContent,
    val active: Boolean,
    val sortOrder: Int,
)
