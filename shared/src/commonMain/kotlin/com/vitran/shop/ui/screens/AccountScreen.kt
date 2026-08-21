package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountHubCitiesRow
import com.vitran.shop.ui.sections.account.AccountHubHeader
import com.vitran.shop.ui.sections.account.AccountHubStorePlanRow
import com.vitran.shop.ui.sections.account.AccountHubUsersRow
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountRecentlyViewedSection
import com.vitran.shop.ui.sections.account.AccountReferralBanner
import com.vitran.shop.ui.sections.account.AccountSavedFollowingRow
import com.vitran.shop.ui.sections.account.AccountSellerSection
import com.vitran.shop.ui.sections.account.AccountSignOutRow
import com.vitran.shop.ui.sections.account.rememberMockAccountHubExtras
import com.vitran.shop.ui.sections.account.rememberMockAccountProfile
import com.vitran.shop.ui.sections.account.rememberMockReferralProfile
import com.vitran.shop.ui.shell.LocalDesktopLayout

/**
 * Account hub — route `/account`.
 * Identity row, referral promo, Saved/Following tiles, recently viewed, seller entry.
 * Visual rhythm matches shop.app account hub (`docs/ui-reference/account/`).
 */
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    onDestClick: (AccountDest) -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenSaved: () -> Unit = {},
    onOpenFollowing: () -> Unit = {},
    onOpenReferrals: () -> Unit = {},
    onCreateStore: () -> Unit = {},
    onOpenStorePlan: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onProductOpen: (
        id: String,
        title: String,
        imageUrl: String,
        storeName: String,
        priceLabel: String,
    ) -> Unit = { _, _, _, _, _ -> },
) {
    val profile = rememberMockAccountProfile()
    val referral = rememberMockReferralProfile()
    val extras = rememberMockAccountHubExtras()

    AccountPageShell(
        dest = AccountDest.Hub,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        modifier = modifier,
    ) {
        AccountHubHeader(
            profile = profile,
            onEditClick = onOpenProfile,
        )
        AccountReferralBanner(
            profile = referral,
            onInviteClick = onOpenReferrals,
            onHistoryClick = onOpenReferrals,
        )
        AccountSavedFollowingRow(
            extras = extras,
            onSavedClick = onOpenSaved,
            onFollowingClick = onOpenFollowing,
        )
        if (!LocalDesktopLayout.current) {
            AccountHubUsersRow(onClick = { onDestClick(AccountDest.Users) })
            AccountHubCitiesRow(onClick = { onDestClick(AccountDest.Cities) })
        }
        AccountRecentlyViewedSection(
            items = extras.recentlyViewed,
            onItemClick = { item ->
                onProductOpen(item.id, item.title, item.imageUrl, item.storeName, item.priceLabel)
            },
            onSaveClick = {},
        )
        when {
            profile.isMerchant && profile.hasStore -> {
                AccountHubStorePlanRow(onClick = onOpenStorePlan)
            }
            !profile.isMerchant -> {
                AccountSellerSection(onCreateStore = onCreateStore)
            }
        }
        AccountSignOutRow(onClick = onSignOut)
    }
}
