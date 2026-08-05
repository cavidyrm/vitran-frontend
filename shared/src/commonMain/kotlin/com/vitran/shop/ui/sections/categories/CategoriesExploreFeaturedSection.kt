package com.vitran.shop.ui.sections.categories

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_explore_edit_open_a11y
import vitranshop.shared.generated.resources.categories_explore_scroll_next_a11y
import vitranshop.shared.generated.resources.categories_explore_scroll_prev_a11y
import vitranshop.shared.generated.resources.categories_explore_title
import vitranshop.shared.generated.resources.ic_arrow_right
import vitranshop.shared.generated.resources.ic_chevron_right

/**
 * shop.app `aspect-ratio: 2.35 / 1` — landscape editorial strips
 * (≈392×167 on desktop). Height floors at [EditCardMinHeight].
 */
private val EditCardAspect = 2.35f

/** shop.app `min-height: 140px` on edit cards. */
private val EditCardMinHeight = 140.dp

/** shop.app `rounded-radius-28`. */
private val EditCardRadius = VitranRadius.extraLarge

/** shop.app overlay `p-space-20`. */
private val EditOverlayPad = VitranSpacing.xl

/** shop.app in-card arrow (`32×32`, `rgba(255,255,255,0.2)`). */
private val EditActionButtonSize = 32.dp
private val EditActionIconSize = 16.dp
private val EditActionButtonBg = Color.White.copy(alpha = 0.2f)

/** shop.app carousel scroll button (`42×42`). */
private val EditScrollButtonSize = 42.dp
private val EditScrollChevronSize = 20.dp

/** shop.app arrow overhang. */
private val EditArrowOverhang = VitranSpacing.lg

/**
 * shop.app `shadow-m` on carousel arrows: `0 4px 24px rgba(0,0,0,0.12)`.
 */
private val ArrowShadowElevation = 24.dp
private val ArrowShadowColor = Color.Black.copy(alpha = 0.12f)

/** shop.app `border-border-image` — `rgba(5, 41, 77, 0.1)`. */
private val ArrowBorder = Color(0x1A05294D)

/** Tailwind-ish floors matching shop.app explore `--carousel-items-*`. */
private val EditCarouselSmMin = 640.dp

/**
 * shop.app explore visible count: default 1.3 / sm 2 / md+ 3.
 */
private fun exploreCarouselVisibleCount(trackWidth: Dp, isDesktop: Boolean): Float = when {
    isDesktop -> 3f
    trackWidth >= EditCarouselSmMin -> 2f
    else -> 1.3f
}

/**
 * shop.app `--carousel-gap-default` 8 / `--carousel-gap-sm` 16.
 */
private fun exploreCarouselGap(trackWidth: Dp, isDesktop: Boolean): Dp =
    if (isDesktop || trackWidth >= EditCarouselSmMin) VitranSpacing.lg else VitranSpacing.sm

/**
 * shop.app `--carousel-item-size` on the padded content track (compact) /
 * full content track (desktop).
 */
private fun exploreCarouselCardWidth(trackWidth: Dp, visibleCount: Float, gap: Dp): Dp =
    (trackWidth - gap * (visibleCount - 1f)) / visibleCount

/**
 * Categories top section: “Explore” / «کاوش» H1 + editorial edit cards carousel
 * (shop.app `/categories`).
 *
 * Measured (shop.app 2026 live):
 * - H1: mobile 28/30 start; desktop 36/38 centered
 * - H1→carousel: 20 compact / 36 desktop
 * - Cards: width from track `(track - gap*(n-1)) / n` — n = 1.3 / 2 / 3;
 *   aspect 2.35/1 with min-height 140; radius 28
 * - Section bottom spacing owned by [CategoriesScreen] (`space-y-40`)
 */
