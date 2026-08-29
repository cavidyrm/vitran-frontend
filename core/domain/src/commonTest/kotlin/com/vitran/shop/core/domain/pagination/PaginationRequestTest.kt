package com.vitran.shop.core.domain.pagination

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PaginationRequestTest {

    @Test
    fun cursorPagination_defaultsToTwentyPerPage() {
        val pagination = CursorPagination()
        assertEquals(20, pagination.perPage)
        assertEquals(null, pagination.cursor)
    }

    @Test
    fun cursorPagination_clampsUpperBound() {
        assertFailsWith<IllegalArgumentException> {
            CursorPagination(perPage = 101)
        }
    }

    @Test
    fun pagePagination_defaultsToFirstPage() {
        val pagination = PagePagination()
        assertEquals(1, pagination.page)
        assertEquals(20, pagination.perPage)
    }

    @Test
    fun pagePagination_rejectsZeroPage() {
        assertFailsWith<IllegalArgumentException> {
            PagePagination(page = 0)
        }
    }
}
