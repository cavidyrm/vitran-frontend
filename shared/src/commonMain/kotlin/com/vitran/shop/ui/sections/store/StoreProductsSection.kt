package com.vitran.shop.ui.sections.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.CategoriesProductCard
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.sections.categories.CategoriesProduct
import com.vitran.shop.ui.shell.LocalShellViewportWidth
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_chevron_down
import vitranshop.shared.generated.resources.ic_filter_sliders
import vitranshop.shared.generated.resources.ic_search
import vitranshop.shared.generated.resources.store_products_filter_a11y
import vitranshop.shared.generated.resources.store_products_in_stock
import vitranshop.shared.generated.resources.store_products_on_sale
import vitranshop.shared.generated.resources.store_products_price
import vitranshop.shared.generated.resources.store_products_search_a11y
import vitranshop.shared.generated.resources.store_products_search_placeholder
import vitranshop.shared.generated.resources.store_products_sort
import vitranshop.shared.generated.resources.store_products_title

/**
 * shop.app Products block top gap after collections / view-all (~space-32).
 */
val StoreProductsSectionTopGap = VitranSpacing.xxxl

/** shop.app search `h-[44px]` / `rounded-radius-28` / `lg:w-[329px]`. */
private val SearchHeight = 44.dp
private val SearchWidthDesktop = 329.dp
private val SearchRadius = VitranRadius.extraLarge

/** shop.app `border-border-image`. */
private val SearchBorder = Color(0xFF05294D).copy(alpha = 0.1f)

/** shop.app filter chip `h-space-40`. */
private val FilterChipHeight = 40.dp

/** shop.app selected chip fill `#121212`. */
private val FilterChipSelectedFill = Color(0xFF121212)

/** shop.app title → filter row (~51px). */
private val TitleToFiltersGap = VitranSpacing.xxl + VitranSpacing.xs // 24+4

/** shop.app filter chips → grid (~40px). */
private val FiltersToGridGap = VitranSpacing.xxxl

/** shop.app product-grid `gap: 32px 16px`. */
private val GridRowGap = VitranSpacing.xxxl
private val GridColGap = VitranSpacing.lg

/** Tailwind-ish floors matching shop.app product-grid column counts. */
private val GridSmMin = 640.dp
private val GridMdMin = 768.dp
private val GridLgMin = 1024.dp
private val GridXlMin = 1200.dp

/**
 * Store products segment (shop.app `/m/{handle}` Products):
 * title + search, filter chips, responsive product grid.
 *
 * Filter / sort / search are visual + local mock state only (no API).
 */
