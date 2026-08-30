package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.admin.users.presentation.AdminUsersUiState
import com.vitran.shop.feature.admin.users.presentation.AdminUsersViewModel
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.users.AccountUserRole
import com.vitran.shop.ui.sections.account.users.AccountUserStatus
import com.vitran.shop.ui.sections.account.users.AccountUsersFilterState
import com.vitran.shop.ui.sections.account.users.AccountUsersFilters
import com.vitran.shop.ui.sections.account.users.AccountUsersHeader
import com.vitran.shop.ui.sections.account.users.AccountUsersPagination
import com.vitran.shop.ui.sections.account.users.AccountUsersSort
import com.vitran.shop.ui.sections.account.users.AccountUsersTable
import com.vitran.shop.ui.sections.account.users.toAccountUser
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.di.vitranKoinViewModel
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_nav_users

@Composable
fun AccountUsersScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onUserOpen: (Int) -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AdminUsersViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var filters by remember { mutableStateOf(AccountUsersFilterState()) }
    var sort by remember { mutableStateOf(AccountUsersSort.JoinedDesc) }
    val isDesktop = LocalDesktopLayout.current
    var filtersExpanded by remember { mutableStateOf(isDesktop) }

    LaunchedEffect(filters.search, filters.phone) {
        viewModel.setPhoneFilter(filters.phone.ifBlank { filters.search })
    }
    LaunchedEffect(filters.role) {
        viewModel.setRoleFilter(filters.role.toBackendRole())
    }
    LaunchedEffect(filters.status) {
        viewModel.setActiveFilter(
            when (filters.status) {
                AccountUserStatus.Active -> true
                AccountUserStatus.Inactive -> false
                null -> null
            },
        )
    }

    AccountPageShell(
        dest = AccountDest.Users,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_nav_users),
        contentMaxWidth = AccountTokens.UsersContentMaxWidth,
        onFooterLinkClick = onFooterLinkClick,
        modifier = modifier,
    ) {
        AccountUsersHeader(onAddClick = {}, showAdd = false)
        AccountUsersFilters(
            filters = filters,
            onFiltersChange = { filters = it },
            expanded = filtersExpanded,
            onExpandedChange = { filtersExpanded = it },
        )
        when (val state = uiState) {
            is AdminUsersUiState.Content -> {
                val users = state.items.map { it.toAccountUser() }.let { items ->
                    when (sort) {
                        AccountUsersSort.JoinedDesc -> items.sortedByDescending { it.joinedJalali }
                        AccountUsersSort.JoinedAsc -> items.sortedBy { it.joinedJalali }
                    }
                }
                AccountUsersTable(
                    users = users,
                    sort = sort,
                    onSortChange = { sort = it },
                    onUserClick = { user -> onUserOpen(user.id) },
                )
                AccountUsersPagination(
                    totalCount = state.total.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                    page = state.page,
                    pageSize = state.perPage,
                    onPageChange = viewModel::setPage,
                    onPageSizeChange = viewModel::setPerPage,
                )
            }
            is AdminUsersUiState.Error ->
                VitranText(state.message ?: "خطا در دریافت کاربران", VitranTextStyle.Body)
            is AdminUsersUiState.Empty ->
                VitranText("کاربری یافت نشد", VitranTextStyle.Body)
            is AdminUsersUiState.Loading ->
                VitranText("در حال دریافت کاربران…", VitranTextStyle.Body)
        }
    }
}

private fun AccountUserRole?.toBackendRole(): String? =
    when (this) {
        AccountUserRole.Customer -> "customer"
        AccountUserRole.Seller -> "seller"
        AccountUserRole.Manager -> "admin"
        AccountUserRole.Support -> null
        null -> null
    }
