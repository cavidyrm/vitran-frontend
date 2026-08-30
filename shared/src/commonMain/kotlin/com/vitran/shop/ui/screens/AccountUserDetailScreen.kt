package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.admin.users.presentation.AdminUserDetailViewModel
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
import com.vitran.shop.ui.sections.account.users.toAccountUser
import com.vitran.shop.ui.sections.account.users.toAccountUserRole
import com.vitran.shop.ui.sections.account.users.toUserRole
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.di.vitranKoinViewModel
import org.koin.core.parameter.parametersOf
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
    viewModel: AdminUserDetailViewModel = vitranKoinViewModel {
        parametersOf(userId.toLongOrNull() ?: 0L)
    },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
        val detail = state.detail
        if (state.isLoading) {
            VitranText("در حال دریافت کاربر…", VitranTextStyle.Body)
        } else if (detail == null) {
            AccountUserDetailMissing(onBack = onBack)
        } else {
            AccountUserDetailContent(
                initial = detail.toAccountUser().copy(
                    status = if (state.isActive) AccountUserStatus.Active else AccountUserStatus.Inactive,
                    roles = state.selectedEditableRoles.mapNotNull { it.toAccountUserRole() },
                ),
                availableRoles = state.assignableRoles.mapNotNull { it.toAccountUserRole() },
                onBack = onBack,
                onReload = viewModel::load,
                onSubmit = viewModel::submit,
                onActiveChange = viewModel::setActive,
                onRoleChange = { role, selected ->
                    role.toUserRole()?.let { viewModel.setRoleSelected(it, selected) }
                },
            )
        }
        state.loadError?.let { VitranText(it, VitranTextStyle.Body) }
        state.submitError?.let { VitranText(it, VitranTextStyle.Body) }
    }
}

@Composable
private fun AccountUserDetailContent(
    initial: AccountUser,
    availableRoles: List<com.vitran.shop.ui.sections.account.users.AccountUserRole>,
    onBack: () -> Unit,
    onReload: () -> Unit,
    onSubmit: () -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onRoleChange: (com.vitran.shop.ui.sections.account.users.AccountUserRole, Boolean) -> Unit,
) {
    val isDesktop = LocalDesktopLayout.current
    AccountUserDetailHeader(
        isActive = initial.status == AccountUserStatus.Active,
        onUsersClick = onBack,
        onSave = onSubmit,
        onReset = onReload,
        onSendResetLink = null,
        onToggleActive = { onActiveChange(initial.status != AccountUserStatus.Active) },
    )
    val onUserChange: (AccountUser) -> Unit = { changed ->
        if (changed.status != initial.status) {
            onActiveChange(changed.status == AccountUserStatus.Active)
        }
        availableRoles.forEach { role ->
            val wasSelected = role in initial.roles
            val selected = role in changed.roles
            if (wasSelected != selected) onRoleChange(role, selected)
        }
    }
    if (isDesktop) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            AccountUserEditForm(
                user = initial,
                onUserChange = onUserChange,
                adminMode = true,
                availableRoles = availableRoles,
                modifier = Modifier.weight(1f),
            )
            AccountUserSummaryRail(user = initial)
        }
    } else {
        AccountUserEditForm(
            user = initial,
            onUserChange = onUserChange,
            adminMode = true,
            availableRoles = availableRoles,
        )
        AccountUserSummaryStack(user = initial)
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
