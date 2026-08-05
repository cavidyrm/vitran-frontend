package com.vitran.shop.ui.sections.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranShapes
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.home_categories_scroll_next_a11y
import vitranshop.shared.generated.resources.home_categories_scroll_prev_a11y
import vitranshop.shared.generated.resources.ic_chevron_right

/** shop.app category pill height (`h-space-44`). */
private val CategoryPillHeight = 44.dp

/** shop.app icon circle (`size-space-32`). */
private val CategoryIconSize = 32.dp

/** shop.app pill inner start / vertical padding (`p-space-6`). */
private val CategoryPillPad = 6.dp

/** shop.app carousel scroll button (`42×42`). */
private val CategoryScrollButtonSize = 42.dp

/** shop.app chevron glyph size inside the scroll button (`20px`). */
private val CategoryScrollChevronSize = 20.dp

/** Approximate one-pill scroll step for scroll chevrons. */
private val CategoryScrollStep = 140.dp

/** shop.app arrow overhang (`-right-space-16` / `-left-space-16`). */
private val CategoryArrowOverhang = VitranSpacing.lg

/**
 * shop.app `border-border-image` — `rgba(5, 41, 77, 0.1)`.
 */
private val CategoryBorder = Color(0x1A05294D)

/**
 * shop.app `shadow-m` on carousel arrows: `0 4px 24px rgba(0,0,0,0.12)`.
 */
private val ArrowShadowElevation = 24.dp
private val ArrowShadowColor = Color.Black.copy(alpha = 0.12f)

/**
 * Horizontal L1 category pills under the Home hero (shop.app).
 *
 * Scroll chevrons show whenever the row can scroll (desktop and compact) —
 * not gated on a width breakpoint. Vertically aligned to the pill band.
 */
@Composable
fun HomeCategoriesRow(
    categories: List<HomeCategory>,
    modifier: Modifier = Modifier,
    onCategoryClick: (HomeCategory) -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val horizontalPad = if (isDesktop) VitranSpacing.xxxl + VitranSpacing.lg else VitranSpacing.lg
    val scrollStepPx = with(density) { CategoryScrollStep.toPx() }
    val endOverhang = if (isRtl) -CategoryArrowOverhang else CategoryArrowOverhang
    // shop.app measured: desktop search→pill ≈ 38, compact ≈ 30.
    val topPad = if (isDesktop) 36.dp else VitranSpacing.xxxl
    // shop.app shelf `mb-space-6` compact / `lg:mb-space-20` — contributes to pills→mosaic gap.
    val bottomPad = if (isDesktop) VitranSpacing.xl else 6.dp
    // Center the 42dp button on the 44dp pill band (below topPad).
    val buttonTopInset =
        topPad + (CategoryPillHeight - CategoryScrollButtonSize) / 2

    val showPrev = listState.canScrollBackward
    val showNext = listState.canScrollForward

    Box(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = horizontalPad,
                end = horizontalPad,
                top = topPad,
                bottom = bottomPad,
            ),
            horizontalArrangement = Arrangement.spacedBy(
                space = VitranSpacing.sm,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(
                items = categories,
                key = { it.id },
            ) { category ->
                HomeCategoryPill(
                    category = category,
                    onClick = { onCategoryClick(category) },
                )
            }
        }

        if (showPrev) {
            CategoryScrollButton(
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
        if (showNext) {
            CategoryScrollButton(
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

@Composable
private fun HomeCategoryPill(
    category: HomeCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = VitranShapes.pill
    Row(
        modifier = modifier
            .height(CategoryPillHeight)
            .categorySoftShadow()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(width = 1.dp, color = CategoryBorder, shape = shape)
            .clip(shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(
                start = CategoryPillPad,
                top = CategoryPillPad,
                end = VitranSpacing.lg,
                bottom = CategoryPillPad,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(category.imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd,
            modifier = Modifier
                .size(CategoryIconSize)
                .clip(CircleShape)
                .border(width = 1.dp, color = CategoryBorder, shape = CircleShape)
                .background(Color(0xFFF2F2F2)),
        )
        Text(
            text = category.title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
            maxLines = 1,
            softWrap = false,
        )
    }
}

/**
 * Approximates shop.app `shadow-s`: `0 2px 8px rgba(0,0,0,0.06)` — kept soft.
 */
private fun Modifier.categorySoftShadow(): Modifier = drawBehind {
    val radius = size.height / 2f
    val yOffset = 2.dp.toPx()
    for (i in 1..5) {
        val spread = i * 1.dp.toPx()
        val alpha = 0.014f * (1f - (i - 1) / 5f)
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = -spread * 0.3f,
                    top = yOffset - spread * 0.1f,
                    right = size.width + spread * 0.3f,
                    bottom = size.height + yOffset + spread * 0.85f,
                    cornerRadius = CornerRadius(radius + spread * 0.2f),
                ),
            )
        }
        drawPath(path = path, color = Color.Black.copy(alpha = alpha), style = Fill)
    }
}

@Composable
private fun CategoryScrollButton(
    forward: Boolean,
    isRtl: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val a11y = stringResource(
        if (forward) {
            Res.string.home_categories_scroll_next_a11y
        } else {
            Res.string.home_categories_scroll_prev_a11y
        },
    )
    val flipForRtl = when {
        forward && isRtl -> true
        !forward && !isRtl -> true
        else -> false
    }
    Box(
        modifier = modifier
            .size(CategoryScrollButtonSize)
            .shadow(
                elevation = ArrowShadowElevation,
                shape = CircleShape,
                clip = false,
                ambientColor = ArrowShadowColor,
                spotColor = ArrowShadowColor,
            )
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .border(width = 1.dp, color = CategoryBorder, shape = CircleShape)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = a11y,
            modifier = Modifier.graphicsLayer { scaleX = if (flipForRtl) -1f else 1f },
            size = CategoryScrollChevronSize,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun HomeCategoriesRowCompactPreview() {
    VitranTheme {
        HomeCategoriesRow(categories = rememberMockHomeCategories())
    }
}

@Preview(showBackground = true, widthDp = 900)
@Composable
private fun HomeCategoriesRowDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            HomeCategoriesRow(categories = rememberMockHomeCategories())
        }
    }
}
