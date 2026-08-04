package com.vitran.shop.ui.sections.home

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
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
import vitranshop.shared.generated.resources.home_category_mosaic_open_a11y
import vitranshop.shared.generated.resources.home_category_mosaics_scroll_next_a11y
import vitranshop.shared.generated.resources.home_category_mosaics_scroll_prev_a11y
import vitranshop.shared.generated.resources.ic_bold_right_chevron
import vitranshop.shared.generated.resources.ic_chevron_right

/**
 * shop.app mosaic card width (`w-[330px]`) — fixed across compact / xl until xxl.
 */
private val MosaicCardWidth = 330.dp

/** shop.app `gap-space-2` between 2×2 tiles. */
private val MosaicTileGap = 2.dp

/** shop.app header title↔chevron `gap-space-8`. */
private val MosaicHeaderGap = VitranSpacing.sm

/**
 * shop.app header chevron chip: `w-screen-margin` circle + `bg-bg-fill-secondary`.
 */
private val MosaicHeaderChevronChip = 16.dp

/** Glyph size inside the 16dp chip (shop.app svg styled 20px, clipped by circle). */
private val MosaicHeaderChevronGlyph = 12.dp

/** shop.app carousel scroll button (`42×42`). */
private val MosaicScrollButtonSize = 42.dp

/** shop.app chevron glyph inside scroll button. */
private val MosaicScrollChevronSize = 20.dp

/** One-card scroll step for mosaic chevrons (card + desktop gap). */
private val MosaicScrollStep = MosaicCardWidth + VitranSpacing.lg

/** shop.app arrow overhang (`-right-space-16` / `-left-space-16`). */
private val MosaicArrowOverhang = VitranSpacing.lg

/**
 * Extra space above mosaics so pills→title ≈ 36dp on desktop
 * (pills LazyRow already contributes 16dp bottom pad).
 */
private val MosaicSectionTopPad = VitranSpacing.xl

/**
 * shop.app `border-border-image` — `rgba(5, 41, 77, 0.1)`.
 */
private val MosaicBorder = Color(0x1A05294D)

/**
 * Soft mosaic card shadow — shop.app `shadow-md`:
 * `0 4px 6px -1px / 0 2px 4px -2px` at `rgba(0,0,0,0.1)`.
 * Kept lighter than Material elevation medium.
 */
private val MosaicCardShadowElevation = 4.dp
private val MosaicCardShadowColor = Color.Black.copy(alpha = 0.08f)

/**
 * shop.app `shadow-m` on carousel arrows: `0 4px 24px rgba(0,0,0,0.12)`.
 */
private val ArrowShadowElevation = 24.dp
private val ArrowShadowColor = Color.Black.copy(alpha = 0.12f)

/**
 * Horizontal L1 category mosaic carousel under the Home pills (shop.app).
 *
 * Measured tokens (shop.app 2026):
 * - Card width 330; tile gap 2; header↔grid mt 12 compact / 16 desktop
 * - Grid aspect 1:1 compact; 374/340 desktop; radius 20 / 28
 * - Carousel pad 16 compact / 48 desktop; gap 8 / 16
 * - Tile label white, 12sp / 14sp, margin 8 / 12, bottom-start
 * - Header chevron: 16dp gray circle + bold-right-chevron
 * - Tile hover: image scale 1.1 / 150ms; title click has no ripple fill
 * - Card shadow: soft 4dp @ 8% black (shop.app shadow-md)
 */
