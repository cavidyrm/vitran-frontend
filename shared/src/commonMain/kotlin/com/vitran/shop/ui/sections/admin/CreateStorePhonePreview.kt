package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_preview_badge
import vitranshop.shared.generated.resources.admin_preview_follow
import vitranshop.shared.generated.resources.admin_preview_following
import vitranshop.shared.generated.resources.admin_preview_font
import vitranshop.shared.generated.resources.admin_preview_open
import vitranshop.shared.generated.resources.admin_preview_product_count
import vitranshop.shared.generated.resources.admin_preview_products
import vitranshop.shared.generated.resources.admin_preview_rating
import vitranshop.shared.generated.resources.admin_preview_store_fallback
import vitranshop.shared.generated.resources.admin_preview_visit
import vitranshop.shared.generated.resources.ic_star_filled

@Composable
fun CreateStorePhonePreview(
    state: CreateStoreFormState,
    onVisit: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val theme = state.theme
    val name = state.storeName.ifBlank {
        stringResource(Res.string.admin_preview_store_fallback)
    }
    val products = remember(state.typeId) { CreateStoreMocks.previewProductsFor(state.typeId) }
    var following by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<String?>(null) }
    val nameWeight = if (theme.id == "luxury") FontWeight.Medium else FontWeight.SemiBold
    val nameSize = if (theme.id == "fashion") 15.sp else 16.sp
    val frame = RoundedCornerShape(36.dp)
    val screen = RoundedCornerShape(28.dp)
    Column(
        modifier = modifier.width(AdminTokens.PhonePreviewWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(AdminTokens.PhonePreviewWidth)
                .height(540.dp)
                .clip(frame)
                .background(AdminTokens.PreviewBezel)
                .padding(10.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(screen)
                    .background(theme.pageBackground),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(118.dp)
                        .background(theme.primary),
                ) {
                    val cover = state.coverUrl
                    if (cover != null) {
                        AsyncImage(
                            model = resolveNetworkImageUrl(cover),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = state.coverZoom
                                    scaleY = state.coverZoom
                                    translationX = state.coverOffsetX * 36f
                                    translationY = state.coverOffsetY * 20f
                                },
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                            .size(width = 72.dp, height = 5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)),
                    )
                    Text(
                        text = stringResource(Res.string.admin_preview_badge),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = VitranSpacing.md, bottom = VitranSpacing.sm)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(theme.primary.copy(alpha = 0.88f))
                            .padding(horizontal = VitranSpacing.sm, vertical = 3.dp),
                        color = theme.onPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .offset(y = (-22).dp)
                        .size(52.dp)
                        .shadow(VitranElevation.small, CircleShape, clip = false)
                        .clip(CircleShape)
                        .background(theme.secondary)
                        .border(3.dp, theme.pageBackground, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    val icon = state.iconUrl
                    if (icon != null) {
                        AsyncImage(
                            model = resolveNetworkImageUrl(icon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Text(
                            text = name.take(1),
                            color = theme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-14).dp)
                        .padding(horizontal = VitranSpacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = nameWeight,
                        fontSize = nameSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (state.slogan.isNotBlank()) {
                        Text(
                            text = state.slogan,
                            color = AdminTokens.Helper,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                    ) {
                        val category = state.categoryLabel
                        val emoji = state.categoryEmoji
                        if (category != null) {
                            Text(
                                text = listOfNotNull(emoji, category).joinToString(" "),
                                color = theme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        Text(
                            text = stringResource(Res.string.admin_preview_font, theme.fontLabel),
                            color = AdminTokens.Helper,
                            fontSize = 10.sp,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            VitranIcon(
                                painter = painterResource(Res.drawable.ic_star_filled),
                                contentDescription = null,
                                size = VitranSize.iconSmall,
                                tint = theme.primary,
                            )
                            Text(
                                text = stringResource(Res.string.admin_preview_rating),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(AdminTokens.Success),
                            )
                            Text(
                                text = stringResource(Res.string.admin_preview_open),
                                color = AdminTokens.Success,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        Text(
                            text = stringResource(
                                Res.string.admin_preview_product_count,
                                products.size.toString(),
                            ),
                            color = AdminTokens.Helper,
                            fontSize = 11.sp,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(if (following) theme.secondary else theme.primary)
                            .clickable(role = Role.Button) { following = !following }
                            .padding(horizontal = VitranSpacing.md, vertical = 5.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(
                                if (following) {
                                    Res.string.admin_preview_following
                                } else {
                                    Res.string.admin_preview_follow
                                },
                            ),
                            color = if (following) theme.primary else theme.onPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                        )
                    }
                }
                Text(
                    text = stringResource(Res.string.admin_preview_products),
                    modifier = Modifier.padding(horizontal = VitranSpacing.md),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    products.take(2).forEach { tile ->
                        val selected = selectedProduct == tile.id
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = VitranElevation.small,
                                    shape = RoundedCornerShape(12.dp),
                                    clip = false,
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .background(theme.cardBackground)
                                .then(
                                    if (selected) {
                                        Modifier.border(1.5.dp, theme.primary, RoundedCornerShape(12.dp))
                                    } else {
                                        Modifier
                                    },
                                )
                                .clickable(role = Role.Button) {
                                    selectedProduct = if (selected) null else tile.id
                                },
                        ) {
                            AsyncImage(
                                model = resolveNetworkImageUrl(tile.imageUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .background(theme.secondary.copy(alpha = 0.45f)),
                                contentScale = ContentScale.Crop,
                            )
                            Text(
                                text = tile.title,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = tile.priceLabel,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 0.dp),
                                color = theme.primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.md)
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(VitranRadius.medium))
                        .background(theme.primary)
                        .clickable(
                            enabled = state.slug.isNotBlank(),
                            role = Role.Button,
                            onClick = onVisit,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.admin_preview_visit),
                        color = theme.onPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    )
                }
            }
        }
    }
}
