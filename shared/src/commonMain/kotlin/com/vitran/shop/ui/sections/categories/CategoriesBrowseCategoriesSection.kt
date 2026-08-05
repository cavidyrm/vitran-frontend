package com.vitran.shop.ui.sections.categories

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_browse_open_a11y
import vitranshop.shared.generated.resources.categories_browse_title

/** shop.app `rounded-radius-24` on browse cards. */
private val BrowseCardRadius = 24.dp

/** shop.app title→grid gap (~24). */
private val TitleToGridGap = VitranSpacing.xxl

/** shop.app `gap-space-12` between the two product tiles. */
private val TileGap = VitranSpacing.md

/**
 * Mid (tablet) content-width floor for 3-up when not in desktop shell.
 * 5-up is gated on [LocalDesktopLayout] (viewport), not content width —
 * otherwise rail/padding swap at the desktop breakpoint widens content and
 * briefly flashes back to a denser grid.
 */
private val BrowseGridThreeColMin = 600.dp

/**
 * shop.app tile border — `border-border-secondary` ≈ `rgba(24, 59, 78, 0.06)`.
 */
private val TileBorder = Color(0x0F183B4E)

/** Soft fill while product images load. */
private val TilePlaceholder = Color(0xFFF2F4F5)

/**
 * Categories “Browse categories” grid (shop.app `/categories`).
 *
 * Measured (shop.app 2026 live):
 * - H2: 20/22 semibold, black
 * - Cards: radius 24, pad 16, column gap 16; solid category color
 * - Tiles: 2× aspect-square, radius 16, gap 12, light border
 * - Grid: 5 cols on desktop shell; else 3 cols (≥600 content) / 2 cols
 *
 * Refs: `docs/ui-reference/categories-browse-desktop.png`,
 * `docs/ui-reference/categories-browse-compact.png`
 */
@Composable
fun CategoriesBrowseCategoriesSection(
    categories: List<BrowseCategory>,
    modifier: Modifier = Modifier,
    onCategoryClick: (BrowseCategory) -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current
    val horizontalPad = if (isDesktop) {
        VitranSpacing.xxxl + VitranSpacing.lg
    } else {
        VitranSpacing.lg
    }
    val itemGap = VitranSpacing.lg
    val sectionBottom = if (isDesktop) VitranSpacing.xxl else VitranSpacing.md
    val title = stringResource(Res.string.categories_browse_title)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPad)
            .padding(bottom = sectionBottom),
    ) {
        // Desktop viewport → always 5. Content-width only chooses 2 vs 3 below that,
        // so chrome/padding changes at the shell breakpoint cannot bounce column count.
        val columns = when {
            isDesktop -> 5
            maxWidth >= BrowseGridThreeColMin -> 3
            else -> 2
        }
        val rowGap = if (columns >= 3) VitranSpacing.lg else VitranSpacing.sm

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                ),
            )

            Spacer(modifier = Modifier.height(TitleToGridGap))

            val rows = categories.chunked(columns)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(rowGap),
            ) {
                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(itemGap),
                    ) {
                        rowItems.forEach { category ->
                            BrowseCategoryCard(
                                category = category,
                                onClick = { onCategoryClick(category) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(columns - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrowseCategoryCard(
    category: BrowseCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(BrowseCardRadius)
    val tileShape = RoundedCornerShape(VitranRadius.large)
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
        label = "browseCategoryHover",
    )
    val openLabel = stringResource(Res.string.categories_browse_open_a11y, category.title)
    val placeholder = remember { ColorPainter(TilePlaceholder) }

    Column(
        modifier = modifier
            .clip(shape)
            .background(category.backgroundColor, shape)
            .hoverable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = openLabel }
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        Text(
            text = category.title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TileGap),
        ) {
            BrowseProductTile(
                imageUrl = category.imageUrl1,
                placeholder = placeholder,
                shape = tileShape,
                imageScale = imageScale,
                modifier = Modifier.weight(1f),
            )
            BrowseProductTile(
                imageUrl = category.imageUrl2,
                placeholder = placeholder,
                shape = tileShape,
                imageScale = imageScale,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BrowseProductTile(
    imageUrl: String,
    placeholder: ColorPainter,
    shape: RoundedCornerShape,
    imageScale: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(Color.White, shape)
            .border(width = 1.dp, color = TileBorder, shape = shape),
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(imageUrl),
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
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun CategoriesBrowseCategoriesCompactPreview() {
    VitranTheme {
        CategoriesBrowseCategoriesSection(categories = rememberMockBrowseCategories())
    }
}

@Preview(showBackground = true, widthDp = 768)
@Composable
private fun CategoriesBrowseCategoriesMidPreview() {
    VitranTheme {
        CategoriesBrowseCategoriesSection(categories = rememberMockBrowseCategories())
    }
}

@Preview(showBackground = true, widthDp = 1280)
@Composable
private fun CategoriesBrowseCategoriesDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            CategoriesBrowseCategoriesSection(categories = rememberMockBrowseCategories())
        }
    }
}