@Composable
fun HomeCategoryMosaicsRow(
    mosaics: List<HomeCategoryMosaic>,
    modifier: Modifier = Modifier,
    onCategoryClick: (HomeCategoryMosaic) -> Unit = {},
    onTileClick: (HomeCategoryMosaic, HomeCategoryMosaicTile) -> Unit = { _, _ -> },
) {
    val isDesktop = LocalDesktopLayout.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val horizontalPad = if (isDesktop) VitranSpacing.xxxl + VitranSpacing.lg else VitranSpacing.lg
    // shop.app: compact ~8px (`gap-space-8`); desktop ~16px at typical widths.
    val itemGap = if (isDesktop) VitranSpacing.lg else VitranSpacing.sm
    val bottomPad = if (isDesktop) 38.dp else VitranSpacing.xxxl
    val scrollStepPx = with(density) { MosaicScrollStep.toPx() }
    val endOverhang = if (isRtl) -MosaicArrowOverhang else MosaicArrowOverhang

    // shop.app: arrows vertically centered on the mosaic grid band.
    val headerHeight = 22.dp
    val gridTopInset = if (isDesktop) VitranSpacing.lg else VitranSpacing.md
    val gridHeight = if (isDesktop) {
        MosaicCardWidth * (340f / 374f)
    } else {
        MosaicCardWidth
    }
    val buttonTopInset =
        MosaicSectionTopPad + headerHeight + gridTopInset +
            (gridHeight - MosaicScrollButtonSize) / 2

    val showPrev = listState.canScrollBackward
    val showNext = listState.canScrollForward

    Box(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = horizontalPad,
                end = horizontalPad,
                top = MosaicSectionTopPad,
                bottom = bottomPad,
            ),
            horizontalArrangement = Arrangement.spacedBy(itemGap),
        ) {
            items(
                items = mosaics,
                key = { it.id },
            ) { mosaic ->
                HomeCategoryMosaicCard(
                    mosaic = mosaic,
                    isDesktop = isDesktop,
                    onCategoryClick = { onCategoryClick(mosaic) },
                    onTileClick = { tile -> onTileClick(mosaic, tile) },
                )
            }
        }

        if (showPrev) {
            MosaicScrollButton(
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
            MosaicScrollButton(
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
private fun HomeCategoryMosaicCard(
    mosaic: HomeCategoryMosaic,
    isDesktop: Boolean,
    onCategoryClick: () -> Unit,
    onTileClick: (HomeCategoryMosaicTile) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridTop = if (isDesktop) VitranSpacing.lg else VitranSpacing.md
    val gridRadius = if (isDesktop) VitranRadius.extraLarge else VitranRadius.xl
    val gridAspect = if (isDesktop) 374f / 340f else 1f
    val openLabel = stringResource(Res.string.home_category_mosaic_open_a11y, mosaic.title)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val headerInteraction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.width(MosaicCardWidth),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = headerInteraction,
                    indication = null,
                    role = Role.Button,
                    onClick = onCategoryClick,
                )
                .semantics { contentDescription = openLabel }
                .padding(vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MosaicHeaderGap),
        ) {
            Text(
                text = mosaic.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = if (isDesktop) {
                    MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp,
                    )
                } else {
                    MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp,
                    )
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            // shop.app: gray circular chip + icon-bold-right-chevron
            Box(
                modifier = Modifier
                    .size(MosaicHeaderChevronChip)
                    .clip(CircleShape)
                    .background(MosaicTilePlaceholder),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_bold_right_chevron),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
                    size = MosaicHeaderChevronGlyph,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(modifier = Modifier.height(gridTop))

        Box(
            modifier = Modifier
                .width(MosaicCardWidth)
                .aspectRatio(gridAspect)
                .shadow(
                    elevation = MosaicCardShadowElevation,
                    shape = RoundedCornerShape(gridRadius),
                    clip = false,
                    ambientColor = MosaicCardShadowColor,
                    spotColor = MosaicCardShadowColor,
                )
                .clip(RoundedCornerShape(gridRadius))
                .background(MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(MosaicTileGap),
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MosaicTileGap),
                ) {
                    MosaicTile(
                        tile = mosaic.tiles[0],
                        isDesktop = isDesktop,
                        onClick = { onTileClick(mosaic.tiles[0]) },
                        modifier = Modifier.weight(1f),
                    )
                    MosaicTile(
                        tile = mosaic.tiles[1],
                        isDesktop = isDesktop,
                        onClick = { onTileClick(mosaic.tiles[1]) },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MosaicTileGap),
                ) {
                    MosaicTile(
                        tile = mosaic.tiles[2],
                        isDesktop = isDesktop,
                        onClick = { onTileClick(mosaic.tiles[2]) },
                        modifier = Modifier.weight(1f),
                    )
                    MosaicTile(
                        tile = mosaic.tiles[3],
                        isDesktop = isDesktop,
                        onClick = { onTileClick(mosaic.tiles[3]) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MosaicTile(
    tile: HomeCategoryMosaicTile,
    isDesktop: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelPad = if (isDesktop) VitranSpacing.md else VitranSpacing.sm
    val labelStyle = if (isDesktop) {
        MaterialTheme.typography.labelLarge.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp,
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.35f),
                offset = Offset(0f, 1f),
                blurRadius = 2f,
            ),
        )
    } else {
        MaterialTheme.typography.labelMedium.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 14.sp,
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.35f),
                offset = Offset(0f, 1f),
                blurRadius = 2f,
            ),
        )
    }
    val placeholder = ColorPainter(tile.placeholderColor)
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val imageScale by animateFloatAsState(
        targetValue = if (hovered) {
            VitranAnimation.CategoryMosaic.TILE_HOVER_SCALE
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = VitranAnimation.CategoryMosaic.TILE_HOVER_MS,
            easing = VitranAnimation.CategoryMosaic.TileHoverEasing,
        ),
        label = "mosaicTileHover",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .background(tile.placeholderColor)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(tile.imageUrl),
            contentDescription = tile.title,
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
        Text(
            text = tile.title,
            color = Color.White,
            style = labelStyle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(labelPad)
                .zIndex(1f),
        )
    }
}

@Composable
private fun MosaicScrollButton(
    forward: Boolean,
    isRtl: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val a11y = stringResource(
        if (forward) {
            Res.string.home_category_mosaics_scroll_next_a11y
        } else {
            Res.string.home_category_mosaics_scroll_prev_a11y
        },
    )
    val flipForRtl = when {
        forward && isRtl -> true
        !forward && !isRtl -> true
        else -> false
    }
    Box(
        modifier = modifier
            .size(MosaicScrollButtonSize)
            .shadow(
                elevation = ArrowShadowElevation,
                shape = CircleShape,
                clip = false,
                ambientColor = ArrowShadowColor,
                spotColor = ArrowShadowColor,
            )
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .border(width = 1.dp, color = MosaicBorder, shape = CircleShape)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = a11y,
            modifier = Modifier.graphicsLayer { scaleX = if (flipForRtl) -1f else 1f },
            size = MosaicScrollChevronSize,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun HomeCategoryMosaicsRowCompactPreview() {
    VitranTheme {
        HomeCategoryMosaicsRow(mosaics = rememberMockHomeCategoryMosaics())
    }
}

@Preview(showBackground = true, widthDp = 1280)
@Composable
private fun HomeCategoryMosaicsRowDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            HomeCategoryMosaicsRow(mosaics = rememberMockHomeCategoryMosaics())
        }
    }
}
