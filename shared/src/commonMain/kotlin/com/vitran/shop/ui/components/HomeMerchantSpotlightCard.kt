package com.vitran.shop.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.sections.home.HomeShopCard
import com.vitran.shop.ui.sections.home.HomeShopProductPeek
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.home_merchant_card_a11y
import vitranshop.shared.generated.resources.home_merchant_product_peek_a11y
import vitranshop.shared.generated.resources.ic_star_filled

/** shop.app `h-[397px] w-[330px]`. */
private val MerchantCardWidth = 330.dp
private val MerchantCardHeight = 397.dp

/** shop.app `p-space-12`. */
private val MerchantContentPad = VitranSpacing.md

/** shop.app wordmark `max-h-[74px] min-h-space-40 max-w-[195px]`. */
private val WordmarkMaxHeight = 74.dp
private val WordmarkMinHeight = VitranSpacing.xxxl + VitranSpacing.sm // 40
private val WordmarkMaxWidth = 195.dp

/** shop.app product peek `aspect-square` ~98dp with `rounded-radius-20`. */
private val PeekSize = 98.dp
private val PeekGap = 6.dp

/** shop.app `shadow-md` soft card elevation. */
private val MerchantCardShadowElevation = 4.dp
private val MerchantCardShadowColor = Color.Black.copy(alpha = 0.08f)

/** shop.app `bg-bg-overlay-fixed-dark-20`. */
private val CoverOverlay = Color.Black.copy(alpha = 0.20f)

/** shop.app star uses `currentColor` with the overlay text. */
private val StarSize = 11.dp

/**
 * shop.app Home merchant spotlight card
 * (`data-testid="large-product-focused-merchant-card"`).
 *
 * Cover + brand tint, centered wordmark, name/rating overlay, and three product peeks.
 */
@Composable
fun HomeMerchantSpotlightCard(
    shop: HomeShopCard,
    modifier: Modifier = Modifier,
    onShopClick: () -> Unit = {},
    onProductClick: (HomeShopProductPeek) -> Unit = {},
) {
    val a11y = stringResource(
        Res.string.home_merchant_card_a11y,
        shop.name,
        shop.ratingLabel,
    )
    val cardInteraction = remember { MutableInteractionSource() }
    val cardHovered by cardInteraction.collectIsHoveredAsState()
    val coverScale by animateFloatAsState(
        targetValue = if (cardHovered) {
            VitranAnimation.MerchantSpotlight.COVER_HOVER_SCALE
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = VitranAnimation.MerchantSpotlight.COVER_HOVER_MS,
            easing = VitranAnimation.MerchantSpotlight.HoverEasing,
        ),
        label = "merchantCoverHover",
    )
    val logoScale by animateFloatAsState(
        targetValue = if (cardHovered) {
            VitranAnimation.MerchantSpotlight.LOGO_HOVER_SCALE
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = VitranAnimation.MerchantSpotlight.LOGO_HOVER_MS,
            easing = VitranAnimation.MerchantSpotlight.HoverEasing,
        ),
        label = "merchantLogoHover",
    )
    val overlayText = if (shop.useLightText) Color.White else Color.Black
    val shape = RoundedCornerShape(VitranRadius.extraLarge)
    val coverPlaceholder = ColorPainter(shop.brandColor)

    Box(
        modifier = modifier
            .size(width = MerchantCardWidth, height = MerchantCardHeight)
            .shadow(
                elevation = MerchantCardShadowElevation,
                shape = shape,
                clip = false,
                ambientColor = MerchantCardShadowColor,
                spotColor = MerchantCardShadowColor,
            )
            .clip(shape)
            .background(shop.brandColor)
            .hoverable(cardInteraction)
            .semantics { contentDescription = a11y },
    ) {
        // Cover layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(shop.coverUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = coverPlaceholder,
                error = coverPlaceholder,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = coverScale
                        scaleY = coverScale
                    },
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CoverOverlay),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.20f to Color.Transparent,
                                0.80f to shop.brandColor,
                                1.0f to shop.brandColor,
                            ),
                        ),
                    ),
            )
        }

        // Interactive content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MerchantContentPad)
                .zIndex(2f),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = cardInteraction,
                        indication = null,
                        role = Role.Button,
                        onClick = onShopClick,
                    ),
            ) {
                Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = shop.name,
                        color = overlayText,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        VitranIcon(
                            painter = painterResource(Res.drawable.ic_star_filled),
                            contentDescription = null,
                            size = StarSize,
                            tint = overlayText,
                        )
                        Text(
                            text = shop.ratingLabel,
                            color = overlayText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 16.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                if (shop.logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = resolveNetworkImageUrl(shop.logoUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .widthIn(max = WordmarkMaxWidth)
                            .heightIn(min = WordmarkMinHeight, max = WordmarkMaxHeight)
                            .graphicsLayer {
                                scaleX = logoScale
                                scaleY = logoScale
                            },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PeekGap, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                shop.products.forEach { peek ->
                    MerchantProductPeek(
                        peek = peek,
                        shopName = shop.name,
                        onClick = { onProductClick(peek) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MerchantProductPeek(
    peek: HomeShopProductPeek,
    shopName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val a11y = stringResource(Res.string.home_merchant_product_peek_a11y, shopName)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (hovered) {
            VitranAnimation.MerchantSpotlight.PEEK_HOVER_SCALE
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = VitranAnimation.MerchantSpotlight.LOGO_HOVER_MS,
            easing = VitranAnimation.MerchantSpotlight.HoverEasing,
        ),
        label = "merchantPeekHover",
    )
    val shape = RoundedCornerShape(VitranRadius.xl)
    val placeholder = ColorPainter(Color(0xFFF2F4F5))

    Box(
        modifier = modifier
            .size(PeekSize)
            .shadow(
                elevation = 2.dp,
                shape = shape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            )
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .hoverable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = a11y },
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(peek.imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 420)
@Composable
private fun HomeMerchantSpotlightCardPreview() {
    VitranTheme {
        HomeMerchantSpotlightCard(
            shop = HomeShopCard(
                id = "wildbird",
                name = "WildBird",
                ratingLabel = "۴٫۸ (۸٫۶ هزار)",
                coverUrl = "",
                logoUrl = "",
                brandColor = Color(0xFFA5896B),
                useLightText = true,
                products = listOf(
                    HomeShopProductPeek("1", "", title = "Acadian Wrap"),
                    HomeShopProductPeek("2", "", title = "Desert Lark Wrap"),
                    HomeShopProductPeek("3", "", title = "Flutter Wrap"),
                ),
            ),
            modifier = Modifier.padding(VitranSpacing.lg),
        )
    }
}
