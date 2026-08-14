package com.vitran.shop.ui.components.admin

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing

/** Shopify-admin-inspired tokens for merchant form screens. Local to admin UI. */
object AdminTokens {
    val PageBackground = Color(0xFFF4F4F5)
    val CardBorder = Color(0xFFE7E5E4)
    val FieldBorder = Color(0xFFD4D4D8)
    val FieldBorderFocused = Color(0xFF5A31F4)
    val Helper = Color(0xFF52525B)
    val Placeholder = Color(0xFF71717A)
    val SaveFill = Color(0xFF18181B)
    val Brand = Color(0xFF5A31F4)
    val OnBrand = Color.White
    val Destructive = Color(0xFFD32F2F)
    val ErrorBorder = Color(0xFFFECACA)
    val NestedPanel = Color(0xFFFAFAFA)
    val DropdownHover = Color(0xFFF4F4F5)
    val ToolbarFill = Color(0xFFFAFAF9)
    val Success = Color(0xFF15803D)
    val PreviewBezel = Color(0xFF18181B)

    val FieldHeight = 44.dp
    val SaveHeight = 40.dp
    val HeaderHeight = 64.dp
    val BottomBarHeight = 72.dp
    val CardRadius = VitranRadius.large
    val FieldRadius = VitranRadius.medium
    val CardGap = VitranSpacing.lg
    val CardPadding = VitranSpacing.xl
    val CardElevation = VitranElevation.medium
    val CardShadow = Color.Black.copy(alpha = 0.16f)
    val PageMaxWidth = 1220.dp
    val FormMaxWidth = 560.dp
    val ProductFormMaxWidth = 800.dp
    val DropdownMaxHeight = 280.dp
    val CoverDropzoneHeight = 140.dp
    val LogoDropzoneSize = 80.dp
    val MultilineMinHeight = 112.dp
    val PhonePreviewWidth = 260.dp
    val RailWidth = 300.dp
    val StepDot = 28.dp
}

@Immutable
data class AdminSelectOption(
    val id: String,
    val label: String,
    val description: String? = null,
)
