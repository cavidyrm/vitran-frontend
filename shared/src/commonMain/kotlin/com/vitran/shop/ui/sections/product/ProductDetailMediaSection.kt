package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.shell.LocalShellViewportHeight
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_merchant_image_next_a11y
import vitranshop.shared.generated.resources.categories_merchant_image_prev_a11y
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.product_detail_media_open_a11y

/**
 * shop.app PDP media gallery — three layouts by width (not chrome only):
 *
 * - Compact (`< md`): horizontal snap carousel (`h-[45vh]`), drag/fling.
 * - Medium (`md`..<`lg`): tall preview on top + horizontal thumbs + always-on
 *   prev/next beside the thumb row (`flex-col`, `block lg:hidden`).
 * - Large (`≥ lg`): vertical thumbs + square preview; hover prev/next on image
 *   (`flex-row-reverse`, `hidden lg:block`).
 *
 * Clicking the main image (or a compact slide) opens [ProductDetailMediaLightbox]
 * — shop.app `cursor-zoom-in` fullscreen viewer — on every breakpoint.
 *
 * First section on Product Detail — top inset only (no prior section gap).
 */
private val CompactGalleryHeightFraction = 0.45f
private val CompactTopInset = VitranSpacing.lg
private val CompactStartPad = VitranSpacing.lg
private val CompactEndPeek = VitranSpacing.xl
private val CompactPageSpacing = VitranSpacing.sm
private val CompactSlideInnerPad = VitranSpacing.xs
private val CompactImageRadius = VitranRadius.small

private val MediumTopInset = VitranSpacing.lg
private val MediumHorizontalPad = VitranSpacing.lg
private val MediumPreviewRadius = VitranRadius.medium
/** shop.app medium preview column is tall; image `object-contain` inside. */
private val MediumPreviewHeightFraction = 0.65f
private val MediumPreviewMaxHeight = 760.dp
private val MediumThumbsToPreviewGap = VitranSpacing.lg
private val MediumThumbNavGap = VitranSpacing.xs
private val MediumNavPairGap = VitranSpacing.xs

private val DesktopTopInset = 32.dp
private val DesktopThumbSize = 48.dp
private val DesktopThumbGap = 6.dp
private val DesktopThumbToPreviewGap = VitranSpacing.lg
private val DesktopThumbRadius = VitranRadius.small
private val DesktopPreviewRadius = VitranRadius.extraLarge
private val DesktopPreviewMax = 760.dp
private val DesktopHorizontalPad = VitranSpacing.xxxl
/** shop.app `md:h-[65vh]` / `--carousel-height`. */
private val DesktopPreviewHeightFraction = 0.65f

private val PreviewShadowElevation = 2.dp
private val PreviewShadowColor = Color.Black.copy(alpha = 0.06f)

private val ThumbRail = Color.Black.copy(alpha = 0.04f)
/** Soft fill behind contain/fit photography (medium + desktop letterboxing). */
private val PreviewRail = Color(0xFFF2F2F2)

private val GalleryNavButtonSize = 44.dp
private val GalleryNavChevronSize = 20.dp
private val GalleryNavInset = VitranSpacing.md
private val GalleryNavShadowElevation = 24.dp
private val GalleryNavShadowColor = Color.Black.copy(alpha = 0.12f)

private enum class GalleryBreakpoint {
    Compact,
    Medium,
    Desktop,
}

