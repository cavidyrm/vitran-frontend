package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_merchant_image_next_a11y
import vitranshop.shared.generated.resources.categories_merchant_image_prev_a11y
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_close
import vitranshop.shared.generated.resources.product_detail_media_close_a11y

/**
 * shop.app PDP fullscreen media viewer (`cursor-zoom-in` → dialog).
 *
 * - Compact: full-bleed white stage, centered contain image, snap swipe + prev/next.
 * - Medium/Desktop: inset card (`m-space-8`, `rounded-radius-20`, `border-border-image`)
 *   with the same gallery chrome (thumbs + large preview).
 */
private val LightboxInset = VitranSpacing.sm
private val LightboxPad = VitranSpacing.md
private val LightboxRadius = VitranRadius.xl
/** shop.app `border-border-image` — `rgba(5, 41, 77, 0.1)`. */
private val LightboxBorder = Color(0x1A05294D)
private val LightboxPreviewRail = Color(0xFFF2F2F2)
private val LightboxThumbSize = 48.dp
private val LightboxThumbGap = 6.dp
private val LightboxThumbRadius = VitranRadius.small
private val LightboxThumbRail = Color.Black.copy(alpha = 0.04f)
private val LightboxCloseSize = 44.dp
private val LightboxCloseGlyph = 20.dp
private val LightboxNavSize = 44.dp
private val LightboxNavGlyph = 20.dp
private val LightboxNavShadowElevation = 24.dp
private val LightboxNavShadowColor = Color.Black.copy(alpha = 0.12f)
private val LightboxPreviewShadowElevation = 2.dp
private val LightboxPreviewShadowColor = Color.Black.copy(alpha = 0.06f)
private val LightboxDesktopPreviewRadius = VitranRadius.extraLarge

@Composable
fun ProductDetailMediaLightbox(
    imageUrls: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val safeIndex = selectedIndex.coerceIn(0, imageUrls.lastIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            val isCompact = maxWidth < VitranSize.mdBreakpoint
            if (isCompact) {
                CompactLightboxBody(
                    imageUrls = imageUrls,
                    selectedIndex = safeIndex,
                    onSelect = onSelect,
                    onDismiss = onDismiss,
                )
            } else {
                ExpandedLightboxBody(
                    imageUrls = imageUrls,
                    selectedIndex = safeIndex,
                    onSelect = onSelect,
                    onDismiss = onDismiss,
                    useVerticalThumbs = maxWidth >= VitranSize.desktopBreakpoint,
                )
            }
        }
    }
}

@Composable
private fun CompactLightboxBody(
    imageUrls: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState.firstVisibleItemIndex, listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val page = listState.firstVisibleItemIndex.coerceIn(0, imageUrls.lastIndex)
            if (page != selectedIndex) onSelect(page)
        }
    }
    LaunchedEffect(selectedIndex) {
        if (listState.firstVisibleItemIndex != selectedIndex) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = VitranSpacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                val side = min(maxWidth, maxHeight)
                LazyRow(
                    state = listState,
                    flingBehavior = flingBehavior,
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    userScrollEnabled = imageUrls.size > 1,
                ) {
                    itemsIndexed(
                        items = imageUrls,
                        key = { index, url -> "lb-c-$index-$url" },
                    ) { _, url ->
                        Box(
                            modifier = Modifier
                                .width(maxWidth)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            AsyncImage(
                                model = resolveNetworkImageUrl(url),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(side)
                                    .background(LightboxPreviewRail),
                                contentScale = ContentScale.Fit,
                            )
                        }
                    }
                }
            }

            if (imageUrls.size > 1) {
                Row(
                    modifier = Modifier.padding(vertical = VitranSpacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LightboxNavButton(
                        forward = false,
                        enabled = selectedIndex > 0,
                        onClick = { onSelect(selectedIndex - 1) },
                    )
                    LightboxNavButton(
                        forward = true,
                        enabled = selectedIndex < imageUrls.lastIndex,
                        onClick = { onSelect(selectedIndex + 1) },
                    )
                }
            }
        }

        LightboxCloseButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(VitranSpacing.lg),
        )
    }
}

