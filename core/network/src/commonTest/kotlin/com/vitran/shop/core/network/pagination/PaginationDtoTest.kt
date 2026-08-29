package com.vitran.shop.core.network.pagination

import com.vitran.shop.core.network.createTestJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.builtins.serializer

class PaginationDtoTest {

    private val json = createTestJson()

    @Test
    fun cursorPage_decodesNumericCursor() {
        val dto = json.decodeFromString(
            CursorPageDto.serializer(String.serializer()),
            """
            {
              "per_page": 20,
              "has_more": true,
              "next_cursor": "42",
              "results": []
            }
            """.trimIndent(),
        )

        assertEquals(20, dto.perPage)
        assertTrue(dto.hasMore)
        assertEquals("42", dto.nextCursor)
        val domain = dto.toDomain()
        assertEquals("42", domain.nextCursor)
    }

    @Test
    fun cursorPage_decodesOpaqueCursor() {
        val dto = json.decodeFromString(
            CursorPageDto.serializer(String.serializer()),
            """
            {
              "per_page": 20,
              "has_more": true,
              "next_cursor": "eyJpZCI6NDJ9",
              "results": ["a"]
            }
            """.trimIndent(),
        )

        assertEquals("eyJpZCI6NDJ9", dto.nextCursor)
        assertEquals(listOf("a"), dto.toDomain().items)
    }

    @Test
    fun cursorPage_nullCursorAndNoMore() {
        val dto = json.decodeFromString(
            CursorPageDto.serializer(String.serializer()),
            """
            {
              "per_page": 20,
              "has_more": false,
              "next_cursor": null,
              "results": []
            }
            """.trimIndent(),
        )

        assertNull(dto.nextCursor)
        assertFalse(dto.hasMore)
    }

    @Test
    fun pageDto_mapsToDomain() {
        val dto = json.decodeFromString(
            PageDto.serializer(String.serializer()),
            """
            {
              "page": 1,
              "per_page": 20,
              "last_page": 3,
              "from": 1,
              "to": 20,
              "total": 42,
              "has_more": true,
              "results": ["x"]
            }
            """.trimIndent(),
        )

        val domain = dto.toDomain()
        assertEquals(1, domain.page)
        assertEquals(20, domain.perPage)
        assertEquals(3, domain.lastPage)
        assertEquals(42, domain.total)
        assertTrue(domain.hasMore)
        assertEquals(listOf("x"), domain.items)
    }
}
