package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountFollowingList
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.rememberMockAccountHubExtras
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_nav_following

@Composable
fun FollowingScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onStoreOpen: (shopId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val extras = rememberMockAccountHubExtras()
    AccountPageShell(
        dest = AccountDest.Following,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_nav_following),
        modifier = modifier,
    ) {
        AccountFollowingList(
            stores = extras.followedStores,
            onStoreClick = { onStoreOpen(it.id) },
        )
    }
}
