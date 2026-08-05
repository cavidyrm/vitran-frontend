package com.vitran.shop.ui.sections.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.vitran.shop.ui.components.CategoriesMerchantCard
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_merchant_grid_section_open_a11y
import vitranshop.shared.generated.resources.ic_bold_right_chevron

/** shop.app header title↔chevron `gap-space-8`. */
private val HeaderChevronGap = VitranSpacing.sm

/** shop.app header chevron chip: 16dp circle. */
private val HeaderChevronChip = 16.dp

/** Glyph size inside the 16dp chip. */
private val HeaderChevronGlyph = 12.dp

/** shop.app `bg-bg-fill-secondary` for header chevron chip. */
private val HeaderChevronFill = Color(0xFFF2F4F5)

/** Tailwind-ish floors matching shop.app `sm` / `md` for wrap columns. */
private val GridSmMin = 640.dp
private val GridMdMin = 768.dp

/**
 * Vertical stack of Categories category-merchant wrap grids
 * (Women / Men / Beauty / …).
 *
 * Page rhythm: [CategoriesSectionGap] (40dp) between each grid and vs product rows.
 */
@Composable
fun CategoriesMerchantGridsFeed(
    sections: List<CategoriesMerchantGridSection>,
    modifier: Modifier = Modifier,
    onSectionClick: (CategoriesMerchantGridSection) -> Unit = {},
    onShopClick: (CategoriesMerchantGridSection, CategoriesMerchantShop) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CategoriesSectionGap),
    ) {
        sections.forEach { section ->
            CategoriesMerchantGrid(
                section = section,
                onSectionClick = { onSectionClick(section) },
                onShopClick = { shop -> onShopClick(section, shop) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * One category header + non-scrolling multi-row merchant grid
 * (shop.app `/categories` feed after product carousels).
 *
 * Measured tokens (shop.app 2026):
 * - Columns: 2 / sm 3 / md 4 / lg+ 6 (`w-1/2 sm:w-1/3 md:w-1/4 lg:w-1/6`)
 * - Row gap 8 compact / 16 md+; item half-pad 8 / 16
 * - Header↔grid 16 compact / 24 desktop; title 18 / 20
 * - Gap above owned by [CategoriesScreen] `CategoriesSectionGap` (40)
 *
 * Refs: `docs/ui-reference/categories-merchant-grids-compact.png`,
 * `docs/ui-reference/categories-merchant-grids-desktop.png`
 */
@Composable
fun CategoriesMerchantGrid(
    section: CategoriesMerchantGridSection,
    modifier: Modifier = Modifier,
    onSectionClick: () -> Unit = {},
    onShopClick: (CategoriesMerchantShop) -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val horizontalPad = if (isDesktop) {
        VitranSpacing.xxxl + VitranSpacing.lg
    } else {
        VitranSpacing.lg
    }
    val titleToGrid = if (isDesktop) VitranSpacing.xxl else VitranSpacing.lg
    val openLabel = stringResource(
        Res.string.categories_merchant_grid_section_open_a11y,
        section.title,
    )
    val headerInteraction = remember { MutableInteractionSource() }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = merchantGridColumns(maxWidth, isDesktop)
        val itemHalfPad = merchantItemHalfPad(maxWidth, isDesktop)
        val rowGap = merchantRowGap(maxWidth, isDesktop)

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

            Spacer(modifier = Modifier.height(titleToGrid))

            // shop.app: flex-wrap with -mx + per-item px (half of column gap).
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPad - itemHalfPad),
                verticalArrangement = Arrangement.spacedBy(rowGap),
            ) {
                section.shops.chunked(columns).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { shop ->
                            CategoriesMerchantCard(
                                shop = shop,
                                onClick = { onShopClick(shop) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = itemHalfPad),
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

/**
 * shop.app column count: default 2 / sm 3 / md 4 / lg+ 6.
 * Desktop shell always uses 6 (viewport), matching product-row lg behavior.
 */
private fun merchantGridColumns(trackWidth: Dp, isDesktop: Boolean): Int = when {
    isDesktop -> 6
    trackWidth >= GridMdMin -> 4
    trackWidth >= GridSmMin -> 3
    else -> 2
}

/** shop.app `px-space-4` / `md:px-space-8` half-gap on each card. */
private fun merchantItemHalfPad(trackWidth: Dp, isDesktop: Boolean): Dp =
    if (isDesktop || trackWidth >= GridMdMin) VitranSpacing.lg else VitranSpacing.sm

/** shop.app `gap-y-space-8` / `md:gap-y-space-16`. */
private fun merchantRowGap(trackWidth: Dp, isDesktop: Boolean): Dp =
    if (isDesktop || trackWidth >= GridMdMin) VitranSpacing.lg else VitranSpacing.sm

@Preview(showBackground = true, widthDp = 390, heightDp = 720)
@Composable
private fun CategoriesMerchantGridCompactPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides false) {
            CategoriesMerchantGrid(
                section = rememberMockCategoriesMerchantGrids().first(),
                modifier = Modifier.padding(vertical = VitranSpacing.lg),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 560)
@Composable
private fun CategoriesMerchantGridDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            CategoriesMerchantGrid(
                section = rememberMockCategoriesMerchantGrids().first(),
                modifier = Modifier.padding(vertical = VitranSpacing.lg),
            )
        }
    }
}