@Composable
fun CategoriesExploreFeaturedSection(
    edits: List<ExploreEdit>,
    modifier: Modifier = Modifier,
    onEditClick: (ExploreEdit) -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val horizontalPad = if (isDesktop) {
        VitranSpacing.xxxl + VitranSpacing.lg
    } else {
        VitranSpacing.lg
    }
    val topPad = if (isDesktop) VitranSpacing.xxxl + VitranSpacing.sm else VitranSpacing.xl
    // shop.app: compact ~20; desktop ~36.
    val titleToCarouselGap = if (isDesktop) 36.dp else VitranSpacing.xl
    val endOverhang = if (isRtl) -EditArrowOverhang else EditArrowOverhang
    // shop.app: mobile `text-header` 28/30; desktop `text-posterXS` 36/38.
    val titleFontSize = if (isDesktop) 36.sp else 28.sp
    val titleLineHeight = if (isDesktop) 38.sp else 30.sp
    val titleBlockHeight = if (isDesktop) 38.dp else 30.dp

    val showPrev = listState.canScrollBackward
    val showNext = listState.canScrollForward
    val title = stringResource(Res.string.categories_explore_title)

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // Compact: shop sizes items against the padded content track.
        // Desktop: track is the full content width (page margin already outside).
        val trackWidth = if (isDesktop) maxWidth else maxWidth - horizontalPad * 2
        val visibleCount = exploreCarouselVisibleCount(trackWidth, isDesktop)
        val itemGap = exploreCarouselGap(trackWidth, isDesktop)
        val cardWidth = exploreCarouselCardWidth(trackWidth, visibleCount, itemGap)
        val cardHeight = max(cardWidth / EditCardAspect, EditCardMinHeight)
        val scrollStepPx = with(density) { (cardWidth + itemGap).toPx() }
        val buttonTopInset =
            topPad + titleBlockHeight + titleToCarouselGap +
                (cardHeight - EditScrollButtonSize) / 2

        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Medium,
                        lineHeight = titleLineHeight,
                        letterSpacing = if (isDesktop) (-1).sp else (-0.5).sp,
                    ),
                    textAlign = if (isDesktop) TextAlign.Center else TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPad)
                        .padding(top = topPad),
                )

                Spacer(modifier = Modifier.height(titleToCarouselGap))

                LazyRow(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = horizontalPad),
                    horizontalArrangement = Arrangement.spacedBy(itemGap),
                ) {
                    items(
                        items = edits,
                        key = { it.id },
                    ) { edit ->
                        ExploreEditCard(
                            edit = edit,
                            cardWidth = cardWidth,
                            cardHeight = cardHeight,
                            onClick = { onEditClick(edit) },
                        )
                    }
                }
            }

            if (showPrev) {
                ExploreScrollButton(
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
                ExploreScrollButton(
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
private fun ExploreEditCard(
    edit: ExploreEdit,
    cardWidth: Dp,
    cardHeight: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val openLabel = stringResource(Res.string.categories_explore_edit_open_a11y, edit.title)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val placeholder = ColorPainter(edit.placeholderColor)
    val shape = RoundedCornerShape(EditCardRadius)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val imageScale by animateFloatAsState(
        targetValue = if (hovered) {
            VitranAnimation.CategoriesCard.IMAGE_HOVER_SCALE
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = VitranAnimation.CategoriesCard.IMAGE_HOVER_MS,
            easing = VitranAnimation.CategoriesCard.ImageHoverEasing,
        ),
        label = "exploreEditHover",
    )

    Box(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(shape)
            .background(edit.placeholderColor)
            .hoverable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = openLabel },
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(edit.imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = imageScale
                    scaleY = imageScale
                },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(cardHeight * 0.7f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.55f),
                        ),
                    ),
                ),
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(EditOverlayPad),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = VitranSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                Text(
                    text = edit.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = edit.subtitle,
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 18.sp,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Box(
                modifier = Modifier
                    .size(EditActionButtonSize)
                    .clip(CircleShape)
                    .background(EditActionButtonBg),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
                    size = EditActionIconSize,
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun ExploreScrollButton(
    forward: Boolean,
    isRtl: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val a11y = stringResource(
        if (forward) {
            Res.string.categories_explore_scroll_next_a11y
        } else {
            Res.string.categories_explore_scroll_prev_a11y
        },
    )
    val flipForRtl = when {
        forward && isRtl -> true
        !forward && !isRtl -> true
        else -> false
    }
    Box(
        modifier = modifier
            .size(EditScrollButtonSize)
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
            size = EditScrollChevronSize,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun CategoriesExploreFeaturedCompactPreview() {
    VitranTheme {
        CategoriesExploreFeaturedSection(edits = rememberMockExploreEdits())
    }
}

@Preview(showBackground = true, widthDp = 1280)
@Composable
private fun CategoriesExploreFeaturedDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            CategoriesExploreFeaturedSection(edits = rememberMockExploreEdits())
        }
    }
}
