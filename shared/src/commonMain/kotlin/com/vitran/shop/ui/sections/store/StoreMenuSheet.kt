package com.vitran.shop.ui.sections.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.sections.product.ProductDetailSideSheet
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_arrow_right
import vitranshop.shared.generated.resources.ic_close
import vitranshop.shared.generated.resources.ic_share
import vitranshop.shared.generated.resources.ic_star_filled
import vitranshop.shared.generated.resources.ic_thumb_up
import vitranshop.shared.generated.resources.product_detail_reviews
import vitranshop.shared.generated.resources.product_detail_reviews_helpful
import vitranshop.shared.generated.resources.store_menu_close_a11y
import vitranshop.shared.generated.resources.store_menu_contact
import vitranshop.shared.generated.resources.store_menu_open_reviews_a11y
import vitranshop.shared.generated.resources.store_menu_policies
import vitranshop.shared.generated.resources.store_menu_share_a11y
import vitranshop.shared.generated.resources.store_menu_visit_online_store
import vitranshop.shared.generated.resources.store_rating_a11y

/**
 * shop.app store-menu text/frost tokens.
 * Dark brands (`useLightText`) use white copy on brand fill — measured Patrick Ta
 * `text-text` white on `rgb(101,81,71)`. Light brands (SACHEU) use near-black.
 */
@Immutable
private data class StoreMenuColors(
    val content: Color,
    val muted: Color,
    val frost: Color,
    val chrome: Color,
)

private val LocalStoreMenuColors = staticCompositionLocalOf<StoreMenuColors> {
    error("StoreMenuColors not provided")
}

@Composable
private fun rememberStoreMenuColors(useLightText: Boolean): StoreMenuColors =
    remember(useLightText) {
        if (useLightText) {
            StoreMenuColors(
                content = Color.White,
                muted = Color.White.copy(alpha = 0.65f),
                // shop.app dark store cards `rgba(255,255,255,0.1)`
                frost = Color.White.copy(alpha = 0.1f),
                chrome = Color.White.copy(alpha = 0.3f),
            )
        } else {
            StoreMenuColors(
                content = Color(0xFF1A1A1A),
                muted = Color(0xFF1A1A1A).copy(alpha = 0.65f),
                // shop.app light store cards `rgba(255,255,255,0.2)`
                frost = Color.White.copy(alpha = 0.2f),
                chrome = Color(0xFF282828).copy(alpha = 0.3f),
            )
        }
    }

/**
 * shop.app store menu side sheet — opens from the store-menu pill.
 *
 * Shell animation: [ProductDetailSideSheet] with [StoreMock.brandColor] panel fill
 * (measured `rgb(208,181,174)` on SACHEU + backdrop blur).
 */
