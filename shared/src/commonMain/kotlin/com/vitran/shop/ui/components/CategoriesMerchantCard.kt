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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.sections.categories.CategoriesMerchantShop
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_merchant_card_a11y
import vitranshop.shared.generated.resources.categories_merchant_image_next_a11y
import vitranshop.shared.generated.resources.categories_merchant_image_prev_a11y
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_star_filled

/** shop.app `shadow-s` on merchant media. */
private val ImageShadowElevation = 2.dp
private val ImageShadowColor = Color.Black.copy(alpha = 0.06f)

/** shop.app `border-border-image` — `rgba(5, 41, 77, 0.1)`. */
private val ImageBorder = Color(0x1A05294D)

private val ImagePlaceholder = Color(0xFFF2F4F5)

/** shop.app `size-space-6` pagination dots. */
private val DotSize = 6.dp

/** shop.app `gap-space-2` between dots. */
private val DotGap = 2.dp

private val DotActive = Color.White.copy(alpha = 0.75f)
private val DotInactive = Color.White.copy(alpha = 0.40f)

/** shop.app hover chevron chip (~32). */
private val MediaChevronSize = 32.dp
private val MediaChevronGlyph = 16.dp
private val MediaChevronInset = VitranSpacing.lg

private val StarSize = 12.dp
private val MetaNameSize = 14.sp
private val MetaNameLine = 18.sp
private val MetaCaptionSize = 12.sp
private val MetaCaptionLine = 16.sp

/**
 * shop.app Categories `product-focused-merchant-card`:
 * square multi-image media + logo / name / rating footer.
 *
 * Compact: snap horizontal pager for the 4 images.
 * Desktop: hover scale + hover-revealed prev/next chevrons + fade index.
 */
@Composable
fun CategoriesMerchantCard(
    shop: CategoriesMerchantShop,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current
    val images = shop.imageUrls.ifEmpty { listOf("") }
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
        label = "merchantCardHover",
    )
    val a11y = stringResource(
        Res.string.categories_merchant_card_a11y,
        shop.name,
        shop.ratingLabel,
    )
    val placeholder = remember { ColorPainter(ImagePlaceholder) }
    val imageShape = RoundedCornerShape(VitranRadius.xl)

    var desktopIndex by remember(shop.id) { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        pageCount = { images.size },
    )
    val scope = rememberCoroutineScope()
    val activeIndex = if (isDesktop) desktopIndex else pagerState.currentPage

    LaunchedEffect(isDesktop, shop.id) {
        if (isDesktop) {
            desktopIndex = 0
        } else if (pagerState.currentPage != 0) {
            pagerState.scrollToPage(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .hoverable(interaction)
            .semantics { contentDescription = a11y },
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(
                    elevation = ImageShadowElevation,
                    shape = imageShape,
                    clip = false,
                    ambientColor = ImageShadowColor,
                    spotColor = ImageShadowColor,
                )
                .clip(imageShape)
                .background(ImagePlaceholder, imageShape),
        ) {
            if (isDesktop) {
                AsyncImage(
                    model = resolveNetworkImageUrl(images[desktopIndex.coerceIn(0, images.lastIndex)]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = placeholder,
                    error = placeholder,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = imageScale
                            scaleY = imageScale
                        }
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        ),
                )
                if (hovered && images.size > 1) {
                    MerchantMediaChevron(
                        forward = false,
                        onClick = {
                            desktopIndex =
                                (desktopIndex - 1 + images.size) % images.size
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = MediaChevronInset),
                    )
                    MerchantMediaChevron(
                        forward = true,
                        onClick = {
                            desktopIndex = (desktopIndex + 1) % images.size
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = MediaChevronInset),
                    )
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    AsyncImage(
                        model = resolveNetworkImageUrl(images[page]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = placeholder,
                        error = placeholder,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                role = Role.Button,
                                onClick = onClick,
                            ),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(width = 1.dp, color = ImageBorder, shape = imageShape),
            )

            if (images.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = VitranSpacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(DotGap),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(images.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(DotSize)
                                .clip(CircleShape)
                                .background(
                                    if (index == activeIndex) DotActive else DotInactive,
                                )
                                .then(
                                    if (!isDesktop) {
                                        Modifier.clickable(
                                            indication = null,
                                            interactionSource = remember {
                                                MutableInteractionSource()
                                            },
                                        ) {
                                            scope.launch { pagerState.animateScrollToPage(index) }
                                        }
                                    } else {
                                        Modifier
                                    },
                                ),
                        )
                    }
                }
            }
        }

        MerchantCardMeta(
            shop = shop,
            isDesktop = isDesktop,
            onClick = onClick,
        )
    }
}

