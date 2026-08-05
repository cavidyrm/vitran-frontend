package com.vitran.shop.ui.sections.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.CategoriesProductCard
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_product_rows_scroll_next_a11y
import vitranshop.shared.generated.resources.categories_product_rows_scroll_prev_a11y
import vitranshop.shared.generated.resources.categories_product_rows_section_open_a11y
import vitranshop.shared.generated.resources.ic_bold_right_chevron
import vitranshop.shared.generated.resources.ic_chevron_right

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
 * shop.app page column `space-y-space-40` between Explore / Browse / product rows.
 */
val CategoriesSectionGap = 40.dp

/** Tailwind-ish floors matching shop.app `--carousel-items-*`. */
private val CarouselSmMin = 640.dp
private val CarouselMdMin = 768.dp

/**
 * shop.app visible card count:
 * default 2.3 / sm 3 / md 4 / lg+ 6.
 */
private fun productCarouselVisibleCount(trackWidth: Dp, isDesktop: Boolean): Float = when {
    isDesktop -> 6f
    trackWidth >= CarouselMdMin -> 4f
    trackWidth >= CarouselSmMin -> 3f
    else -> 2.3f
}

/**
 * shop.app `--carousel-gap-default` 8 / `--carousel-gap-sm` 16.
 */
private fun productCarouselGap(trackWidth: Dp, isDesktop: Boolean): Dp =
    if (isDesktop || trackWidth >= CarouselSmMin) VitranSpacing.lg else VitranSpacing.sm

/**
 * shop.app `--carousel-item-size`:
 * `calc(100% / n - gap * (n - 1) / n)` ≡ `(track - gap * (n - 1)) / n`.
 */
private fun productCarouselCardWidth(trackWidth: Dp, visibleCount: Float, gap: Dp): Dp =
    (trackWidth - gap * (visibleCount - 1f)) / visibleCount

/**
 * Vertical stack of Categories product carousels (Top rated / New in).
 *
 * shop.app page column uses `space-y-space-40` between major blocks — 40dp
 * between every product row (and vs Browse / Explore).
 */
@Composable
fun CategoriesProductRowsFeed(
    sections: List<CategoriesProductRowSection>,
    modifier: Modifier = Modifier,
    onSectionClick: (CategoriesProductRowSection) -> Unit = {},
    onProductClick: (CategoriesProductRowSection, CategoriesProduct) -> Unit = { _, _ -> },
    onSaveClick: (CategoriesProductRowSection, CategoriesProduct) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CategoriesSectionGap),
    ) {
        sections.forEach { section ->
            CategoriesProductRow(
                section = section,
                onSectionClick = { onSectionClick(section) },
                onProductClick = { product -> onProductClick(section, product) },
                onSaveClick = { product -> onSaveClick(section, product) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * One section header + horizontal product carousel (shared by all Top rated /
 * New in rows on Categories).
 *
 * Measured tokens (shop.app 2026):
 * - Card width from track: `(track - gap*(n-1)) / n` — n = 2.3 / 3 / 4 / 6
 * - Gap 8 compact / 16 sm+; pad 16 compact / 48 desktop
 * - Header↔carousel 16; header 18sp / 20sp
 * - Desktop scroll chevrons centered on image band
 */
@Composable
fun CategoriesProductRow(
    section: CategoriesProductRowSection,
    modifier: Modifier = Modifier,
    onSectionClick: () -> Unit = {},
    onProductClick: (CategoriesProduct) -> Unit = {},
    onSaveClick: (CategoriesProduct) -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val horizontalPad = if (isDesktop) VitranSpacing.xxxl + VitranSpacing.lg else VitranSpacing.lg
    val endOverhang = if (isRtl) -ArrowOverhang else ArrowOverhang

    val headerBand = 22.dp
    val headerToCarousel = VitranSpacing.lg

    val showPrev = listState.canScrollBackward
    val showNext = listState.canScrollForward
    val openLabel = stringResource(
        Res.string.categories_product_rows_section_open_a11y,
        section.title,
    )
    val headerInteraction = remember { MutableInteractionSource() }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val visibleCount = productCarouselVisibleCount(maxWidth, isDesktop)
        val itemGap = productCarouselGap(maxWidth, isDesktop)
        val cardWidth = productCarouselCardWidth(maxWidth, visibleCount, itemGap)
        val scrollStepPx = with(density) { (cardWidth + itemGap).toPx() }
        val buttonTopInset =
            headerBand + headerToCarousel + (cardWidth - ScrollButtonSize) / 2

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPad)
                    .clickable(
                        interactionSource = headerInteraction,
                        indication = null,
                        role = Role.Button,
                        onClick = onSectionClick,
                    )
                    .semantics { contentDescription = openLabel }
                    .padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HeaderChevronGap),
            ) {
                Text(
                    text = section.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = if (isDesktop) {
                        MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 22.sp,
                        )
                    } else {
                        MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 22.sp,
                        )
                    },
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
                        items = section.products,
                        key = { it.id },
                    ) { product ->
                        CategoriesProductCard(
                            product = product,
                            cardWidth = cardWidth,
                            onClick = { onProductClick(product) },
                            onSaveClick = { onSaveClick(product) },
                        )
                    }
                }

                if (isDesktop && showPrev) {
                    ProductRowsScrollButton(
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
                    ProductRowsScrollButton(
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
}

@Composable
private fun ProductRowsScrollButton(
    forward: Boolean,
    isRtl: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val a11y = stringResource(
        if (forward) {
            Res.string.categories_product_rows_scroll_next_a11y
        } else {
            Res.string.categories_product_rows_scroll_prev_a11y
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

@Preview(showBackground = true, widthDp = 390, heightDp = 360)
@Composable
private fun CategoriesProductRowCompactPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides false) {
            CategoriesProductRow(
                section = rememberMockCategoriesProductRows().first(),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 1200, heightDp = 360)
@Composable
private fun CategoriesProductRowDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            CategoriesProductRow(
                section = rememberMockCategoriesProductRows().first(),
            )
        }
    }
}
