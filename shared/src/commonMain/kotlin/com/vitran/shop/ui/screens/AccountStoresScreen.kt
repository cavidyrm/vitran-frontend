package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.seller.shop.presentation.SellerShopsViewModel
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.stores.AccountStoresEmpty
import com.vitran.shop.ui.sections.account.stores.AccountStoresError
import com.vitran.shop.ui.sections.account.stores.AccountStoresList
import com.vitran.shop.ui.sections.account.stores.AccountStoresLoading
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_seller_stores

/**
 * Owned shops — route `/account/stores`.
 * Loads `GET /seller/shops?per_page=20` through [SellerShopsViewModel].
 */
@Composable
fun AccountStoresScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onCreateStore: () -> Unit,
    onShopEdit: (String) -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SellerShopsViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val list = uiState.list
    val title = stringResource(Res.string.account_seller_stores)
    var skipInitialResume by remember { mutableStateOf(true) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (skipInitialResume) {
            skipInitialResume = false
        } else {
            viewModel.refresh()
        }
    }

    AccountPageShell(
        dest = AccountDest.Stores,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = title,
        onFooterLinkClick = onFooterLinkClick,
        onNearEnd = {
            if (list.hasMore && !list.isLoadingMore && !list.isLoadingInitial) {
                viewModel.loadNextPage()
            }
        },
        nearEndToken = list.items.size to list.hasMore to list.isLoadingMore,
        modifier = modifier,
    ) {
        when {
            list.items.isEmpty() && list.initialError != null ->
                AccountStoresError(
                    message = list.initialError?.message,
                    onRetry = viewModel::refresh,
                )
            list.items.isEmpty() && (list.isLoadingInitial || list.isRefreshing) ->
                AccountStoresLoading()
            list.items.isEmpty() ->
                AccountStoresEmpty(onCreateStore = onCreateStore)
            else ->
                AccountStoresList(
                    shops = list.items,
                    isLoadingMore = list.isLoadingMore,
                    paginationError = list.paginationError?.message,
                    onShopClick = { shop -> onShopEdit(shop.id.value.toString()) },
                    onRetryPage = viewModel::loadNextPage,
                )
        }
    }
}
