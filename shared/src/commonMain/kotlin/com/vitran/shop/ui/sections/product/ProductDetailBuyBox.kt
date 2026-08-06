package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.OnSurfaceSecondary
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_favorite_outline
import vitranshop.shared.generated.resources.ic_share
import vitranshop.shared.generated.resources.ic_star_filled
import vitranshop.shared.generated.resources.product_detail_option_selected_a11y
import vitranshop.shared.generated.resources.product_detail_quantity
import vitranshop.shared.generated.resources.product_detail_quantity_decrease_a11y
import vitranshop.shared.generated.resources.product_detail_quantity_increase_a11y
import vitranshop.shared.generated.resources.product_detail_save
import vitranshop.shared.generated.resources.product_detail_share

/**
 * PDP buy box: title, rating, badges, price, dynamic options, quantity,
 * Save | Share (visual placeholders). No Add to cart / Buy now.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailBuyBox(
    product: ProductDetailMock,
    modifier: Modifier = Modifier,
) {
    var selectedByOption by remember(product.id) {
        mutableStateOf(product.options.associate { it.id to it.selectedIndex })
    }
    var quantity by remember(product.id) { mutableIntStateOf(1) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs)) {
        Text(
                text = product.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )

            val rating = product.productRating
            val ratingCount = product.productRatingCountLabel
            if (rating != null && ratingCount != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val filled = rating.roundToInt().coerceIn(0, 5)
                        repeat(filled) {
                            VitranIcon(
                                painter = painterResource(Res.drawable.ic_star_filled),
                                contentDescription = null,
                                size = ProductStarSize,
                                tint = VitranTheme.extraColors.star,
                            )
                        }
                    }
                    Text(
                        text = ratingCount,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline,
                        ),
                    )
                }
            }

            val hasBadges =
                product.socialProofLabel != null || product.inventoryLabel != null
            if (hasBadges) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                    modifier = Modifier.padding(top = VitranSpacing.xs),
                ) {
                    product.socialProofLabel?.let { label ->
                        BadgePill(
                            text = label,
                            containerColor = SocialProofFill,
                        )
                    }
                    product.inventoryLabel?.let { label ->
                        BadgePill(
                            text = label,
                            containerColor = ScarcityFill,
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Text(
                text = product.priceLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                ),
            )
            product.compareAtPriceLabel?.let { compare ->
                Text(
                    text = compare,
                    color = OnSurfaceSecondary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.LineThrough,
                    ),
                )
            }
        }

        if (product.options.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg)) {
                product.options.forEach { option ->
                    val selectedIndex = selectedByOption[option.id] ?: option.selectedIndex
                    ProductOptionGroup(
                        option = option,
                        selectedIndex = selectedIndex,
                        onSelect = { index ->
                            selectedByOption = selectedByOption + (option.id to index)
                        },
                    )
                }
            }
        }

        QuantityStepper(
            quantity = quantity,
            onDecrease = { if (quantity > 1) quantity -= 1 },
            onIncrease = { if (quantity < 99) quantity += 1 },
        )

        // shop.app: Save | Share `_button_outlined_l` — gap-8, h-44, p-12, icon 20, gap-4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            ShopOutlineActionButton(
                text = stringResource(Res.string.product_detail_save),
                onClick = {},
                modifier = Modifier.weight(1f),
                icon = {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_favorite_outline),
                        contentDescription = null,
                        size = ActionIconSize,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                },
            )
            ShopOutlineActionButton(
                text = stringResource(Res.string.product_detail_share),
                onClick = {},
                modifier = Modifier.weight(1f),
                icon = {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_share),
                        contentDescription = null,
                        size = ActionIconSize,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                },
            )
        }
    }
}

@Composable
private fun BadgePill(
    text: String,
    containerColor: Color,
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(containerColor)
            .padding(horizontal = VitranSpacing.sm, vertical = VitranSpacing.xs),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductOptionGroup(
    option: ProductOption,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    val selectedLabel = option.values.getOrNull(selectedIndex)?.label.orEmpty()
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = option.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                ),
            )
            Text(
                text = selectedLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            )
        }
        if (option.usesSwatches) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                option.values.forEachIndexed { index, value ->
                    val selected = index == selectedIndex
                    val a11y = stringResource(
                        Res.string.product_detail_option_selected_a11y,
                        option.name,
                        value.label,
                    )
                    Box(
                        modifier = Modifier
                            .size(SwatchSelectedOuter)
                            .clip(CircleShape)
                            .then(
                                if (selected) {
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                } else {
                                    Modifier
                                },
                            )
                            .padding(2.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.RadioButton, onClick = { onSelect(index) })
                            .semantics { contentDescription = a11y },
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            value.swatchImageUrl != null -> {
                                AsyncImage(
                                    model = resolveNetworkImageUrl(value.swatchImageUrl),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(SwatchSize)
                                        .clip(CircleShape),
                                )
                            }
                            value.swatchColorArgb != null -> {
                                Box(
                                    modifier = Modifier
                                        .size(SwatchSize)
                                        .clip(CircleShape)
                                        .background(Color(value.swatchColorArgb))
                                        .border(
                                            width = VitranSize.borderHairline,
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = CircleShape,
                                        ),
                                )
                            }
                        }
                    }
                }
            }
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                option.values.forEachIndexed { index, value ->
                    val selected = index == selectedIndex
                    val a11y = stringResource(
                        Res.string.product_detail_option_selected_a11y,
                        option.name,
                        value.label,
                    )
                    Text(
                        text = value.label,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .border(
                                width = if (selected) 1.dp else VitranSize.borderHairline,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                },
                                shape = RoundedCornerShape(percent = 50),
                            )
                            .clickable(role = Role.RadioButton, onClick = { onSelect(index) })
                            .semantics { contentDescription = a11y }
                            .padding(horizontal = VitranSpacing.lg, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    val decreaseA11y = stringResource(Res.string.product_detail_quantity_decrease_a11y)
    val increaseA11y = stringResource(Res.string.product_detail_quantity_increase_a11y)
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        Text(
            text = stringResource(Res.string.product_detail_quantity),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            ),
        )
        // Keep − 1 + in LTR order (numeric control, not prose).
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .border(
                        width = VitranSize.borderHairline,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(percent = 50),
                    )
                    .padding(horizontal = VitranSpacing.sm, vertical = VitranSpacing.xs),
            ) {
                Text(
                    text = "−",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .size(QtyButtonSize)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onDecrease)
                        .semantics { contentDescription = decreaseA11y }
                        .padding(VitranSpacing.sm),
                )
                Text(
                    text = quantity.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(28.dp),
                )
                Text(
                    text = "+",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .size(QtyButtonSize)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onIncrease)
                        .semantics { contentDescription = increaseA11y }
                        .padding(VitranSpacing.sm),
                )
            }
        }
    }
}

@Composable
private fun ShopOutlineActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .height(ActionButtonHeight)
            .shopShadowS()
            .clip(shape)
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 1.dp,
                // shop.app ::after stroke: rgba(24, 59, 78, 0.06)
                color = OutlineActionBorder,
                shape = shape,
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(ActionButtonPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = VitranSpacing.xs,
            alignment = Alignment.CenterHorizontally,
        ),
    ) {
        icon()
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 20.sp,
            ),
            maxLines = 1,
        )
    }
}

/**
 * shop.app `shadow-s`: `0 2px 8px rgba(0,0,0,0.06)` on outlined Save/Share.
 */
