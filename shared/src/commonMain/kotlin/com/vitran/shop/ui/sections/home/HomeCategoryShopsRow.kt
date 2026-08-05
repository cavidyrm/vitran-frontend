package com.vitran.shop.ui.sections.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.HomeMerchantSpotlightCard
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.shell.LocalShellViewportWidth
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.home_category_shops_scroll_next_a11y
import vitranshop.shared.generated.resources.home_category_shops_scroll_prev_a11y
import vitranshop.shared.generated.resources.home_category_shops_section_open_a11y
import vitranshop.shared.generated.resources.ic_bold_right_chevron
import vitranshop.shared.generated.resources.ic_chevron_right

/** shop.app merchant card width (`w-[330px]`). */
private val MerchantCardWidth = 330.dp

/** shop.app merchant card height (`h-[397px]`). */
private val MerchantCardHeight = 397.dp

/** shop.app header title↔chevron `gap-space-8`. */
private val HeaderChevronGap = VitranSpacing.sm

/** shop.app header chevron chip: 16dp circle. */
private val HeaderChevronChip = 16.dp

/** Glyph size inside the 16dp chip. */
private val HeaderChevronGlyph = 12.dp

/** shop.app carousel scroll button (`42×42`). */
private val ScrollButtonSize = 42.dp

/** shop.app chevron glyph inside scroll button. */
private val ScrollChevronSize = 20.dp

/** One-card scroll step (card + desktop gap). */
private val ScrollStep = MerchantCardWidth + VitranSpacing.lg

/** shop.app arrow overhang (`±space-16`). */
private val ArrowOverhang = VitranSpacing.lg

/**
 * shop.app `border-border-image` — `rgba(5, 41, 77, 0.1)`.
 */
private val ArrowBorder = Color(0x1A05294D)

/**
 * shop.app `shadow-m` on carousel arrows.
 */
private val ArrowShadowElevation = 24.dp
private val ArrowShadowColor = Color.Black.copy(alpha = 0.12f)

/** shop.app `bg-bg-fill-secondary` for header chevron chip. */
private val HeaderChevronFill = Color(0xFFF2F4F5)

/**
 * Vertical stack of category shop carousels under Home mosaics (shop.app).
 *
 * Measured gaps: compact 40dp / desktop 64dp between sections.
 */
