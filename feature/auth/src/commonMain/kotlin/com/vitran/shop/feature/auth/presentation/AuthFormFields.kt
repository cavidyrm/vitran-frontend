package com.vitran.shop.feature.auth.presentation

internal object AuthFormFields {
    const val Phone = "phone"
    const val Password = "password"
    const val ReferralCode = "referral_code"
    const val Code = "code"
    const val NewPassword = "new_password"

    val login = setOf(Phone, Password)
    val register = setOf(Phone, Password, ReferralCode)
    val forgot = setOf(Phone)
    val reset = setOf(Code, Password)
    val resetAliases = mapOf(NewPassword to Password)
    val verify = setOf(Code)
}
