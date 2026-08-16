package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_empty_following
import vitranshop.shared.generated.resources.account_nav_following
import vitranshop.shared.generated.resources.ic_chevron_right

@Composable
internal fun AccountFollowingList(
    stores: List<FollowedStore>,
    onStoreClick: (FollowedStore) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        VitranText(
            text = stringResource(Res.string.account_nav_following),
            style = VitranTextStyle.Title,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (stores.isEmpty()) {
            VitranText(
                text = stringResource(Res.string.account_empty_following),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            AccountCard {
                stores.forEachIndexed { index, store ->
                    FollowingRow(
                        store = store,
                        onClick = { onStoreClick(store) },
                        isRtl = isRtl,
                        showDivider = index != stores.lastIndex,
                    )
                }
            }
        }
    }
}

@Composable
private fun FollowingRow(
    store: FollowedStore,
    onClick: () -> Unit,
    isRtl: Boolean,
    showDivider: Boolean,
) {
    val placeholder = remember(store.id) { ColorPainter(AccountTokens.ImagePlaceholder) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(store.logoUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
                modifier = Modifier
                    .size(AccountTokens.ShortcutThumb)
                    .clip(CircleShape)
                    .background(AccountTokens.ImagePlaceholder, CircleShape),
            )
            VitranText(
                text = store.name,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            )
        }
        if (showDivider) {
            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                thickness = VitranSize.borderHairline,
                color = AccountTokens.FieldDivider,
            )
        }
    }
}
