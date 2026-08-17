package com.vitran.shop.ui.sections.account.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.theme.ErrorRed
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.ShopPurpleTint
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_close

private val SellerChipBg = Color(0xFFE0F2FE)
private val SellerChipFg = Color(0xFF0369A1)
private val ManagerChipBg = Color(0xFFFFEDD5)
private val ManagerChipFg = Color(0xFFC2410C)
private val SupportChipBg = Color(0xFFCCFBF1)
private val SupportChipFg = Color(0xFF0F766E)
private val ActiveChipBg = Color(0xFFE8F8EF)
private val ActiveChipFg = Color(0xFF15803D)

@Composable
internal fun AccountUserRoleChip(
    role: AccountUserRole,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
) {
    val (bg, fg) = when (role) {
        AccountUserRole.Customer -> ShopPurpleTint to ShopPurple
        AccountUserRole.Seller -> SellerChipBg to SellerChipFg
        AccountUserRole.Manager -> ManagerChipBg to ManagerChipFg
        AccountUserRole.Support -> SupportChipBg to SupportChipFg
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(VitranRadius.small))
            .background(bg)
            .padding(
                start = VitranSpacing.sm,
                top = VitranSpacing.xs,
                end = if (onRemove != null) VitranSpacing.xs else VitranSpacing.sm,
                bottom = VitranSpacing.xs,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        VitranText(
            text = role.label(),
            style = VitranTextStyle.Label,
            color = fg,
            maxLines = 1,
        )
        if (onRemove != null) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = fg,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onRemove)
                    .padding(VitranSpacing.xs),
            )
        }
    }
}

@Composable
internal fun AccountUserStatusChip(
    status: AccountUserStatus,
    modifier: Modifier = Modifier,
) {
    val (bg, fg) = when (status) {
        AccountUserStatus.Active -> ActiveChipBg to ActiveChipFg
        AccountUserStatus.Inactive -> AccountTokens.DangerSoft to ErrorRed
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(VitranRadius.medium))
            .background(bg)
            .padding(horizontal = VitranSpacing.sm, vertical = VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Box(
            modifier = Modifier
                .size(AccountTokens.StatusDot)
                .clip(CircleShape)
                .background(fg),
        )
        VitranText(
            text = status.label(),
            style = VitranTextStyle.Label,
            color = fg,
            maxLines = 1,
        )
    }
}

@Composable
internal fun AccountUserAvatar(
    initial: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(AccountTokens.UserAvatar)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        VitranText(
            text = initial,
            style = VitranTextStyle.Title,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
        )
    }
}

@Composable
internal fun AccountUsersFilterChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(VitranRadius.medium))
            .background(ShopPurpleTint)
            .padding(
                start = VitranSpacing.md,
                top = VitranSpacing.xs,
                end = VitranSpacing.xs,
                bottom = VitranSpacing.xs,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        VitranText(
            text = label,
            style = VitranTextStyle.Label,
            color = ShopPurple,
            maxLines = 1,
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = ShopPurple,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onRemove)
                .padding(VitranSpacing.xs),
        )
    }
}
