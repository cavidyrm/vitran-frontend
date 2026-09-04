package com.vitran.shop.core.domain.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FormFieldErrorsTest {

    @Test
    fun toMessageMap_joinsMessagesForSameReason() {
        val map = listOf(
            FieldError("password", listOf("too short", "needs a symbol")),
            FieldError("PASSWORD", listOf("too short")),
        ).toMessageMap()

        assertEquals(
            "too short\nneeds a symbol",
            map["password"],
        )
        assertEquals(1, map.size)
    }

    @Test
    fun splitForForm_registerPasswordExample_hidesEnvelopeMessage() {
        val error = AppError.Validation(
            message = "داده‌های ارسالی نامعتبر است.",
            httpStatus = 400,
            backendCode = -2,
            fieldErrors = listOf(
                FieldError(
                    reason = "password",
                    messages = listOf(
                        "گذرواژه باید حداقل ۸ کاراکتر باشد و شامل حرف بزرگ، حرف کوچک، عدد و نماد باشد.",
                    ),
                ),
            ),
        )

        val split = error.splitForForm(knownReasons = setOf("phone", "password", "referral_code"))

        assertEquals(
            "گذرواژه باید حداقل ۸ کاراکتر باشد و شامل حرف بزرگ، حرف کوچک، عدد و نماد باشد.",
            split.fieldErrors["password"],
        )
        assertNull(split.generalMessage)
    }

    @Test
    fun splitForForm_unmappedCredentialsGoToBanner() {
        val error = AppError.Validation(
            message = "ورود ناموفق بود",
            fieldErrors = listOf(
                FieldError("credentials", listOf("اطلاعات ورود نادرست است")),
                FieldError("phone", listOf("شماره موبایل نامعتبر است")),
            ),
        )

        val split = error.splitForForm(knownReasons = setOf("phone", "password"))

        assertEquals("شماره موبایل نامعتبر است", split.fieldErrors["phone"])
        assertEquals("اطلاعات ورود نادرست است", split.generalMessage)
        assertEquals(setOf("phone"), split.fieldErrors.keys)
    }

    @Test
    fun splitForForm_aliasesMapOntoUiKeys() {
        val error = AppError.Validation(
            message = "نامعتبر",
            fieldErrors = listOf(
                FieldError("category_slug", listOf("دسته را انتخاب کنید")),
                FieldError("images", listOf("حداقل یک تصویر لازم است")),
            ),
        )

        val split = error.splitForForm(
            knownReasons = setOf("title", "price"),
            reasonAliases = mapOf(
                "category_slug" to "category",
                "category" to "category",
                "images" to "summary",
                "description" to "summary",
                "active" to "summary",
            ),
        )

        assertEquals("دسته را انتخاب کنید", split.fieldErrors["category"])
        assertEquals("حداقل یک تصویر لازم است", split.fieldErrors["summary"])
        assertNull(split.generalMessage)
    }

    @Test
    fun splitForForm_emptyFieldErrorsUsesEnvelopeMessage() {
        val error = AppError.Validation(message = "درخواست نامعتبر است")
        val split = error.splitForForm(knownReasons = setOf("phone"))
        assertEquals(emptyMap(), split.fieldErrors)
        assertEquals("درخواست نامعتبر است", split.generalMessage)
    }

    @Test
    fun messageFor_matchesAnyAlias() {
        val errors = listOf(FieldError("category_slug", listOf("الزامی")))
        assertEquals("الزامی", errors.messageFor("category_slug", "category"))
        assertNull(errors.messageFor("title"))
    }
}
