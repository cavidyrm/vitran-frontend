package com.vitran.shop.feature.content.data.mapper

import com.vitran.shop.feature.content.data.remote.dto.StaticPageDto
import com.vitran.shop.feature.content.data.remote.dto.StaticPageSummaryDto
import com.vitran.shop.feature.content.domain.model.HtmlContent
import com.vitran.shop.feature.content.domain.model.StaticPage
import com.vitran.shop.feature.content.domain.model.StaticPageId
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import com.vitran.shop.feature.content.domain.model.StaticPageSummary

internal fun StaticPageSummaryDto.toDomain() = StaticPageSummary(
    id = StaticPageId(id),
    slug = StaticPageSlug(slug),
    title = title,
    active = active,
    sortOrder = sortOrder,
)

internal fun StaticPageDto.toDomain() = StaticPage(
    id = StaticPageId(id),
    slug = StaticPageSlug(slug),
    title = title,
    bodyHtml = HtmlContent(body),
    active = active,
    sortOrder = sortOrder,
)
