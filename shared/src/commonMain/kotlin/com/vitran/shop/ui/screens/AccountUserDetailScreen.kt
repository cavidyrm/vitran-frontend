package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountPrimaryButton
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.users.AccountUser
import com.vitran.shop.ui.sections.account.users.AccountUserDetailHeader
import com.vitran.shop.ui.sections.account.users.AccountUserEditForm
import com.vitran.shop.ui.sections.account.users.AccountUserStatus
import com.vitran.shop.ui.sections.account.users.AccountUserSummaryRail
import com.vitran.shop.ui.sections.account.users.AccountUserSummaryStack
import com.vitran.shop.ui.sections.account.users.findMockAccountUser
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_user_detail_back_list
import vitranshop.shared.generated.resources.account_user_detail_not_found
import vitranshop.shared.generated.resources.account_user_detail_title

@Composable
fun AccountUserDetailScreen(
    userId: String,
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val initial = remember(userId) { findMockAccountUser(userId) }
    AccountPageShell(
        dest = AccountDest.Users,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_user_detail_title),
        contentMaxWidth = AccountTokens.UsersContentMaxWidth,
        onFooterLinkClick = onFooterLinkClick,
        modifier = modifier,
    ) {
        if (initial == null) {
            AccountUserDetailMissing(onBack = onBack)
        } else {
            AccountUserDetailContent(
                initial = initial,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun AccountUserDetailContent(
    initial: AccountUser,
    onBack: () -> Unit,
) {
    var saved by remember(initial.id) { mutableStateOf(initial) }
    var draft by remember(initial.id) { mutableStateOf(initial) }
    val isDesktop = LocalDesktopLayout.current
    AccountUserDetailHeader(
        isActive = draft.status == AccountUserStatus.Active,
        onUsersClick = onBack,
        onSave = { saved = draft },
        onReset = { draft = saved },
        onSendResetLink = { /* mock */ },
        onToggleActive = {
            draft = draft.copy(
                status = if (draft.status == AccountUserStatus.Active) {
                    AccountUserStatus.Inactive
                } else {
                    AccountUserStatus.Active
                },
            )
        },
    )
    if (isDesktop) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            AccountUserEditForm(
                user = draft,
                onUserChange = { draft = it },
                modifier = Modifier.weight(1f),
            )
            AccountUserSummaryRail(user = draft)
        }
    } else {
        AccountUserEditForm(
            user = draft,
            onUserChange = { draft = it },
        )
        AccountUserSummaryStack(user = draft)
    }
}

@Composable
private fun AccountUserDetailMissing(onBack: () -> Unit) {
    AccountCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.xxxl),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            VitranText(
                text = stringResource(Res.string.account_user_detail_not_found),
                style = VitranTextStyle.Title,
            )
            AccountPrimaryButton(
                label = stringResource(Res.string.account_user_detail_back_list),
                onClick = onBack,
            )
        }
    }
}
