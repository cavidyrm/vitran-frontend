package com.vitran.shop.core.network.pagination

import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.pagination.PagePagination
import io.ktor.http.ParametersBuilder

fun ParametersBuilder.appendCursorPagination(pagination: CursorPagination) {
    append("per_page", pagination.perPage.toString())
    pagination.cursor?.let { append("cursor", it) }
}

fun ParametersBuilder.appendPagePagination(pagination: PagePagination) {
    append("page", pagination.page.toString())
    append("per_page", pagination.perPage.toString())
}
