package com.vitran.shop.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Shop.app / screenshot-aligned brand tokens
val ShopPurple = Color(0xFF5A31F4)
val ShopPurpleDark = Color(0xFF4524DB)
val ShopPurpleSoft = Color(0xFFDBD1FF)
val ShopPurpleTint = Color(0xFFEEEAFF)
val BackgroundGray = Color(0xFFFCFCFC)
val SurfaceWhite = Color(0xFFFFFFFF)
val OnSurfacePrimary = Color(0xFF1A1A1A)
val OnSurfaceSecondary = Color(0xFF757575)
val OutlineGray = Color(0xFFE5E5E5)
val SaleBadgeBlack = Color(0xFF000000)
val StarGold = Color(0xFFF5A623)
val ErrorRed = Color(0xFFD32F2F)

val VitranLightColorScheme = lightColorScheme(
    primary = ShopPurple,
    onPrimary = Color.White,
    primaryContainer = ShopPurpleSoft,
    onPrimaryContainer = ShopPurpleDark,
    secondary = ShopPurpleDark,
    onSecondary = Color.White,
    secondaryContainer = ShopPurpleTint,
    onSecondaryContainer = ShopPurpleDark,
    tertiary = ShopPurple,
    onTertiary = Color.White,
    background = BackgroundGray,
    onBackground = OnSurfacePrimary,
    surface = SurfaceWhite,
    onSurface = OnSurfacePrimary,
    surfaceVariant = ShopPurpleTint,
    onSurfaceVariant = OnSurfaceSecondary,
    outline = OutlineGray,
    outlineVariant = OutlineGray,
    error = ErrorRed,
    onError = Color.White,
)

@Immutable
data class VitranExtraColors(
    val saleBadge: Color = SaleBadgeBlack,
    val star: Color = StarGold,
    val purpleDark: Color = ShopPurpleDark,
    val purpleTint: Color = ShopPurpleTint,
)

val LocalVitranExtraColors = staticCompositionLocalOf { VitranExtraColors() }
