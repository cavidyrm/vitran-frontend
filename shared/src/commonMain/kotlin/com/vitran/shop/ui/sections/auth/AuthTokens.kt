package com.vitran.shop.ui.sections.auth

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.ShopPurpleDark
import com.vitran.shop.ui.theme.VitranSize

/** Shared auth form layout tokens (split shell + dense credentials / OTP cards). */
internal object AuthTokens {
    val FormCardMaxWidth = 428.dp
    val OtpCardMaxWidth = 428.dp
    val CardPaddingHorizontal = 24.dp
    val CardPaddingVertical = 28.dp

    val ControlHeight = 48.dp
    val LabeledFieldHeight = 44.dp
    val LogoSize = VitranSize.iconLarge
    /** Fixed width for non-editable +98 prefix. */
    val CountryCodeWidth = 52.dp

    val OtpCellWidth = 42.dp
    val OtpCellHeight = 48.dp
    val OtpCellRadius = 10.dp
    val OtpCellGap = 3.dp

    val FieldFill = Color.Black.copy(alpha = 0.04f)
    val FieldBorder = Color.Black.copy(alpha = 0.14f)
    val FieldBorderFocus = ShopPurple.copy(alpha = 0.55f)

    /** Darker brand-tinted disabled CTA for clearer primary affordance. */
    val DisabledCtaFill = ShopPurpleDark.copy(alpha = 0.55f)
    val DisabledCtaLabel = Color.White.copy(alpha = 0.95f)

    val PageCanvas = Color(0xFFF3F2F7)
    val Muted = Color.Black.copy(alpha = 0.62f)
    val LegalMuted = Color.Black.copy(alpha = 0.88f)
    val OtpIdleBorder = Color.Black.copy(alpha = 0.08f)
    val OtpError = Color(0xFFD32F2F)
    val OtpSuccess = Color(0xFF1B7A3D)

    val StrengthWeak = Color(0xFFE57373)
    val StrengthFair = Color(0xFFFFB74D)
    val StrengthGood = Color(0xFF81C784)
    val StrengthStrong = Color(0xFF43A047)

    val BrandGradientStart = ShopPurple
    val BrandGradientEnd = ShopPurpleDark

    val CardShadowElevation = 10.dp
    const val MinPasswordLength = 5
    const val OtpResendSeconds = 45
    /** Mock wrong OTP used to demo error state. */
    const val MockInvalidOtp = "000000"
}

enum class AuthMode {
    Login,
    Register,
}

enum class PasswordStrength {
    Empty,
    Weak,
    Fair,
    Good,
    Strong,
}

internal fun passwordStrengthOf(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.Empty
    val hasLetter = password.any { it.isLetter() }
    val hasDigit = password.any { it.isDigit() }
    val len = password.length
    return when {
        len < AuthTokens.MinPasswordLength -> PasswordStrength.Weak
        len >= 10 && hasLetter && hasDigit -> PasswordStrength.Strong
        len >= 8 && (hasLetter || hasDigit) -> PasswordStrength.Good
        else -> PasswordStrength.Fair
    }
}

/**
 * Normalizes Iranian national digits to `09xxxxxxxxx` for nav / OTP display.
 * Accepts input with or without leading 0; strips non-digits.
 */
fun normalizeIranMobile(nationalDigits: String): String {
    val digits = nationalDigits.filter { it.isDigit() }
    if (digits.isEmpty()) return ""
    val withoutCountry = when {
        digits.startsWith("98") && digits.length > 10 -> digits.drop(2)
        else -> digits
    }
    val local = withoutCountry.trimStart('0')
    return if (local.isEmpty()) "" else "0$local"
}

/** Masks `09123456789` → `0912***6789`. */
fun maskIranMobile(mobile: String): String {
    val digits = mobile.filter { it.isDigit() }
    if (digits.length < 8) return mobile.ifBlank { "—" }
    val start = digits.take(4)
    val end = digits.takeLast(4)
    return "$start***$end"
}
