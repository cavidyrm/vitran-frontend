package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_star_filled
import vitranshop.shared.generated.resources.product_detail_merchant_more_a11y
import vitranshop.shared.generated.resources.product_detail_merchant_rating_a11y
import vitranshop.shared.generated.resources.product_detail_visit_store
import vitranshop.shared.generated.resources.product_detail_visit_store_a11y

/**
 * Merchant / store row on Product Detail (shop.app `shop-info-header`).
 *
 * - Compact / mid (`< lg`): larger logo + Visit store pill.
 * - Large (`≥ lg`): smaller logo + stacked name/rating + more menu; no Visit
 *   (Visit is `lg:hidden` on shop.app).
 *
 * Visit store / more menu are visual placeholders in the mock phase.
 */
@Composable
fun ProductDetailMerchantHeader(
    merchant: ProductDetailMerchant,
    modifier: Modifier = Modifier,
    showVisitStore: Boolean = true,
    showMoreMenu: Boolean = false,
    logoSize: Dp = if (showVisitStore) MerchantLogoCompact else MerchantLogoDesktop,
    onVisitStoreClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    val visitLabel = stringResource(Res.string.product_detail_visit_store)
    val visitA11y = stringResource(Res.string.product_detail_visit_store_a11y, merchant.name)
    val ratingA11y = stringResource(
        Res.string.product_detail_merchant_rating_a11y,
        merchant.ratingLabel,
        merchant.reviewCountLabel,
    )
    val moreA11y = stringResource(Res.string.product_detail_merchant_more_a11y)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            modifier = Modifier
                .weight(1f, fill = false)
                .semantics(mergeDescendants = true) {
                    contentDescription = "${merchant.name}. $ratingA11y"
                },
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(merchant.logoUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(logoSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = VitranSize.borderHairline,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = CircleShape,
                    ),
            )
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = merchant.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                ) {
                    Text(
                        text = merchant.ratingLabel,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                        ),
                        maxLines = 1,
                    )
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_star_filled),
                        contentDescription = null,
                        size = MerchantStarSize,
                        tint = VitranTheme.extraColors.star,
                    )
                    Text(
                        text = merchant.reviewCountLabel,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                        maxLines = 1,
                    )
                }
            }
        }

        when {
            showVisitStore -> {
                Spacer(modifier = Modifier.width(VitranSpacing.sm))
                Text(
                    text = visitLabel,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .border(
                            width = VitranSize.borderHairline,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(percent = 50),
                        )
                        .clickable(
                            role = Role.Button,
                            onClick = onVisitStoreClick,
                        )
                        .semantics { contentDescription = visitA11y }
                        .padding(horizontal = VitranSpacing.sm, vertical = VitranSpacing.sm),
                )
            }
            showMoreMenu -> {
                Box(
                    modifier = Modifier
                        .size(MoreMenuSize)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onMoreClick)
                        .semantics { contentDescription = moreA11y },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "⋯",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

/** shop.app compact sticky merchant logo ≈ 44dp. */
private val MerchantLogoCompact = 44.dp
/** shop.app desktop in-column logo ≈ 32dp. */
private val MerchantLogoDesktop = 32.dp
private val MerchantStarSize = 12.dp
private val MoreMenuSize = 44.dp
