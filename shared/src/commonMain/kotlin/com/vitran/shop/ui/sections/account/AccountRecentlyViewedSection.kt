package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_empty_recent
import vitranshop.shared.generated.resources.account_recently_viewed
import vitranshop.shared.generated.resources.account_recently_viewed_item_a11y
import vitranshop.shared.generated.resources.account_recently_viewed_next_a11y
import vitranshop.shared.generated.resources.account_recently_viewed_open_a11y
import vitranshop.shared.generated.resources.categories_product_card_save_a11y
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_nav_saved
import kotlin.math.max

/**
 * Recently viewed — shop.app hub shows image tiles only (no title/price under the photo).
 * Heart sits on the image corner.
 */
@Composable
internal fun AccountRecentlyViewedSection(
    items: List<RecentlyViewedItem>,
    onItemClick: (RecentlyViewedItem) -> Unit,
    onSaveClick: (RecentlyViewedItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            VitranText(
                text = stringResource(Res.string.account_recently_viewed),
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = stringResource(Res.string.account_recently_viewed_open_a11y),
                size = 12.dp,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            )
        }
        if (items.isEmpty()) {
            VitranText(
                text = stringResource(Res.string.account_empty_recent),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@Column
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val visible = if (isDesktop) 4 else 2
            val gap = AccountTokens.RecentlyViewedGap
            val edgePad = VitranSpacing.xs
            val cardWidth = max(
                AccountTokens.RecentlyViewedMin.value,
                (maxWidth.value - edgePad.value * 2 - gap.value * (visible - 1)) / visible,
            ).dp
            Box {
                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    contentPadding = PaddingValues(horizontal = edgePad),
                ) {
                    items(items, key = { it.id }) { item ->
                        RecentlyViewedImageTile(
                            item = item,
                            modifier = Modifier.width(cardWidth),
                            onClick = { onItemClick(item) },
                            onSaveClick = { onSaveClick(item) },
                        )
                    }
                }
                if (isDesktop && items.size > visible) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(AccountTokens.ScrollButton)
                            .shadow(
                                elevation = AccountTokens.CardElevation,
                                shape = CircleShape,
                                clip = false,
                                ambientColor = AccountTokens.CardShadow,
                                spotColor = AccountTokens.CardShadow,
                            )
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable(role = Role.Button) {
                                scope.launch {
                                    val next = (listState.firstVisibleItemIndex + 1)
                                        .coerceAtMost(items.lastIndex)
                                    listState.animateScrollToItem(next)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        VitranIcon(
                            painter = painterResource(Res.drawable.ic_chevron_right),
                            contentDescription = stringResource(Res.string.account_recently_viewed_next_a11y),
                            size = VitranSize.iconSmall,
                            modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentlyViewedImageTile(
    item: RecentlyViewedItem,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(VitranRadius.xl)
    val placeholder = remember { ColorPainter(AccountTokens.ImagePlaceholder) }
    val a11y = stringResource(
        Res.string.account_recently_viewed_item_a11y,
        item.title,
        item.storeName,
        item.priceLabel,
    )
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .semantics { contentDescription = a11y }
            .shadow(
                elevation = AccountTokens.CardElevation,
                shape = shape,
                clip = false,
                ambientColor = AccountTokens.CardShadow,
                spotColor = AccountTokens.CardShadow,
            )
            .clip(shape)
            .background(AccountTokens.ImagePlaceholder, shape)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        AsyncImage(
            model = resolveNetworkImageUrl(item.imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, AccountTokens.ImageBorder, shape),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(VitranSpacing.sm)
                .size(28.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onSaveClick),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_nav_saved),
                contentDescription = stringResource(Res.string.categories_product_card_save_a11y),
                size = 16.dp,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
