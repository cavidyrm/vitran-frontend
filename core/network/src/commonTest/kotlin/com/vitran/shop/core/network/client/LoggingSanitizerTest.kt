package com.vitran.shop.core.network.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoggingSanitizerTest {

    @Test
    fun authorizationHeader_isRedacted() {
        assertEquals("***REDACTED***", sanitizeHeaderValue("Authorization", "Bearer secret-token"))
    }

    @Test
    fun accessTokenInJsonBody_isRedacted() {
        val sanitized = sanitizeLogMessage("""{"access_token":"super-secret","status":"ok"}""")
        assertFalse(sanitized.contains("super-secret"))
        assertEquals("""{"access_token":"***REDACTED***","status":"ok"}""", sanitized)
    }

    @Test
    fun apiKeyInJsonBody_isRedacted() {
        val sanitized = sanitizeLogMessage("""{"api_key":"vt_live_xxxxxxxx","status":"ok"}""")
        assertFalse(sanitized.contains("vt_live_xxxxxxxx"))
        assertEquals("""{"api_key":"***REDACTED***","status":"ok"}""", sanitized)
    }

    @Test
    fun passwordField_isRedacted() {
        val sanitized = sanitizeLogMessage("""{"password":"123456"}""")
        assertFalse(sanitized.contains("123456"))
    }

    @Test
    fun multipartImageBinary_isRedacted() {
        val raw =
            """
            Content-Disposition: form-data; name="title"

            Blue Widget
            --boundary
            Content-Disposition: form-data; name="images"; filename="a.jpg"
            Content-Type: image/jpeg

            BINARY_IMAGE_BYTES_SHOULD_NOT_LEAK
            --boundary--
            """.trimIndent()
        val sanitized = sanitizeLogMessage(raw)
        assertFalse(sanitized.contains("BINARY_IMAGE_BYTES_SHOULD_NOT_LEAK"))
        assertTrue(sanitized.contains("***BINARY_REDACTED***") || sanitized.contains("name=\"images\""))
    }
}
