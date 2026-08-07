package com.vitran.shop.ui.sections.store

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.shell.LocalShellViewportHeight
import com.vitran.shop.ui.shell.LocalShellViewportWidth
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_menu_hamburger
import vitranshop.shared.generated.resources.ic_star_filled
import vitranshop.shared.generated.resources.product_detail_follow
import vitranshop.shared.generated.resources.product_detail_follow_a11y
import vitranshop.shared.generated.resources.store_category_a11y
import vitranshop.shared.generated.resources.store_collection_a11y
import vitranshop.shared.generated.resources.store_menu_a11y
import vitranshop.shared.generated.resources.store_rating_a11y
import vitranshop.shared.generated.resources.store_view_all_collections
import vitranshop.shared.generated.resources.store_view_all_collections_a11y

/** shop.app cover `h-[200px]` compact. */
private val CoverHeightCompact = 200.dp

/** shop.app cover `max-h-[600px]` on md+. */
private val CoverHeightMaxDesktop = 600.dp

/** shop.app `md:h-[55vh]`. */
private const val CoverViewportFractionDesktop = 0.55f

/** shop.app sticky top pad `pt-space-20`. */
private val CoverTopPad = VitranSpacing.xl

/** shop.app menu/follow overlay inset. */
private val OverlayInset = VitranSpacing.lg

/** shop.app glassy control height ~40. */
private val GlassyControlHeight = 40.dp

/** shop.app menu logo `w-space-32 h-space-32`. */
private val MenuLogoSize = VitranSize.avatarMedium

/** shop.app hamburger glyph 16. */
private val MenuIconSize = VitranSize.iconSmall

/** shop.app wordmark compact `max-h-[80px] max-w-[180px] min-h-space-40`. */
private val WordmarkMaxHCompact = 80.dp
private val WordmarkMaxWCompact = 180.dp
private val WordmarkMinH = VitranSpacing.xxxl + VitranSpacing.sm // 40

/** shop.app wordmark md `max-h-[180px] max-w-[400px]`. */
private val WordmarkMaxHDesktop = 180.dp
private val WordmarkMaxWDesktop = 400.dp

/** shop.app h1 `font-headerBold` ~26px line on compact. */
private val StoreNameSizeCompact = 22.sp
private val StoreNameSizeDesktop = 32.sp

/** shop.app category thumb ~28; keep inside the shorter chip. */
private val ChipThumbSize = 24.dp

/** shop.app category chip ~42; keep slightly compact without a heavy outline. */
private val ChipHeight = 34.dp

/** shop.app chip row `gap-space-4` / `md:gap-space-8`. */
private val ChipGapCompact = VitranSpacing.xs
private val ChipGapDesktop = VitranSpacing.sm

/** shop.app collections `mt-space-32`. */
private val CollectionsTopGap = VitranSpacing.xxxl

/**
 * shop.app collection tile:
 * outer `rounded-radius-20` + `border-border-image` + `p-space-4` + `shadow-s`
 * → inner image `rounded-radius-16`.
 */
private val CollectionFrameRadius = VitranRadius.xl
private val CollectionImageRadius = VitranRadius.large
private val CollectionFramePad = VitranSpacing.xs

/** shop.app `border-border-image` ≈ `rgba(5, 41, 77, 0.1)`. */
private val CollectionFrameBorder = Color(0xFF05294D).copy(alpha = 0.1f)

/** shop.app `bg-bg-overlay-inverse-06` ≈ `rgba(24, 59, 78, 0.06)`. */
private val CollectionFrameFill = Color(0xFF183B4E).copy(alpha = 0.06f)

/** shop.app `shadow-s` / hover `shadow-m` spot. */
private val CollectionShadowIdle = Color.Black.copy(alpha = 0.06f)
private val CollectionShadowHover = Color.Black.copy(alpha = 0.12f)

/** shop.app `hover:-translate-y-space-2` (8dp). */
private val CollectionHoverLift = VitranSpacing.sm

/** shop.app collection gutters `px-space-6` / `md:px-space-8`. */
private val CollectionGutterCompact = 6.dp
private val CollectionGutterDesktop = VitranSpacing.sm

/** shop.app `gap-y-space-12` / `md:gap-y-space-16`. */
private val CollectionRowGapCompact = VitranSpacing.md
private val CollectionRowGapDesktop = VitranSpacing.lg

/** shop.app `aspect-square` / `md:aspect-video`. */
private val CollectionAspectCompact = 1f
private val CollectionAspectDesktop = 16f / 9f

/** shop.app view-all `mt/mb-space-24`. */
private val ViewAllVerticalGap = VitranSpacing.xxl