@Composable
fun HomeCategoryShopsFeed(
    sections: List<HomeCategoryShopSection>,
    modifier: Modifier = Modifier,
    onCategoryClick: (HomeCategoryShopSection) -> Unit = {},
    onShopClick: (HomeCategoryShopSection, HomeShopCard) -> Unit = { _, _ -> },
    onProductClick: (HomeCategoryShopSection, HomeShopCard, HomeShopProductPeek) -> Unit =
        { _, _, _ -> },
) {
    val isDesktop = LocalDesktopLayout.current
    val sectionGap = if (isDesktop) 64.dp else 40.dp
    // Bottom spacing owned by SiteFooter top padding — avoid double gap.

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(sectionGap),
    ) {
        sections.forEach { section ->
            HomeCategoryShopsRow(
                section = section,
                onCategoryClick = { onCategoryClick(section) },
                onShopClick = { shop -> onShopClick(section, shop) },
                onProductClick = { shop, peek -> onProductClick(section, shop, peek) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * One category header + horizontal merchant spotlight carousel.
 *
 * Measured tokens (shop.app 2026):
 * - Card 330×397 fixed (no mobile downscale), radius 28, shadow-md
 * - Carousel pad 16 compact / 48 desktop; gap 8 / 16
 * - Header↔carousel 16; title 18/20 Normal (md ≥768 → 20)
 * - Desktop scroll chevrons centered on card band
 */
@Composable
fun HomeCategoryShopsRow(
    section: HomeCategoryShopSection,
    modifier: Modifier = Modifier,
    onCategoryClick: () -> Unit = {},
    onShopClick: (HomeShopCard) -> Unit = {},
    onProductClick: (HomeShopCard, HomeShopProductPeek) -> Unit = { _, _ -> },
) {
    val isDesktop = LocalDesktopLayout.current
    val isMdUp = LocalShellViewportWidth.current >= VitranSize.mdBreakpoint
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val horizontalPad = if (isDesktop) VitranSpacing.xxxl + VitranSpacing.lg else VitranSpacing.lg
    val itemGap = if (isDesktop) VitranSpacing.lg else VitranSpacing.sm
    val scrollStepPx = with(density) { ScrollStep.toPx() }
    val endOverhang = if (isRtl) -ArrowOverhang else ArrowOverhang

    // Header band matches title lineHeight (20 compact / 22 md+).
    val headerBand = if (isMdUp) 22.dp else 20.dp
    val headerToCarousel = VitranSpacing.lg
    val buttonTopInset =
        headerBand + headerToCarousel + (MerchantCardHeight - ScrollButtonSize) / 2

    val showPrev = listState.canScrollBackward
    val showNext = listState.canScrollForward
    val openLabel = stringResource(
        Res.string.home_category_shops_section_open_a11y,
        section.title,
    )
    val headerInteraction = remember { MutableInteractionSource() }
    // shop.app `text-subtitle` / `md:text-sectionTitle`.
    val titleStyle = if (isMdUp) {
        MaterialTheme.typography.titleMedium.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 22.sp,
        )
    } else {
        MaterialTheme.typography.titleMedium.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPad)
                .clickable(
                    interactionSource = headerInteraction,
                    indication = null,
                    role = Role.Button,
                    onClick = onCategoryClick,
                )
                .semantics { contentDescription = openLabel }
                .padding(vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HeaderChevronGap),
        ) {
            Text(
                text = section.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = titleStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            Box(
                modifier = Modifier
                    .size(HeaderChevronChip)
                    .clip(CircleShape)
                    .background(HeaderChevronFill),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_bold_right_chevron),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
                    size = HeaderChevronGlyph,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(modifier = Modifier.height(headerToCarousel))

        Box(modifier = Modifier.fillMaxWidth()) {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPad),
                horizontalArrangement = Arrangement.spacedBy(itemGap),
            ) {
                items(
                    items = section.shops,
                    key = { it.id },
                ) { shop ->
                    HomeMerchantSpotlightCard(
                        shop = shop,
                        onShopClick = { onShopClick(shop) },
                        onProductClick = { peek -> onProductClick(shop, peek) },
                    )
                }
            }

            if (isDesktop && showPrev) {
                ShopsScrollButton(
                    forward = false,
                    isRtl = isRtl,
                    onClick = {
                        scope.launch { listState.animateScrollBy(-scrollStepPx) }
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = buttonTopInset, start = VitranSpacing.sm)
                        .offset(x = -endOverhang)
                        .zIndex(2f),
                )
            }
            if (isDesktop && showNext) {
                ShopsScrollButton(
                    forward = true,
                    isRtl = isRtl,
                    onClick = {
                        scope.launch { listState.animateScrollBy(scrollStepPx) }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = buttonTopInset, end = VitranSpacing.sm)
                        .offset(x = endOverhang)
                        .zIndex(2f),
                )
            }
        }
    }
}

@Composable
private fun ShopsScrollButton(
    forward: Boolean,
    isRtl: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val a11y = stringResource(
        if (forward) {
            Res.string.home_category_shops_scroll_next_a11y
        } else {
            Res.string.home_category_shops_scroll_prev_a11y
        },
    )
    val flipForRtl = when {
        forward && isRtl -> true
        !forward && !isRtl -> true
        else -> false
    }
    Box(
        modifier = modifier
            .size(ScrollButtonSize)
            .shadow(
                elevation = ArrowShadowElevation,
                shape = CircleShape,
                clip = false,
                ambientColor = ArrowShadowColor,
                spotColor = ArrowShadowColor,
            )
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .border(width = 1.dp, color = ArrowBorder, shape = CircleShape)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = a11y,
            modifier = Modifier.graphicsLayer { scaleX = if (flipForRtl) -1f else 1f },
            size = ScrollChevronSize,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 520)
@Composable
private fun HomeCategoryShopsRowCompactPreview() {
    VitranTheme {
        CompositionLocalProvider(
            LocalDesktopLayout provides false,
            LocalShellViewportWidth provides 390.dp,
        ) {
            HomeCategoryShopsRow(
                section = rememberMockHomeCategoryShopSections().first(),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 1200, heightDp = 520)
@Composable
private fun HomeCategoryShopsRowDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(
            LocalDesktopLayout provides true,
            LocalShellViewportWidth provides 1200.dp,
        ) {
            HomeCategoryShopsRow(
                section = rememberMockHomeCategoryShopSections().first(),
            )
        }
    }
}