@Composable
fun StoreMenuSheet(
    store: StoreMock,
    menu: StoreMenuMock,
    onDismiss: () -> Unit,
    onOpenReviews: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onPolicyClick: (StoreMenuPolicy) -> Unit = {},
    onContactClick: (StoreMenuContactLink) -> Unit = {},
    onVisitOnlineStoreClick: () -> Unit = {},
) {
    val closeA11y = stringResource(Res.string.store_menu_close_a11y)
    val shareA11y = stringResource(Res.string.store_menu_share_a11y, store.name)
    val reviewsTitle = stringResource(Res.string.product_detail_reviews)
    val openReviewsA11y = stringResource(Res.string.store_menu_open_reviews_a11y)
    val policiesTitle = stringResource(Res.string.store_menu_policies)
    val contactTitle = stringResource(Res.string.store_menu_contact)
    val visitLabel = stringResource(Res.string.store_menu_visit_online_store)
    val helpfulLabel = stringResource(Res.string.product_detail_reviews_helpful)
    val ratingA11y = stringResource(
        Res.string.store_rating_a11y,
        store.ratingLabel,
        store.reviewCountLabel,
    )
    val colors = rememberStoreMenuColors(store.useLightText)

    ProductDetailSideSheet(
        onDismiss = onDismiss,
        panelColor = store.brandColor,
        fromPhysicalRight = true,
    ) { onClose ->
        CompositionLocalProvider(LocalStoreMenuColors provides colors) {
            val menuColors = LocalStoreMenuColors.current
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = PanelPad,
                            end = PanelPad,
                            top = ScrollTopPad,
                            bottom = PanelPad,
                        ),
                ) {
                    StoreMenuIdentity(
                        store = store,
                        ratingA11y = ratingA11y,
                    )

                    Spacer(modifier = Modifier.height(VitranSpacing.xxl))

                    StoreMenuReviewsBlock(
                        title = reviewsTitle,
                        openA11y = openReviewsA11y,
                        averageLabel = store.ratingLabel,
                        ratingsCountLabel = menu.ratingsCountLabel,
                        mediaUrls = menu.mediaUrls,
                        teasers = menu.teaserReviews,
                        helpfulLabel = helpfulLabel,
                        onOpenReviews = onOpenReviews,
                    )

                    Spacer(modifier = Modifier.height(SectionGap))

                    StoreMenuFrostedCard {
                        Text(
                            text = policiesTitle,
                            color = menuColors.content,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = SectionTitleSize,
                                lineHeight = 42.sp,
                            ),
                        )
                        menu.policies.forEach { policy ->
                            StoreMenuPolicyRow(
                                policy = policy,
                                onClick = { onPolicyClick(policy) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(SectionGap))

                    StoreMenuFrostedCard(horizontalPad = ContactCardHPad) {
                        Text(
                            text = contactTitle,
                            color = menuColors.content,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = SectionTitleSize,
                                lineHeight = 42.sp,
                            ),
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = ContactLinksBlockGap),
                            verticalArrangement = Arrangement.spacedBy(ContactLinksBlockGap),
                        ) {
                            menu.contactLinks.forEach { link ->
                                StoreMenuContactRow(
                                    link = link,
                                    onClick = { onContactClick(link) },
                                )
                            }
                        }
                        Text(
                            text = menu.address,
                            color = menuColors.content,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                            ),
                            modifier = Modifier.padding(bottom = VitranSpacing.lg),
                        )
                        Text(
                            text = visitLabel,
                            color = menuColors.content,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(role = Role.Button, onClick = onVisitOnlineStoreClick)
                                .padding(vertical = VitranSpacing.xs),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PanelPad, vertical = PanelPad)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StoreMenuCircleButton(
                        a11y = shareA11y,
                        onClick = onShareClick,
                    ) {
                        VitranIcon(
                            painter = painterResource(Res.drawable.ic_share),
                            contentDescription = null,
                            size = GlyphSize,
                            tint = menuColors.content,
                        )
                    }
                    StoreMenuCircleButton(
                        a11y = closeA11y,
                        onClick = onClose,
                    ) {
                        VitranIcon(
                            painter = painterResource(Res.drawable.ic_close),
                            contentDescription = null,
                            size = GlyphSize,
                            tint = menuColors.content,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreMenuIdentity(
    store: StoreMock,
    ratingA11y: String,
) {
    val colors = LocalStoreMenuColors.current
    val placeholder = remember { ColorPainter(Color.White.copy(alpha = 0.35f)) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(store.avatarUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            modifier = Modifier
                .size(IdentityAvatarSize)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.35f))
                .border(0.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = store.name,
                color = colors.content,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                ),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.semantics { contentDescription = ratingA11y },
            ) {
                Text(
                    text = store.ratingLabel,
                    color = colors.content,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                )
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_star_filled),
                    contentDescription = null,
                    size = 14.dp,
                    tint = colors.content,
                )
                Text(
                    text = "(${store.reviewCountLabel})",
                    color = colors.content,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                )
            }
        }
    }
}

@Composable
private fun StoreMenuReviewsBlock(
    title: String,
    openA11y: String,
    averageLabel: String,
    ratingsCountLabel: String,
    mediaUrls: List<String>,
    teasers: List<StoreMenuReviewTeaser>,
    helpfulLabel: String,
    onOpenReviews: () -> Unit,
) {
    val colors = LocalStoreMenuColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = colors.content,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = SectionTitleSize,
                lineHeight = 42.sp,
            ),
        )
        StoreMenuCircleButton(
            a11y = openA11y,
            onClick = onOpenReviews,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = null,
                size = GlyphSize,
                tint = colors.content,
                modifier = Modifier.graphicsLayer { scaleX = -1f },
            )
        }
    }

    Spacer(modifier = Modifier.height(VitranSpacing.md))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = averageLabel,
            color = colors.content,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
            ),
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_star_filled),
                        contentDescription = null,
                        size = 16.dp,
                        tint = VitranTheme.extraColors.star,
                    )
                }
            }
            Text(
                text = ratingsCountLabel,
                color = colors.content,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            )
        }
    }

    if (mediaUrls.isNotEmpty()) {
        Spacer(modifier = Modifier.height(VitranSpacing.lg))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            mediaUrls.forEach { url ->
                val placeholder = remember(url) { ColorPainter(Color.White.copy(alpha = 0.25f)) }
                AsyncImage(
                    model = resolveNetworkImageUrl(url),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = placeholder,
                    error = placeholder,
                    modifier = Modifier
                        .size(MediaThumbSize)
                        .clip(RoundedCornerShape(VitranRadius.large))
                        .background(Color.White.copy(alpha = 0.25f)),
                )
            }
        }
    }

    if (teasers.isNotEmpty()) {
        Spacer(modifier = Modifier.height(VitranSpacing.lg))
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            teasers.forEach { teaser ->
                StoreMenuTeaserCard(
                    teaser = teaser,
                    helpfulLabel = helpfulLabel,
                )
            }
        }
    }
}

