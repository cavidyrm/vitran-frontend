package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_edit_photo
import vitranshop.shared.generated.resources.account_edit_photo_a11y
import vitranshop.shared.generated.resources.ic_camera
import vitranshop.shared.generated.resources.ic_edit
import vitranshop.shared.generated.resources.ic_nav_profile

@Composable
internal fun ProfileAvatarSection(
    @Suppress("UNUSED_PARAMETER") profile: AccountProfile,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // profile.avatarUrl reserved for a future image loader; soft placeholder for now.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = VitranSpacing.lg, bottom = VitranSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(AccountTokens.AvatarProfile)
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(Res.string.account_edit_photo_a11y),
                    onClick = onEditClick,
                ),
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_nav_profile),
                    contentDescription = null,
                    size = VitranSize.iconLarge,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(AccountTokens.EditBadge)
                    .shadow(elevation = VitranElevation.small, shape = CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, AccountTokens.CardBorder, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_camera),
                    contentDescription = stringResource(Res.string.account_edit_photo_a11y),
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Row(
            modifier = Modifier.clickable(role = Role.Button, onClick = onEditClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_edit),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.primary,
            )
            VitranText(
                text = stringResource(Res.string.account_edit_photo),
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
