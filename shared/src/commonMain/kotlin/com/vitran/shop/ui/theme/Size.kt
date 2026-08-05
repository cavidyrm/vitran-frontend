package com.vitran.shop.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Recurring component sizes (icons, avatars, rails, touch targets).
 * One-off values that are not reused stay as local literals.
 */
object VitranSize {
    val iconSmall = 16.dp
    val iconMedium = 24.dp
    val iconLarge = 32.dp

    val avatarSmall = 28.dp
    val avatarMedium = 32.dp
    val avatarLarge = 40.dp

    val touchTarget = 48.dp
    val buttonHeight = 48.dp

    val sideRailWidth = 72.dp
    val bottomBarHeight = 72.dp
    val profileItemHeight = 54.dp
    val downloadBannerHeight = 40.dp

    val borderHairline = 0.5.dp

    /** App chrome: side rail vs bottom nav (shop.app lg-ish). */
    val desktopBreakpoint = 1024.dp

    /** shop.app `md` — section titles switch to `text-sectionTitle` (20sp). */
    val mdBreakpoint = 768.dp

    /** shop.app `xl` — mosaic aspect 374/340 + radius 28 (measured ≥1440). */
    val xlBreakpoint = 1440.dp
}
