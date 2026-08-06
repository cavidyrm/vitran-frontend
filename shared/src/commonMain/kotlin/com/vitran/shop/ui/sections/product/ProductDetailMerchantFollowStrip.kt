package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_star_filled
import vitranshop.shared.generated.resources.product_detail_follow
import vitranshop.shared.generated.resources.product_detail_follow_a11y
import vitranshop.shared.generated.resources.product_detail_merchant_rating_a11y

/**
 * Merchant Follow strip under Reviews (shop.app cover 120px + meta + Follow).
 *
 * Follow is a visual placeholder — no auth in the mock phase.
 */
@Composable
fun ProductDetailMerchantFollowStrip(
    merchant: ProductDetailMerchant,
    coverImageUrl: String,
    modifier: Modifier = Modifier,
    onFollowClick: () -> Unit = {},
) {
    val followLabel = stringResource(Res.string.product_detail_follow)
    val followA11y = stringResource(Res.string.product_detail_follow_a11y, merchant.name)
    val ratingA11y = stringResource(
        Res.string.product_detail_merchant_rating_a11y,
        merchant.ratingLabel,
        merchant.reviewCountLabel,
    )
    val cardShape = RoundedCornerShape(VitranRadius.extraLarge)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .border(VitranSize.borderHairline, CardBorder, cardShape)
            .background(MaterialTheme.colorScheme.surface, cardShape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(CoverHeight)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(coverImageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CoverHeight),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.lg),
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
                        .size(LogoSize)
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
                            size = StarSize,
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
            Text(
                text = followLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .border(
                        width = VitranSize.borderHairline,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(percent = 50),
                    )
                    .clickable(role = Role.Button, onClick = onFollowClick)
                    .semantics { contentDescription = followA11y }
                    .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.sm),
            )
        }
    }
}

/** shop.app cover band `h-[120px]`. */
private val CoverHeight = 120.dp
private val LogoSize = 40.dp
private val StarSize = 12.dp
/** shop.app `border-border-image`. */
private val CardBorder = androidx.compose.ui.graphics.Color(0x1A05294D)