@Composable
private fun StoreMenuTeaserCard(
    teaser: StoreMenuReviewTeaser,
    helpfulLabel: String,
) {
    val colors = LocalStoreMenuColors.current
    val placeholder = remember(teaser.productThumbUrl) {
        ColorPainter(Color.White.copy(alpha = 0.3f))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TeaserCardRadius))
            .border(0.5.dp, colors.content.copy(alpha = 0.08f), RoundedCornerShape(TeaserCardRadius))
            .background(colors.frost)
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(teaser.productThumbUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
                modifier = Modifier
                    .size(TeaserThumbSize)
                    .clip(RoundedCornerShape(VitranRadius.medium))
                    .background(Color.White.copy(alpha = 0.3f)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(teaser.rating.coerceIn(0, 5)) {
                        VitranIcon(
                            painter = painterResource(Res.drawable.ic_star_filled),
                            contentDescription = null,
                            size = 14.dp,
                            tint = VitranTheme.extraColors.star,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = teaser.title,
                    color = colors.content,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = teaser.productName,
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Text(
            text = teaser.body,
            color = colors.content,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(teaser.avatarColorArgb)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = teaser.authorInitial,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                    ),
                )
            }
            Spacer(modifier = Modifier.width(VitranSpacing.sm))
            Text(
                text = "${teaser.authorName} · ${teaser.dateLabel}",
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable(role = Role.Button, onClick = {}),
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_thumb_up),
                    contentDescription = null,
                    size = 16.dp,
                    tint = colors.muted,
                )
                Text(
                    text = helpfulLabel,
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                )
            }
        }
    }
}

@Composable
private fun StoreMenuFrostedCard(
    horizontalPad: Dp = VitranSpacing.lg,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalStoreMenuColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(VitranRadius.extraLarge))
            .background(colors.frost)
            .padding(horizontal = horizontalPad, vertical = VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        content = content,
    )
}

@Composable
private fun StoreMenuContactRow(
    link: StoreMenuContactLink,
    onClick: () -> Unit,
) {
    val colors = LocalStoreMenuColors.current
    // shop.app Contact links: flex-direction row-reverse → label then trailing icon.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = link.label,
            color = colors.content,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        VitranIcon(
            painter = painterResource(link.kind.icon()),
            contentDescription = null,
            size = ContactIconSize,
            tint = colors.content,
            modifier = Modifier.padding(start = ContactIconGap),
        )
    }
}

@Composable
private fun StoreMenuPolicyRow(
    policy: StoreMenuPolicy,
    onClick: () -> Unit,
) {
    val colors = LocalStoreMenuColors.current
    // shop.app Policies: label + trailing policy glyph (about / dollar / order), not chevron.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(PolicyRowHeight)
            .clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = policy.title,
            color = colors.content,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            modifier = Modifier.weight(1f),
        )
        VitranIcon(
            painter = painterResource(policy.kind.icon()),
            contentDescription = null,
            size = PolicyIconSize,
            tint = colors.content,
        )
    }
}

@Composable
private fun StoreMenuCircleButton(
    a11y: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val colors = LocalStoreMenuColors.current
    Box(
        modifier = Modifier
            .size(CircleButtonSize)
            .clip(CircleShape)
            .background(colors.chrome)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = a11y },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** shop.app panel `p-space-24`. */
private val PanelPad = VitranSpacing.xxl

/** shop.app scroll `pt-space-40`. */
private val ScrollTopPad = 40.dp

/** shop.app Policies/Contact `mt-space-24`. */
private val SectionGap = VitranSpacing.xxl

private val SectionTitleSize = 36.sp
private val CircleButtonSize = 44.dp
private val GlyphSize = 20.dp

private val IdentityAvatarSize = 48.dp
private val MediaThumbSize = 72.dp
private val TeaserCardRadius = 20.dp
private val TeaserThumbSize = 48.dp
private val PolicyRowHeight = 46.dp
private val ContactCardHPad = VitranSpacing.xxl

/** shop.app Contact links `my-space-24` / `gap-space-24`. */
private val ContactLinksBlockGap = VitranSpacing.xxl

/** shop.app contact icon 20px + `gap: 12px`. */
private val ContactIconSize = 20.dp
private val ContactIconGap = 12.dp

/** shop.app policy trailing glyphs (about / dollar / order). */
private val PolicyIconSize = 20.dp
