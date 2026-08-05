package com.vitran.shop.ui.components

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.sections.categories.CategoriesProduct
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranShapes
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_product_card_a11y
import vitranshop.shared.generated.resources.categories_product_card_save_a11y
import vitranshop.shared.generated.resources.ic_nav_saved
import vitranshop.shared.generated.resources.ic_star_filled

/**
 * shop.app desktop reference width (~6-up on a ~1200 track).
 * Compact / mid widths are computed by the product row carousel.
 */
val CategoriesProductCardWidthDesktop = 188.dp

/** shop.app `shadow-s` on product image. */
private val ImageShadowElevation = 2.dp
private val ImageShadowColor = Color.Black.copy(alpha = 0.06f)

/**
 * shop.app `border-border-image` — `rgba(5, 41, 77, 0.1)`.
 */
private val ImageBorder = Color(0x1A05294D)

/** Soft fill while product images load. */
private val ImagePlaceholder = Color(0xFFF2F4F5)

/** shop.app `bg-bg-overlay-fixed-icon` on save control. */
private val SaveButtonFill = Color(0x4D282828)

/** shop.app compare-at tertiary — `rgba(0, 0, 0, 0.56)`. */
private val CompareAtColor = Color.Black.copy(alpha = 0.56f)

private val SaveButtonSize = 32.dp
private val SaveIconSize = 16.dp
private val StarSize = 12.dp
private val CaptionSize = 12.sp
private val CaptionLine = 16.sp
private val BadgeTextSize = 10.sp

/**
 * Product tile used by Categories “Top rated / New in” carousels
 * (shop.app `/categories` feed card).
 *
 * Width follows the parent carousel (shop.app
 * `calc(100% / n - gap * (n - 1) / n)`): ~151 compact / ~188 desktop.
 * Square image, radius 20, gap 8 to meta; optional sale badge and save overlay.
 */
@Composable
fun CategoriesProductCard(
    product: CategoriesProduct,
    modifier: Modifier = Modifier,
    cardWidth: Dp = CategoriesProductCardWidthDesktop,
    onClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
) {
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
        label = "categoriesProductHover",
    )
    val a11y = stringResource(
        Res.string.categories_product_card_a11y,
        product.title,
        product.storeName,
        product.priceLabel,
    )
    val saveA11y = stringResource(Res.string.categories_product_card_save_a11y)
    val placeholder = remember { ColorPainter(ImagePlaceholder) }
    val imageShape = RoundedCornerShape(VitranRadius.xl)
    val captionStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = CaptionSize,
        fontWeight = FontWeight.Normal,
        lineHeight = CaptionLine,
    )
    val captionBoldStyle = captionStyle.copy(fontWeight = FontWeight.Bold)

    Column(
        modifier = modifier
            .width(cardWidth)
            .hoverable(interaction)
            .semantics { contentDescription = a11y },
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = ImageShadowElevation,
                        shape = imageShape,
                        clip = false,
                        ambientColor = ImageShadowColor,
                        spotColor = ImageShadowColor,
                    )
                    .clip(imageShape)
                    .background(ImagePlaceholder, imageShape)
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    ),
            ) {
                AsyncImage(
                    model = resolveNetworkImageUrl(product.imageUrl),
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(width = 1.dp, color = ImageBorder, shape = imageShape),
                )
            }

            product.discountLabel?.let { label ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(VitranSpacing.md)
                        .background(
                            color = VitranTheme.extraColors.saleBadge,
                            shape = VitranShapes.pill,
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = BadgeTextSize,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 13.sp,
                        ),
                        maxLines = 1,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(VitranSpacing.md)
                    .size(SaveButtonSize)
                    .clip(CircleShape)
                    .background(SaveButtonFill)
                    .clickable(
                        role = Role.Button,
                        onClick = onSaveClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_nav_saved),
                    contentDescription = saveA11y,
                    size = SaveIconSize,
                    tint = Color.White,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(start = VitranSpacing.xs),
        ) {
            Text(
                text = product.storeName,
                color = MaterialTheme.colorScheme.onSurface,
                style = captionStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = product.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = captionBoldStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val rating = product.rating
            val reviewLabel = product.reviewCountLabel
            if (rating != null && reviewLabel != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val filled = rating.toInt().coerceIn(0, 5)
                        repeat(filled) {
                            VitranIcon(
                                painter = painterResource(Res.drawable.ic_star_filled),
                                contentDescription = null,
                                size = StarSize,
                                tint = VitranTheme.extraColors.star,
                            )
                        }
                    }
                    Text(
                        text = reviewLabel,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = captionStyle.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                    )
                }
            }
            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                Text(
                    text = product.priceLabel,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = captionBoldStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                product.compareAtPriceLabel?.let { compare ->
                    Text(
                        text = compare,
                        color = CompareAtColor,
                        style = captionStyle.copy(
                            textDecoration = TextDecoration.LineThrough,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 220, heightDp = 320)
@Composable
private fun CategoriesProductCardPreview() {
    VitranTheme {
        CategoriesProductCard(
            product = CategoriesProduct(
                id = "preview",
                storeName = "Caraway",
                title = "Baking Sheet Duo",
                imageUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
                rating = 5f,
                reviewCountLabel = "(۲.۱K)",
                priceLabel = "۴٬۴۳۰٬۰۰۰ تومان",
                discountLabel = "۱۰٪ تخفیف",
                compareAtPriceLabel = "۴٬۹۰۰٬۰۰۰ تومان",
            ),
            modifier = Modifier.padding(VitranSpacing.lg),
        )
    }
}
