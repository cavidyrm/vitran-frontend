package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.users.AccountUsersFilterState
import com.vitran.shop.ui.sections.account.users.AccountUsersFilters
import com.vitran.shop.ui.sections.account.users.AccountUsersHeader
import com.vitran.shop.ui.sections.account.users.AccountUsersPageSizeOptions
import com.vitran.shop.ui.sections.account.users.AccountUsersPagination
import com.vitran.shop.ui.sections.account.users.AccountUsersSort
import com.vitran.shop.ui.sections.account.users.AccountUsersTable
import com.vitran.shop.ui.sections.account.users.filterAccountUsers
import com.vitran.shop.ui.sections.account.users.rememberMockAccountUsers
import com.vitran.shop.ui.shell.LocalDesktopLayout
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_nav_users
import kotlin.math.max

@Composable
fun AccountUsersScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onUserOpen: (Int) -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val users = rememberMockAccountUsers()
    var filters by remember { mutableStateOf(AccountUsersFilterState()) }
    var sort by remember { mutableStateOf(AccountUsersSort.JoinedDesc) }
    var page by remember { mutableIntStateOf(1) }
    var pageSize by remember { mutableIntStateOf(AccountUsersPageSizeOptions[1]) }
    val isDesktop = LocalDesktopLayout.current
    var filtersExpanded by remember { mutableStateOf(isDesktop) }
    val filtered = remember(users, filters, sort) {
        filterAccountUsers(users, filters, sort)
    }
    LaunchedEffect(filters, pageSize) {
        page = 1
    }
    val pageCount = max(1, (filtered.size + pageSize - 1) / pageSize)
    val safePage = page.coerceIn(1, pageCount)
    val pageUsers = filtered.drop((safePage - 1) * pageSize).take(pageSize)

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
        AccountUsersHeader(onAddClick = { /* mock */ })
        AccountUsersFilters(
            filters = filters,
            onFiltersChange = { filters = it },
            expanded = filtersExpanded,
            onExpandedChange = { filtersExpanded = it },
        )
        AccountUsersTable(
            users = pageUsers,
            sort = sort,
            onSortChange = { sort = it },
            onUserClick = { user -> onUserOpen(user.id) },
        )
        AccountUsersPagination(
            totalCount = filtered.size,
            page = safePage,
            pageSize = pageSize,
            onPageChange = { page = it },
            onPageSizeChange = { pageSize = it },
        )
    }
}