@Composable
fun ProductDetailMediaSection(
    media: ProductDetailMedia,
    modifier: Modifier = Modifier,
) {
    var selectedIndex by remember(media.imageUrls) { mutableIntStateOf(0) }
    var lightboxOpen by remember(media.imageUrls) { mutableStateOf(false) }
    val safeIndex = selectedIndex.coerceIn(0, media.imageUrls.lastIndex)

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val breakpoint = when {
            maxWidth >= VitranSize.desktopBreakpoint -> GalleryBreakpoint.Desktop
            maxWidth >= VitranSize.mdBreakpoint -> GalleryBreakpoint.Medium
            else -> GalleryBreakpoint.Compact
        }

        when (breakpoint) {
            GalleryBreakpoint.Desktop -> DesktopMediaGallery(
                imageUrls = media.imageUrls,
                selectedIndex = safeIndex,
                onSelect = { selectedIndex = it },
                onOpenLightbox = { lightboxOpen = true },
            )
            GalleryBreakpoint.Medium -> MediumMediaGallery(
                imageUrls = media.imageUrls,
                selectedIndex = safeIndex,
                onSelect = { selectedIndex = it },
                onOpenLightbox = { lightboxOpen = true },
            )
            GalleryBreakpoint.Compact -> CompactMediaCarousel(
                imageUrls = media.imageUrls,
                onOpenLightbox = { index ->
                    selectedIndex = index
                    lightboxOpen = true
                },
            )
        }
    }

    if (lightboxOpen) {
        ProductDetailMediaLightbox(
            imageUrls = media.imageUrls,
            selectedIndex = safeIndex,
            onSelect = { selectedIndex = it },
            onDismiss = { lightboxOpen = false },
        )
    }
}

/**
 * Mobile / compact: snap [LazyRow] with peek — works for touch and mouse drag on web.
 * Prefer LazyRow over HorizontalPager so nested vertical page scroll does not eat
 * horizontal drags as easily on Wasm/JS.
 */
@Composable
private fun CompactMediaCarousel(
    imageUrls: List<String>,
    onOpenLightbox: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewportHeight = LocalShellViewportHeight.current
    val galleryHeight = viewportHeight * CompactGalleryHeightFraction
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val openA11y = stringResource(Res.string.product_detail_media_open_a11y)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = CompactTopInset),
    ) {
        val slideWidth = (maxWidth - CompactStartPad - CompactEndPeek).coerceAtLeast(120.dp)
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .height(galleryHeight),
            contentPadding = PaddingValues(
                start = CompactStartPad,
                end = CompactEndPeek,
            ),
            horizontalArrangement = Arrangement.spacedBy(CompactPageSpacing),
            verticalAlignment = Alignment.CenterVertically,
            userScrollEnabled = true,
        ) {
            itemsIndexed(
                items = imageUrls,
                key = { index, url -> "c-$index-$url" },
            ) { index, url ->
                val interaction = remember(index) { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .width(slideWidth)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = CompactSlideInnerPad)
                        .clip(RoundedCornerShape(CompactImageRadius))
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Button,
                            onClick = { onOpenLightbox(index) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = resolveNetworkImageUrl(url),
                        contentDescription = openA11y,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

/**
 * Medium (`md`..<`lg`): preview stacked above horizontal thumbs + always-visible nav.
 */
@Composable
private fun MediumMediaGallery(
    imageUrls: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onOpenLightbox: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewportHeight = LocalShellViewportHeight.current
    val previewHeight = min(
        viewportHeight * MediumPreviewHeightFraction,
        MediumPreviewMaxHeight,
    )
    val thumbsState = rememberLazyListState()
    val previewInteraction = remember { MutableInteractionSource() }
    val openA11y = stringResource(Res.string.product_detail_media_open_a11y)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MediumTopInset)
            .padding(horizontal = MediumHorizontalPad),
        verticalArrangement = Arrangement.spacedBy(MediumThumbsToPreviewGap),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight)
                .clip(RoundedCornerShape(MediumPreviewRadius))
                .background(PreviewRail)
                .clickable(
                    interactionSource = previewInteraction,
                    indication = null,
                    role = Role.Button,
                    onClick = onOpenLightbox,
                ),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(imageUrls[selectedIndex]),
                contentDescription = openA11y,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MediumThumbNavGap),
        ) {
            LazyRow(
                state = thumbsState,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(DesktopThumbGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(
                    items = imageUrls,
                    key = { index, url -> "mt-$index-$url" },
                ) { index, url ->
                    GalleryThumbnail(
                        imageUrl = url,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                    )
                }
            }

            if (imageUrls.size > 1) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MediumNavPairGap),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GalleryNavButton(
                        forward = false,
                        enabled = selectedIndex > 0,
                        onClick = { onSelect(selectedIndex - 1) },
                    )
                    GalleryNavButton(
                        forward = true,
                        enabled = selectedIndex < imageUrls.lastIndex,
                        onClick = { onSelect(selectedIndex + 1) },
                    )
                }
            }
        }
    }
}

