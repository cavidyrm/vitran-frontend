package com.vitran.shop.feature.seller.subscription.domain

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaymentUrlValidatorTest {
    @Test
    fun allowsHttpAndHttps() {
        assertTrue(PaymentUrlValidator.isSafeToLaunch("https://pay.example/x"))
        assertTrue(PaymentUrlValidator.isSafeToLaunch("http://localhost:8080/payments/callback?Authority=x"))
    }

    @Test
    fun rejectsUnsafeSchemes() {
        assertFalse(PaymentUrlValidator.isSafeToLaunch("javascript:alert(1)"))
        assertFalse(PaymentUrlValidator.isSafeToLaunch("file:///tmp"))
        assertFalse(PaymentUrlValidator.isSafeToLaunch(""))
    }
}