/** Tailwind-ish floors matching shop.app `sm` / `md` for collection columns. */
private val GridSmMin = 640.dp
private val GridMdMin = 768.dp

/** shop.app glassy control shadow (inset highlight + soft drop). */
private val GlassyShadow = Color.Black.copy(alpha = 0.12f)

/** Cover band height — compact `h-[200px]`, md+ `h-[55vh]` capped at 600. */
@Composable
fun rememberStoreCoverHeight(): Dp {
    val viewportWidth = LocalShellViewportWidth.current
    val viewportHeight = LocalShellViewportHeight.current
    val isMdUp = viewportWidth >= VitranSize.mdBreakpoint
    return if (isMdUp) {
        (viewportHeight * CoverViewportFractionDesktop).coerceIn(
            CoverHeightCompact,
            CoverHeightMaxDesktop,
        )
    } else {
        CoverHeightCompact
    }
}

/**
 * Sticky cover media layer (shop.app `sticky top-0` + `mb-[-200px]` trick).
 *
 * Drawn behind the scrolling column; [collapseProgress] 0→1 shrinks height and
 * fades opacity so the band “collapses” as content scrolls over it.
 */
@Composable
fun StoreCoverLayer(
    store: StoreMock,
    coverHeight: Dp,
    collapseProgress: Float,
    modifier: Modifier = Modifier,
) {
    val progress = collapseProgress.coerceIn(0f, 1f)
    val height = coverHeight * (1f - progress)
    val alpha = 1f - progress
    if (height <= 0.dp || alpha <= 0.01f) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer { this.alpha = alpha },
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(store.coverUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(store.brandColor),
            error = ColorPainter(store.brandColor),
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            store.brandColor.copy(alpha = 0.55f),
                            store.brandColor,
                        ),
                    ),
                ),
        )
    }
}

/**
 * Menu + Follow row — shop.app `sticky top-space-20 … z-30`.
 */
@Composable
fun StoreMenuFollowBar(
    store: StoreMock,
    onMenuClick: () -> Unit,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val overlayText = if (store.useLightText) Color.White else Color(0xFF1A1A1A)
    val glassyFill = store.brandColor.copy(alpha = 0.85f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = OverlayInset)
            .padding(top = CoverTopPad, bottom = VitranSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StoreMenuButton(
            storeName = store.name,
            avatarUrl = store.avatarUrl,
            glassyFill = glassyFill,
            contentColor = overlayText,
            onClick = onMenuClick,
        )
        StoreFollowButton(
            storeName = store.name,
            glassyFill = glassyFill,
            contentColor = overlayText,
            onClick = onFollowClick,
        )
    }
}

/**
 * Wordmark + rating — scrolls away under sticky chrome (not sticky on shop.app).
 *
 * Height roughly fills the cover band under the menu so chips sit near the
 * cover bottom before they stick.
 */
@Composable
fun StoreIdentityBlock(
    store: StoreMock,
    coverHeight: Dp,
    onRatingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewportWidth = LocalShellViewportWidth.current
    val isMdUp = viewportWidth >= VitranSize.mdBreakpoint
    val overlayText = if (store.useLightText) Color.White else Color(0xFF1A1A1A)
    // Leave room for sticky menu (~40+pad) so wordmark sits in the cover center.
    val blockHeight = (coverHeight - GlassyControlHeight - CoverTopPad).coerceAtLeast(96.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(blockHeight)
            .padding(horizontal = VitranSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StoreWordmark(
            wordmarkUrl = store.wordmarkUrl,
            name = store.name,
            isMdUp = isMdUp,
            contentColor = overlayText,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.sm))
        StoreRatingRow(
            ratingLabel = store.ratingLabel,
            reviewCountLabel = store.reviewCountLabel,
            contentColor = overlayText,
            onClick = onRatingClick,
        )
    }
}

/**
 * Category chip row — shop.app `sticky top-space-20 … z-20`.
 *
 * Parent should use [LazyListScope.stickyHeader] with [store.brandColor] behind
 * so product rows do not show through once stuck.
 */
