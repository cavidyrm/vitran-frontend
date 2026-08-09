package com.vitran.shop.ui.sections.auth

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranSize

/** Shared auth form layout tokens (shop.app `/accounts/login`). */
internal object AuthTokens {
    /** shop.app login form column `w-[344px]` (sm+). */
    val FormMaxWidth = 344.dp

    /** shop.app email / continue `h-[52px]`. */
    val ControlHeight = 52.dp

    /** shop.app swirl logo 32×32. */
    val LogoSize = VitranSize.iconLarge

    /** shop.app verify illustration `size-[84px]`. */
    val VerifyIllustrationSize = 84.dp

    /** shop.app OTP cell `aspect-[47/51]`. */
    val OtpCellWidth = 47.dp
    val OtpCellHeight = 51.dp
    val OtpCellRadius = 12.dp
    val OtpCellGap = 8.dp
    val OtpGroupGap = 25.dp
    val OtpSeparatorSize = 5.dp

    /** shop.app input fill `rgba(0,0,0,0.04)`. */
    val FieldFill = Color.Black.copy(alpha = 0.04f)

    /** shop.app secondary copy `rgba(0,0,0,0.56)`. */
    val Muted = Color.Black.copy(alpha = 0.56f)

    /** Idle OTP cell border `rgba(0,0,0,0.06)`. */
    val OtpIdleBorder = Color.Black.copy(alpha = 0.06f)

    /** shop.app continue shadow elevation. */
    val ContinueShadowElevation = 12.dp
}