@Composable
private fun ExpandedLightboxBody(
    imageUrls: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    useVerticalThumbs: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(LightboxInset)
            .border(
                width = 1.dp,
                color = LightboxBorder,
                shape = RoundedCornerShape(LightboxRadius),
            )
            .clip(RoundedCornerShape(LightboxRadius))
            .background(MaterialTheme.colorScheme.surface)
            .padding(LightboxPad),
    ) {
        if (useVerticalThumbs) {
            DesktopLightboxGallery(
                imageUrls = imageUrls,
                selectedIndex = selectedIndex,
                onSelect = onSelect,
            )
        } else {
            MediumLightboxGallery(
                imageUrls = imageUrls,
                selectedIndex = selectedIndex,
                onSelect = onSelect,
            )
        }

        LightboxCloseButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(VitranSpacing.xs),
        )
    }
}

@Composable
private fun DesktopLightboxGallery(
    imageUrls: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyColumn(
            modifier = Modifier
                .width(LightboxThumbSize)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(
                space = LightboxThumbGap,
                alignment = Alignment.CenterVertically,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(
                items = imageUrls,
                key = { index, url -> "lb-dt-$index-$url" },
            ) { index, url ->
                LightboxThumbnail(
                    imageUrl = url,
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) },
                )
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            val previewSize = min(maxWidth, maxHeight)
            Box(
                modifier = Modifier
                    .size(previewSize)
                    .shadow(
                        elevation = LightboxPreviewShadowElevation,
                        shape = RoundedCornerShape(LightboxDesktopPreviewRadius),
                        clip = false,
                        ambientColor = LightboxPreviewShadowColor,
                        spotColor = LightboxPreviewShadowColor,
                    )
                    .clip(RoundedCornerShape(LightboxDesktopPreviewRadius))
                    .background(LightboxPreviewRail),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = resolveNetworkImageUrl(imageUrls[selectedIndex]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
private fun MediumLightboxGallery(
    imageUrls: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(VitranRadius.medium))
                .background(LightboxPreviewRail),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(imageUrls[selectedIndex]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(LightboxThumbGap),
                contentPadding = PaddingValues(end = VitranSpacing.sm),
            ) {
                itemsIndexed(
                    items = imageUrls,
                    key = { index, url -> "lb-mt-$index-$url" },
                ) { index, url ->
                    LightboxThumbnail(
                        imageUrl = url,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                    )
                }
            }
            if (imageUrls.size > 1) {
                LightboxNavButton(
                    forward = false,
                    enabled = selectedIndex > 0,
                    onClick = { onSelect(selectedIndex - 1) },
                )
                LightboxNavButton(
                    forward = true,
                    enabled = selectedIndex < imageUrls.lastIndex,
                    onClick = { onSelect(selectedIndex + 1) },
                )
            }
        }
    }
}

@Composable
private fun LightboxThumbnail(
    imageUrl: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val borderColor =
        if (selected) MaterialTheme.colorScheme.onSurface else Color.Transparent
    Box(
        modifier = Modifier
            .size(LightboxThumbSize)
            .clip(RoundedCornerShape(LightboxThumbRadius))
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(LightboxThumbRadius),
            )
            .background(LightboxThumbRail)
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
                .clip(RoundedCornerShape(LightboxThumbRadius - 2.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun LightboxCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val a11y = stringResource(Res.string.product_detail_media_close_a11y)
    Box(
        modifier = modifier
            .size(LightboxCloseSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f), CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = a11y,
            size = LightboxCloseGlyph,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun LightboxNavButton(
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
            .size(LightboxNavSize)
            .shadow(
                elevation = LightboxNavShadowElevation,
                shape = CircleShape,
                clip = false,
                ambientColor = LightboxNavShadowColor,
                spotColor = LightboxNavShadowColor,
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
            size = LightboxNavGlyph,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}