@Composable
fun StoreCategoryChipsBar(
    store: StoreMock,
    onNavChipClick: (StoreNavChip) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewportWidth = LocalShellViewportWidth.current
    val isMdUp = viewportWidth >= VitranSize.mdBreakpoint
    val overlayText = if (store.useLightText) Color.White else Color(0xFF1A1A1A)
    val glassyFill = store.brandColor.copy(alpha = 0.85f)
    StoreCategoryChipRow(
        chips = store.navChips,
        isMdUp = isMdUp,
        contentColor = overlayText,
        chipFill = glassyFill,
        onChipClick = onNavChipClick,
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * Collections grid below sticky chips (scrolls normally).
 */
@Composable
fun StoreCollectionsSection(
    store: StoreMock,
    onCollectionClick: (StoreCollection) -> Unit = {},
    onViewAllCollectionsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val overlayText = if (store.useLightText) Color.White else Color(0xFF1A1A1A)
    StoreCollectionsGrid(
        collections = store.collections,
        storeName = store.name,
        contentColor = overlayText,
        onCollectionClick = onCollectionClick,
        onViewAllClick = onViewAllCollectionsClick,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun StoreMenuButton(
    storeName: String,
    avatarUrl: String,
    glassyFill: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    val a11y = stringResource(Res.string.store_menu_a11y, storeName)
    Row(
        modifier = Modifier
            .height(GlassyControlHeight)
            .defaultMinSize(minWidth = 68.dp)
            .shadow(elevation = VitranElevation.small, shape = RoundedCornerShape(percent = 50), spotColor = GlassyShadow, ambientColor = GlassyShadow)
            .clip(RoundedCornerShape(percent = 50))
            .background(glassyFill)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = a11y }
            .padding(start = VitranSpacing.xs, end = 10.dp, top = VitranSpacing.xs, bottom = VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(avatarUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.White.copy(alpha = 0.35f)),
            error = ColorPainter(Color.White.copy(alpha = 0.35f)),
            modifier = Modifier
                .size(MenuLogoSize)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f))
                .border(VitranSize.borderHairline, Color.White.copy(alpha = 0.45f), CircleShape),
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_menu_hamburger),
            contentDescription = null,
            size = MenuIconSize,
            tint = contentColor,
        )
    }
}

@Composable
private fun StoreFollowButton(
    storeName: String,
    glassyFill: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    val label = stringResource(Res.string.product_detail_follow)
    val a11y = stringResource(Res.string.product_detail_follow_a11y, storeName)
    Text(
        text = label,
        color = contentColor,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
        modifier = Modifier
            .heightIn(min = GlassyControlHeight)
            .shadow(elevation = VitranElevation.small, shape = RoundedCornerShape(percent = 50), spotColor = GlassyShadow, ambientColor = GlassyShadow)
            .clip(RoundedCornerShape(percent = 50))
            .background(glassyFill)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = a11y }
            .padding(horizontal = VitranSpacing.lg, vertical = 10.dp),
    )
}

@Composable
private fun StoreWordmark(
    wordmarkUrl: String?,
    name: String,
    isMdUp: Boolean,
    contentColor: Color,
) {
    val maxH = if (isMdUp) WordmarkMaxHDesktop else WordmarkMaxHCompact
    val maxW = if (isMdUp) WordmarkMaxWDesktop else WordmarkMaxWCompact
    val url = wordmarkUrl?.takeIf { it.isNotBlank() }
    if (url != null) {
        AsyncImage(
            model = resolveNetworkImageUrl(url),
            contentDescription = name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .heightIn(min = WordmarkMinH, max = maxH)
                .widthIn(max = maxW)
                .fillMaxWidth(fraction = if (isMdUp) 0.55f else 0.7f),
        )
    } else {
        // shop.app h1 when no `store-data-wordmark` (e.g. Tushbaby).
        Text(
            text = name,
            color = contentColor,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = if (isMdUp) StoreNameSizeDesktop else StoreNameSizeCompact,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StoreRatingRow(
    ratingLabel: String,
    reviewCountLabel: String,
    contentColor: Color,
    onClick: () -> Unit,
) {
    val a11y = stringResource(Res.string.store_rating_a11y, ratingLabel, reviewCountLabel)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = a11y }
            .padding(horizontal = VitranSpacing.sm, vertical = VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Text(
            text = ratingLabel,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            ),
            maxLines = 1,
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_star_filled),
            contentDescription = null,
            size = 12.dp,
            tint = VitranTheme.extraColors.star,
        )
        Text(
            text = reviewCountLabel,
            color = contentColor.copy(alpha = 0.92f),
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
            maxLines = 1,
        )
    }
}

@Composable
private fun StoreCategoryChipRow(
    chips: List<StoreNavChip>,
    isMdUp: Boolean,
    contentColor: Color,
    chipFill: Color,
    onChipClick: (StoreNavChip) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gap = if (isMdUp) ChipGapDesktop else ChipGapCompact
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = VitranSpacing.lg,
            vertical = VitranSpacing.sm,
        ),
        // Center the chip group when it fits; still scrolls when it overflows.
        horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(chips, key = { it.id }) { chip ->
            StoreCategoryChip(
                chip = chip,
                contentColor = contentColor,
                chipFill = chipFill,
                onClick = { onChipClick(chip) },
            )
        }
    }
}

