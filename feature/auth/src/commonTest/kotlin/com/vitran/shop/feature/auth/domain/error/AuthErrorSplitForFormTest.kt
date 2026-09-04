package com.vitran.shop.feature.auth.domain.error

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.error.FieldError
import com.vitran.shop.feature.auth.presentation.AuthFormFields
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthErrorSplitForFormTest {

    @Test
    fun registerPasswordError_mapsUnderPassword_andHidesEnvelope() {
        val error = AppError.Validation(
            message = "داده‌های ارسالی نامعتبر است.",
            fieldErrors = listOf(
                FieldError(
                    reason = "password",
                    messages = listOf(
                        "گذرواژه باید حداقل ۸ کاراکتر باشد و شامل حرف بزرگ، حرف کوچک، عدد و نماد باشد.",
                    ),
                ),
            ),
        ).toAuthError()

        val split = error.splitForForm(
            knownReasons = AuthFormFields.register,
            fallbackMessage = "ثبت‌نام ناموفق بود",
        )

        assertEquals(
            "گذرواژه باید حداقل ۸ کاراکتر باشد و شامل حرف بزرگ، حرف کوچک، عدد و نماد باشد.",
            split.fieldErrors["password"],
        )
        assertNull(split.generalMessage)
    }

    @Test
    fun loginCredentialsError_staysOnBanner() {
        val error = AppError.Validation(
            message = "ورود ناموفق بود",
            fieldErrors = listOf(
                FieldError("credentials", listOf("اطلاعات ورود نادرست است")),
            ),
        ).toAuthError()

        val split = error.splitForForm(
            knownReasons = AuthFormFields.login,
            fallbackMessage = "اطلاعات ورود نادرست است",
        )

        assertEquals(emptyMap(), split.fieldErrors)
        assertEquals("اطلاعات ورود نادرست است", split.generalMessage)
    }
}
