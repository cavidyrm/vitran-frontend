package com.vitran.shop.feature.content.domain.html

import com.vitran.shop.feature.content.domain.model.HtmlContent

fun interface HtmlSanitizer {
    fun sanitize(html: HtmlContent): HtmlContent
}