@Composable
private fun StoreCategoryChip(
    chip: StoreNavChip,
    contentColor: Color,
    chipFill: Color,
    onClick: () -> Unit,
) {
    val a11y = stringResource(Res.string.store_category_a11y, chip.label)
    Row(
        modifier = Modifier
            .height(ChipHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(chipFill)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = a11y }
            .padding(
                start = if (chip.thumbUrl != null) VitranSpacing.xs else VitranSpacing.md,
                end = VitranSpacing.md,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        val thumb = chip.thumbUrl
        if (thumb != null) {
            AsyncImage(
                model = resolveNetworkImageUrl(thumb),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(ChipThumbSize)
                    .clip(CircleShape),
            )
        }
        Text(
            text = chip.label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StoreCollectionsGrid(
    collections: List<StoreCollection>,
    storeName: String,
    contentColor: Color,
    onCollectionClick: (StoreCollection) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewAll = stringResource(Res.string.store_view_all_collections)
    val viewAllA11y = stringResource(Res.string.store_view_all_collections_a11y, storeName)
    // Use shell viewport (not content pane) so side-rail desktop width still maps
    // to shop.app `sm`/`md` column counts correctly.
    val viewportWidth = LocalShellViewportWidth.current
    val columns = when {
        viewportWidth >= GridMdMin -> 4
        viewportWidth >= GridSmMin -> 3
        else -> 2
    }
    val isMdUp = viewportWidth >= GridMdMin
    val gutter = if (isMdUp) CollectionGutterDesktop else CollectionGutterCompact
    val rowGap = if (isMdUp) CollectionRowGapDesktop else CollectionRowGapCompact
    val outerPad = VitranSpacing.lg - gutter
    val aspect = if (isMdUp) CollectionAspectDesktop else CollectionAspectCompact

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = CollectionsTopGap)
            .padding(horizontal = outerPad.coerceAtLeast(0.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        collections.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = rowGap),
            ) {
                rowItems.forEach { collection ->
                    StoreCollectionCard(
                        collection = collection,
                        contentColor = contentColor,
                        aspectRatio = aspect,
                        onClick = { onCollectionClick(collection) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = gutter),
                    )
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Text(
            text = viewAll,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
            modifier = Modifier
                .padding(vertical = ViewAllVerticalGap)
                .shadow(elevation = VitranElevation.small, shape = RoundedCornerShape(percent = 50), spotColor = GlassyShadow, ambientColor = GlassyShadow)
                .clip(RoundedCornerShape(percent = 50))
                .background(contentColor.copy(alpha = 0.12f))
                .clickable(role = Role.Button, onClick = onViewAllClick)
                .semantics { contentDescription = viewAllA11y }
                .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
        )
    }
}

@Composable
private fun StoreCollectionCard(
    collection: StoreCollection,
    contentColor: Color,
    aspectRatio: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val a11y = stringResource(Res.string.store_collection_a11y, collection.title)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    // shop.app: hover lifts the framed tile (`-translate-y-space-2`) and bumps shadow — not image zoom.
    val lift by animateDpAsState(
        targetValue = if (hovered) -CollectionHoverLift else 0.dp,
        animationSpec = tween(150),
        label = "collectionHoverLift",
    )
    val elevation by animateDpAsState(
        targetValue = if (hovered) VitranElevation.medium else VitranElevation.small,
        animationSpec = tween(150),
        label = "collectionHoverElevation",
    )
    val shadowColor = if (hovered) CollectionShadowHover else CollectionShadowIdle
    val frameShape = RoundedCornerShape(CollectionFrameRadius)
    val imageShape = RoundedCornerShape(CollectionImageRadius)

    Column(
        modifier = modifier
            .hoverable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = a11y },
        horizontalAlignment = Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .offset(y = lift)
                .shadow(
                    elevation = elevation,
                    shape = frameShape,
                    clip = false,
                    ambientColor = shadowColor,
                    spotColor = shadowColor,
                )
                .clip(frameShape)
                .background(CollectionFrameFill, frameShape)
                .border(1.dp, CollectionFrameBorder, frameShape)
                .padding(CollectionFramePad),
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(collection.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(CollectionFrameFill),
                error = ColorPainter(CollectionFrameFill),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(imageShape),
            )
        }
        Text(
            text = collection.title,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                // shop.app label `mt-space-8`
                .padding(top = VitranSpacing.sm, bottom = VitranSpacing.xs),
        )
    }
}