@Composable
fun StoreProductsSection(
    storeName: String,
    brandColor: Color,
    products: List<CategoriesProduct>,
    modifier: Modifier = Modifier,
    onProductClick: (CategoriesProduct) -> Unit = {},
    onSaveClick: (CategoriesProduct) -> Unit = {},
) {
    val viewportWidth = LocalShellViewportWidth.current
    val isMdUp = viewportWidth >= VitranSize.mdBreakpoint
    val glassyFill = brandColor.copy(alpha = 0.85f)
    val columns = when {
        viewportWidth >= GridXlMin -> 6
        viewportWidth >= GridLgMin -> 5
        viewportWidth >= GridMdMin -> 4
        viewportWidth >= GridSmMin -> 3
        else -> 2
    }

    var query by remember { mutableStateOf("") }
    var onSaleOnly by remember { mutableStateOf(false) }
    var inStockOnly by remember { mutableStateOf(true) }

    val visible = remember(products, query, onSaleOnly) {
        products.filter { product ->
            val matchesQuery = query.isBlank() ||
                product.title.contains(query, ignoreCase = true)
            val matchesSale = !onSaleOnly || product.discountLabel != null
            matchesQuery && matchesSale
        }
    }

    val title = stringResource(Res.string.store_products_title)
    val searchPlaceholder = stringResource(Res.string.store_products_search_placeholder, storeName)
    val searchA11y = stringResource(Res.string.store_products_search_a11y, storeName)
    val filterA11y = stringResource(Res.string.store_products_filter_a11y)
    val sortLabel = stringResource(Res.string.store_products_sort)
    val onSaleLabel = stringResource(Res.string.store_products_on_sale)
    val priceLabel = stringResource(Res.string.store_products_price)
    val inStockLabel = stringResource(Res.string.store_products_in_stock)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = StoreProductsSectionTopGap)
            .padding(horizontal = VitranSpacing.lg),
    ) {
        if (isMdUp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp,
                    ),
                    modifier = Modifier.weight(1f),
                )
                StoreProductsSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = searchPlaceholder,
                    contentDescription = searchA11y,
                    modifier = Modifier.width(SearchWidthDesktop),
                )
            }
        } else {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                ),
            )
        }

        Spacer(modifier = Modifier.height(TitleToFiltersGap))

        StoreProductsFilterRow(
            glassyFill = glassyFill,
            filterA11y = filterA11y,
            sortLabel = sortLabel,
            onSaleLabel = onSaleLabel,
            priceLabel = priceLabel,
            inStockLabel = inStockLabel,
            onSaleSelected = onSaleOnly,
            inStockSelected = inStockOnly,
            onFilterClick = {},
            onSortClick = {},
            onOnSaleToggle = { onSaleOnly = !onSaleOnly },
            onPriceClick = {},
            onInStockToggle = { inStockOnly = !inStockOnly },
        )

        Spacer(modifier = Modifier.height(FiltersToGridGap))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cellWidth = (maxWidth - GridColGap * (columns - 1)) / columns
            // BoxWithConstraints is a Box — stack rows in a Column or they paint on top of each other.
            Column(modifier = Modifier.fillMaxWidth()) {
                visible.chunked(columns).forEachIndexed { rowIndex, rowItems ->
                    if (rowIndex > 0) {
                        Spacer(modifier = Modifier.height(GridRowGap))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(GridColGap),
                    ) {
                        rowItems.forEach { product ->
                            CategoriesProductCard(
                                product = product,
                                cardWidth = null,
                                showStoreName = false,
                                onClick = { onProductClick(product) },
                                onSaveClick = { onSaveClick(product) },
                                modifier = Modifier.width(cellWidth),
                            )
                        }
                        repeat(columns - rowItems.size) {
                            Spacer(modifier = Modifier.width(cellWidth))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(VitranSpacing.xxxl))
    }
}

@Composable
private fun StoreProductsSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Box(
        modifier = modifier
            .height(SearchHeight)
            .clip(RoundedCornerShape(SearchRadius))
            .border(1.dp, SearchBorder, RoundedCornerShape(SearchRadius))
            .semantics { this.contentDescription = contentDescription },
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = VitranSpacing.lg),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .padding(start = 48.dp, end = VitranSpacing.xxl),
            decorationBox = { inner ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = textStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    inner()
                }
            },
        )
    }
}

@Composable
private fun StoreProductsFilterRow(
    glassyFill: Color,
    filterA11y: String,
    sortLabel: String,
    onSaleLabel: String,
    priceLabel: String,
    inStockLabel: String,
    onSaleSelected: Boolean,
    inStockSelected: Boolean,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    onOnSaleToggle: () -> Unit,
    onPriceClick: () -> Unit,
    onInStockToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StoreFilterIconButton(
            contentDescription = filterA11y,
            fill = glassyFill,
            onClick = onFilterClick,
        )
        // shop.app Sort control uses selected (dark) chrome by default.
        StoreFilterChip(
            label = sortLabel,
            selected = true,
            glassyFill = glassyFill,
            showChevron = true,
            onClick = onSortClick,
        )
        StoreFilterChip(
            label = onSaleLabel,
            selected = onSaleSelected,
            glassyFill = glassyFill,
            showChevron = false,
            onClick = onOnSaleToggle,
        )
        StoreFilterChip(
            label = priceLabel,
            selected = false,
            glassyFill = glassyFill,
            showChevron = true,
            onClick = onPriceClick,
        )
        StoreFilterChip(
            label = inStockLabel,
            selected = inStockSelected,
            glassyFill = glassyFill,
            showChevron = false,
            onClick = onInStockToggle,
        )
    }
}

@Composable
private fun StoreFilterIconButton(
    contentDescription: String,
    fill: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(FilterChipHeight)
            .clip(CircleShape)
            .background(fill)
            .border(1.dp, fill.copy(alpha = 1f), CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_filter_sliders),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun StoreFilterChip(
    label: String,
    selected: Boolean,
    glassyFill: Color,
    showChevron: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) FilterChipSelectedFill else glassyFill
    val fg = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    val border = if (selected) FilterChipSelectedFill else glassyFill.copy(alpha = 1f)
    Row(
        modifier = Modifier
            .height(FilterChipHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(percent = 50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(
                start = VitranSpacing.lg,
                end = if (showChevron) VitranSpacing.sm else VitranSpacing.lg,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
            maxLines = 1,
        )
        if (showChevron) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_down),
                contentDescription = null,
                size = 12.dp,
                tint = fg,
            )
        }
    }
}