/**
 * Large (`≥ lg`): vertical thumbs + square preview sized by width AND ~65vh.
 */
@Composable
private fun DesktopMediaGallery(
    imageUrls: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onOpenLightbox: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewportHeight = LocalShellViewportHeight.current
    val previewInteraction = remember { MutableInteractionSource() }
    val previewHovered by previewInteraction.collectIsHoveredAsState()
    val showNav = previewHovered && imageUrls.size > 1
    val openA11y = stringResource(Res.string.product_detail_media_open_a11y)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = DesktopTopInset)
            .padding(horizontal = DesktopHorizontalPad),
    ) {
        val widthBudget = (maxWidth - DesktopThumbSize - DesktopThumbToPreviewGap)
            .coerceAtLeast(200.dp)
        val heightBudget = min(
            viewportHeight * DesktopPreviewHeightFraction,
            DesktopPreviewMax,
        )
        val previewSize = min(widthBudget, heightBudget)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(previewSize),
            horizontalArrangement = Arrangement.spacedBy(DesktopThumbToPreviewGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Fixed height + LazyColumn: strip matches preview and scrolls when
            // thumbs overflow (page itself is LazyColumn, so nesting is orthogonal).
            LazyColumn(
                modifier = Modifier
                    .width(DesktopThumbSize)
                    .height(previewSize),
                verticalArrangement = Arrangement.spacedBy(
                    space = DesktopThumbGap,
                    alignment = Alignment.CenterVertically,
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(
                    items = imageUrls,
                    key = { index, url -> "dt-$index-$url" },
                ) { index, url ->
                    GalleryThumbnail(
                        imageUrl = url,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(previewSize)
                    .hoverable(previewInteraction)
                    .shadow(
                        elevation = PreviewShadowElevation,
                        shape = RoundedCornerShape(DesktopPreviewRadius),
                        clip = false,
                        ambientColor = PreviewShadowColor,
                        spotColor = PreviewShadowColor,
                    )
                    .clip(RoundedCornerShape(DesktopPreviewRadius))
                    .background(PreviewRail),
            ) {
                AsyncImage(
                    model = resolveNetworkImageUrl(imageUrls[selectedIndex]),
                    contentDescription = openA11y,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = previewInteraction,
                            indication = null,
                            role = Role.Button,
                            onClick = onOpenLightbox,
                        ),
                    contentScale = ContentScale.Fit,
                )

                if (showNav) {
                    GalleryNavButton(
                        forward = false,
                        enabled = selectedIndex > 0,
                        onClick = { onSelect(selectedIndex - 1) },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = GalleryNavInset),
                    )
                    GalleryNavButton(
                        forward = true,
                        enabled = selectedIndex < imageUrls.lastIndex,
                        onClick = { onSelect(selectedIndex + 1) },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = GalleryNavInset),
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryThumbnail(
    imageUrl: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val borderColor =
        if (selected) MaterialTheme.colorScheme.onSurface else Color.Transparent
    Box(
        modifier = modifier
            .size(DesktopThumbSize)
            .clip(RoundedCornerShape(DesktopThumbRadius))
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(DesktopThumbRadius),
            )
            .background(ThumbRail)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(DesktopThumbRadius - 2.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

/**
 * shop.app PDP “Previous image” / “Next image”: white circle, `shadow-m`, 44dp.
 */
@Composable
private fun GalleryNavButton(
    forward: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val a11y = stringResource(
        if (forward) {
            Res.string.categories_merchant_image_next_a11y
        } else {
            Res.string.categories_merchant_image_prev_a11y
        },
    )
    val flipForRtl = when {
        forward && isRtl -> true
        !forward && !isRtl -> true
        else -> false
    }
    Box(
        modifier = modifier
            .size(GalleryNavButtonSize)
            .shadow(
                elevation = GalleryNavShadowElevation,
                shape = CircleShape,
                clip = false,
                ambientColor = GalleryNavShadowColor,
                spotColor = GalleryNavShadowColor,
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .graphicsLayer { alpha = if (enabled) 1f else 0.4f },
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = a11y,
            modifier = Modifier.graphicsLayer { scaleX = if (flipForRtl) -1f else 1f },
            size = GalleryNavChevronSize,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}
