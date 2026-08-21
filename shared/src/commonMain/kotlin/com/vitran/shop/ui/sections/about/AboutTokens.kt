package com.vitran.shop.ui.sections.about

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing

/** Layout / color tokens for the public About Us page (`/about`). */
internal object AboutTokens {
    val ContentMaxWidth = 1120.dp
    val PagePadCompact = VitranSpacing.lg
    val PagePadDesktop = VitranSpacing.xxxl
    val SectionGap = 40.dp
    val CardRadius = VitranRadius.large
    val ImageRadius = VitranRadius.large
    val SoftSurface = Color(0xFFF8F7FF)
    val SoftBar = Color(0xFFF3EEFF)
    val CardShadowAlpha = 0.06f
}
