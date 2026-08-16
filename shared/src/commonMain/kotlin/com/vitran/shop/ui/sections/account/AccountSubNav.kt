package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_nav_following
import vitranshop.shared.generated.resources.account_nav_hub
import vitranshop.shared.generated.resources.account_nav_referrals
import vitranshop.shared.generated.resources.account_nav_saved
import vitranshop.shared.generated.resources.account_nav_settings

@Composable
internal fun AccountSubNav(
    dest: AccountDest,
    onDestClick: (AccountDest) -> Unit,
    onSavedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(AccountTokens.SubNavWidth)
            .padding(top = VitranSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        AccountSubNavItem(
            label = stringResource(Res.string.account_nav_hub),
            selected = dest == AccountDest.Hub,
            onClick = { onDestClick(AccountDest.Hub) },
        )
        AccountSubNavItem(
            label = stringResource(Res.string.account_nav_saved),
            selected = false,
            onClick = onSavedClick,
        )
        AccountSubNavItem(
            label = stringResource(Res.string.account_nav_following),
            selected = dest == AccountDest.Following,
            onClick = { onDestClick(AccountDest.Following) },
        )
        AccountSubNavItem(
            label = stringResource(Res.string.account_nav_referrals),
            selected = dest == AccountDest.Referrals,
            onClick = { onDestClick(AccountDest.Referrals) },
        )
        AccountSubNavItem(
            label = stringResource(Res.string.account_nav_settings),
            selected = dest == AccountDest.Settings,
            onClick = { onDestClick(AccountDest.Settings) },
        )
    }
}

@Composable
private fun AccountSubNavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = if (selected) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = label,
        color = color,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 16.sp,
        ),
        modifier = Modifier
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = VitranSpacing.sm, horizontal = VitranSpacing.xs),
    )
}
