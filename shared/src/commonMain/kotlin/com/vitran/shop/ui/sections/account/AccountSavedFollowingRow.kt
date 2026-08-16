package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_following
import vitranshop.shared.generated.resources.account_saved

/**
 * Side-by-side Saved / Following shortcut tiles — shop.app account hub.
 */
@Composable
internal fun AccountSavedFollowingRow(
    extras: AccountHubExtras,
    onSavedClick: () -> Unit,
    onFollowingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        ShortcutTile(
            title = stringResource(Res.string.account_saved),
            imageUrl = extras.savedThumbUrl,
            circularThumb = false,
            onClick = onSavedClick,
            modifier = Modifier.weight(1f),
        )
        ShortcutTile(
            title = stringResource(Res.string.account_following),
            imageUrl = extras.followingThumbUrl,
            circularThumb = true,
            onClick = onFollowingClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ShortcutTile(
    title: String,
    imageUrl: String,
    circularThumb: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholder = remember(imageUrl) { ColorPainter(AccountTokens.ImagePlaceholder) }
    val thumbShape = if (circularThumb) CircleShape else RoundedCornerShape(VitranRadius.small)
    AccountCard(
        modifier = modifier
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AccountTokens.ShortcutCardMinHeight)
                .padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            horizontalAlignment = Alignment.Start,
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
                modifier = Modifier
                    .size(AccountTokens.ShortcutCardThumb)
                    .clip(thumbShape)
                    .background(AccountTokens.ImagePlaceholder, thumbShape),
            )
            VitranText(
                text = title,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
        }
    }
}
