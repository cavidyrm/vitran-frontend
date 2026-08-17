package com.vitran.shop.ui.sections.account.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountOutlinedButton
import com.vitran.shop.ui.sections.account.AccountPrimaryButton
import com.vitran.shop.ui.sections.account.AccountTextLink
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.ErrorRed
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_save_changes
import vitranshop.shared.generated.resources.account_user_detail_activate
import vitranshop.shared.generated.resources.account_user_detail_deactivate
import vitranshop.shared.generated.resources.account_user_detail_reset
import vitranshop.shared.generated.resources.account_user_detail_send_reset
import vitranshop.shared.generated.resources.account_user_detail_subtitle
import vitranshop.shared.generated.resources.account_user_detail_title
import vitranshop.shared.generated.resources.account_users_title
import vitranshop.shared.generated.resources.ic_delete
import vitranshop.shared.generated.resources.ic_save
import vitranshop.shared.generated.resources.ic_social_email
import vitranshop.shared.generated.resources.ic_unfold

@Composable
internal fun AccountUserDetailHeader(
    isActive: Boolean,
    onUsersClick: () -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onSendResetLink: () -> Unit,
    onToggleActive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    if (isDesktop) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            AccountUserDetailTitleBlock(
                onUsersClick = onUsersClick,
                modifier = Modifier.weight(1f),
            )
            AccountUserDetailActions(
                isActive = isActive,
                onSave = onSave,
                onReset = onReset,
                onSendResetLink = onSendResetLink,
                onToggleActive = onToggleActive,
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AccountUserDetailTitleBlock(onUsersClick = onUsersClick)
            AccountUserDetailActions(
                isActive = isActive,
                onSave = onSave,
                onReset = onReset,
                onSendResetLink = onSendResetLink,
                onToggleActive = onToggleActive,
            )
        }
    }
}

@Composable
private fun AccountUserDetailTitleBlock(
    onUsersClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            AccountTextLink(
                label = stringResource(Res.string.account_users_title),
                onClick = onUsersClick,
            )
            VitranText(
                text = "/",
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            VitranText(
                text = stringResource(Res.string.account_user_detail_title),
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        VitranText(
            text = stringResource(Res.string.account_user_detail_title),
            style = VitranTextStyle.Headline,
            color = MaterialTheme.colorScheme.onSurface,
        )
        VitranText(
            text = stringResource(Res.string.account_user_detail_subtitle),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AccountUserDetailActions(
    isActive: Boolean,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onSendResetLink: () -> Unit,
    onToggleActive: () -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        AccountPrimaryButton(
            label = stringResource(Res.string.account_save_changes),
            onClick = onSave,
            icon = painterResource(Res.drawable.ic_save),
        )
        AccountOutlinedButton(
            label = stringResource(Res.string.account_user_detail_reset),
            onClick = onReset,
            icon = painterResource(Res.drawable.ic_unfold),
            borderColor = ShopPurple,
            contentColor = ShopPurple,
        )
        AccountOutlinedButton(
            label = stringResource(Res.string.account_user_detail_send_reset),
            onClick = onSendResetLink,
            icon = painterResource(Res.drawable.ic_social_email),
            borderColor = ShopPurple,
            contentColor = ShopPurple,
        )
        AccountOutlinedButton(
            label = stringResource(
                if (isActive) {
                    Res.string.account_user_detail_deactivate
                } else {
                    Res.string.account_user_detail_activate
                },
            ),
            onClick = onToggleActive,
            icon = painterResource(Res.drawable.ic_delete),
            borderColor = ErrorRed,
            contentColor = ErrorRed,
        )
    }
}
