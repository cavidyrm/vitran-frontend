package com.vitran.shop.ui.sections.account

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing

/**
 * Account / Profile layout tokens measured from shop.app screenshots
 * (`docs/ui-reference/account/`). Live `/account` is gated behind sign-in.
 */
internal object AccountTokens {
    val CardRadius = VitranRadius.xl
    val CardBorder = Color(0xFFE5E5E5)
    val FieldDivider = Color(0xFFF0F0F0)
    val Placeholder = Color(0xFFB3B3B3)
    val MenuHover = Color(0xFFF2F2F2)
    val DashedBorder = Color(0xFFD6D6D6)
    val ChipBorder = Color(0xFFE5E5E5)
    val SaveOverlay = Color(0x4D282828)
    val ImagePlaceholder = Color(0xFFF2F4F5)
    val ImageBorder = Color(0x1A05294D)

    val SubNavWidth = 184.dp
    /** shop.app profile/account form column — not the full content pane. */
    val ContentMaxWidth = 560.dp
    val NavContentGap = VitranSpacing.xxxl
    /** Sub-nav + gap + content — centered together on desktop (shop.app hub). */
    val DesktopClusterMaxWidth = SubNavWidth + NavContentGap + ContentMaxWidth
    /** Gap between hub cards / sections — shop.app ~16–24. */
    val SectionGap = VitranSpacing.xxl
    val TitledSectionGap = VitranSpacing.xxxl
    val PagePadCompact = VitranSpacing.lg
    val PagePadDesktop = VitranSpacing.xxxl + VitranSpacing.lg

    /** Soft lift under account cards — match AdminFormCard / Auth (Wasm-visible). */
    val CardElevation = VitranElevation.medium
    val CardShadow = Color.Black.copy(alpha = 0.16f)

    /** Compact hub identity row — shop.app account header. */
    val AvatarHubCompact = 48.dp
    /** Account hub identity card avatar (design mock). */
    val AvatarHubHeader = 64.dp
    val AvatarHub = 80.dp
    val AvatarProfile = 112.dp
    val HubChevronBtn = 40.dp
    val EditBadge = 32.dp
    val SectionIconTile = 40.dp
    val FieldMinHeight = 56.dp
    val StackedFieldHeight = 48.dp
    /** Username available / success feedback (profile redesign). */
    val Success = Color(0xFF16A34A)
    val ShortcutThumb = 40.dp
    /** Side-by-side Saved / Following tiles on the hub. */
    val ShortcutCardThumb = 56.dp
    val ShortcutCardMinHeight = 120.dp
    val PromoIconSize = 48.dp
    val ReferralIllustrationWidth = 200.dp
    val ReferralIllustrationHeight = 140.dp
    /** Referrals page content column — wider than profile forms for stats grid. */
    val ReferralsContentMaxWidth = 720.dp
    val ReferralHeroIllustration = 160.dp
    val ReferralHeroIllustrationWidth = 200.dp
    val ReferralRulesIllustration = 80.dp
    val ReferralPlanCardWidth = 120.dp
    val ReferralPlanCardHeight = 88.dp
    val ReferralStatIcon = 40.dp
    val ReferralProgressHeight = 8.dp
    val ReferralHeroBg = Color(0xFFF5F2FF)
    val ReferralPending = Color(0xFFF59E0B)
    val ReferralPendingSoft = Color(0xFFFFF4E5)
    val ReferralSuccessSoft = Color(0xFFE8F8EF)
    val ReferralStatIconBg = Color(0xFFEEEAFF)
    /** Settings page two-column cards — `docs/ui-reference/account/settings-desktop.png`. */
    val SettingsContentMaxWidth = 920.dp
    /** Users list table — `docs/ui-reference/account/users-list-desktop.png`. */
    val UsersContentMaxWidth = 1100.dp
    val UserDetailRailWidth = 300.dp
    val UserDetailAvatar = 72.dp
    val UserNoteMinHeight = 112.dp
    val UserAvatar = 40.dp
    val StatusDot = 8.dp
    val DangerSoft = Color(0xFFFFEBEE)
    val SoftIconBg = Color(0xFFEEEAFF)
    val SellerIllustrationWidth = 200.dp
    val SellerIllustrationHeight = 130.dp
    val RecentlyViewedMin = 132.dp
    val RecentlyViewedGap = VitranSpacing.md
    val ScrollButton = 42.dp
}