@Composable
private fun MerchantCardMeta(
    shop: CategoriesMerchantShop,
    isDesktop: Boolean,
    onClick: () -> Unit,
) {
    val placeholder = remember { ColorPainter(ImagePlaceholder) }
    val nameStyle = if (isDesktop) {
        MaterialTheme.typography.bodyMedium.copy(
            fontSize = MetaNameSize,
            fontWeight = FontWeight.Normal,
            lineHeight = MetaNameLine,
        )
    } else {
        MaterialTheme.typography.labelMedium.copy(
            fontSize = MetaCaptionSize,
            fontWeight = FontWeight.Bold,
            lineHeight = MetaCaptionLine,
        )
    }
    val ratingStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = MetaNameSize,
        fontWeight = FontWeight.Normal,
        lineHeight = MetaNameLine,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = VitranSpacing.xs),
        verticalAlignment = if (isDesktop) Alignment.CenterVertically else Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(VitranSize.avatarMedium)
                .clip(CircleShape)
                .background(ImagePlaceholder, CircleShape)
                .border(width = 1.dp, color = ImageBorder, shape = CircleShape),
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(shop.logoUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (isDesktop) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                Text(
                    text = shop.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = nameStyle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                MerchantRating(
                    ratingLabel = shop.ratingLabel,
                    style = ratingStyle,
                )
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shop.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = nameStyle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                MerchantRating(
                    ratingLabel = shop.ratingLabel,
                    style = ratingStyle,
                )
            }
        }
    }
}

@Composable
private fun MerchantRating(
    ratingLabel: String,
    style: TextStyle,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = ratingLabel,
            color = MaterialTheme.colorScheme.onSurface,
            style = style,
            maxLines = 1,
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_star_filled),
            contentDescription = null,
            size = StarSize,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun MerchantMediaChevron(
    forward: Boolean,
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
            .size(MediaChevronSize)
            .shadow(
                elevation = ImageShadowElevation,
                shape = CircleShape,
                clip = false,
                ambientColor = ImageShadowColor,
                spotColor = ImageShadowColor,
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f), CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = a11y,
            modifier = Modifier.graphicsLayer {
                scaleX = if (flipForRtl) -1f else 1f
                // Match shop.app ~70% opacity on hover chevrons.
                alpha = 0.7f
            },
            size = MediaChevronGlyph,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 200, heightDp = 280)
@Composable
private fun CategoriesMerchantCardPreview() {
    VitranTheme {
        CategoriesMerchantCard(
            shop = CategoriesMerchantShop(
                id = "preview",
                name = "Comfrt",
                ratingLabel = "۴.۸",
                logoUrl = "https://cdn.shopify.com/s/files/1/0569/4029/8284/files/D_1.png?v=1655843709&width=64",
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=384",
                    "https://cdn.shopify.com/s/files/1/0932/1356/files/20260128_ECOMM_SS26_20_WOMENSOFFSHOULDERTEE_WHITE_13216.jpg?v=1771343002&width=384",
                    "https://cdn.shopify.com/s/files/1/0664/9036/8232/files/SGM1E38TDSMDW_9.jpg?v=1784754651&width=384",
                    "https://cdn.shopify.com/s/files/1/0037/3807/5202/files/DOLCEVITA-SANDAL_JULIO_TORTOISE_VINYL-02.jpg?v=1758816127&width=384",
                ),
            ),
            modifier = Modifier.padding(VitranSpacing.lg),
        )
    }
}