private fun Modifier.shopShadowS(): Modifier = drawBehind {
    val radius = size.height / 2f
    val yOffset = 2.dp.toPx()
    for (i in 1..5) {
        val spread = i * 1.6.dp.toPx()
        val alpha = 0.016f * (1f - (i - 1) / 5f)
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = -spread * 0.35f,
                    top = yOffset - spread * 0.1f,
                    right = size.width + spread * 0.35f,
                    bottom = size.height + yOffset + spread * 0.9f,
                    cornerRadius = CornerRadius(radius + spread * 0.2f),
                ),
            )
        }
        drawPath(path = path, color = Color.Black.copy(alpha = alpha), style = Fill)
    }
}

private val ProductStarSize = 14.dp
private val SwatchSize = 34.dp
private val SwatchSelectedOuter = 38.dp
private val QtyButtonSize = 36.dp
/** shop.app outlined Save/Share `_button_outlined_l` ≈ 44dp. */
private val ActionButtonHeight = 44.dp
private val ActionButtonPadding = 12.dp
private val ActionIconSize = 20.dp
private val SocialProofFill = Color(0xFFF2F4F5)
private val ScarcityFill = Color(0xFFFFECE9)
/** shop.app outlined button hairline. */
private val OutlineActionBorder = Color(0x0F183B4E)
