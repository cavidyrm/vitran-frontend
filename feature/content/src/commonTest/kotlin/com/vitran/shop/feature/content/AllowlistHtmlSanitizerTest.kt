package com.vitran.shop.feature.content

import com.vitran.shop.feature.content.domain.html.AllowlistHtmlSanitizer
import com.vitran.shop.feature.content.domain.model.HtmlContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AllowlistHtmlSanitizerTest {

    private val sanitizer = AllowlistHtmlSanitizer()

    @Test
    fun sanitize_removesDangerousBlocksUrlsAndEventAttributes() {
        val result = sanitizer.sanitize(
            HtmlContent(
                """
                <p>safe<script>alert('x')</script></p>
                <a href="javascript:alert(1)" onclick="alert(1)">link</a>
                <span><img src="x" onerror="alert(1)">text</span>
                """.trimIndent(),
            ),
        ).rawHtml

        assertFalse(result.contains("script", ignoreCase = true))
        assertFalse(result.contains("alert", ignoreCase = true))
        assertFalse(result.contains("javascript:", ignoreCase = true))
        assertFalse(result.contains("onerror", ignoreCase = true))
        assertFalse(result.contains("onclick", ignoreCase = true))
        assertFalse(result.contains("<img", ignoreCase = true))
    }

    @Test
    fun sanitize_keepsBasicSafeHtmlAndHttpLinks() {
        val input = HtmlContent(
            """<h2>Title</h2><p>Hello <strong>world</strong><br><a href="https://example.com">More</a></p>""",
        )

        val result = sanitizer.sanitize(input).rawHtml

        assertEquals(input.rawHtml, result)
        assertTrue(result.contains("href=\"https://example.com\""))
    }
}
