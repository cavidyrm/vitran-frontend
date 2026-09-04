package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountHubAdminPlansRow
import com.vitran.shop.ui.sections.account.AccountHubAdminRow
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
import com.vitran.shop.ui.sections.account.accountProfileLoadingPlaceholder
import com.vitran.shop.ui.sections.account.rememberMockAccountHubExtras
import com.vitran.shop.ui.sections.account.rememberMockReferralProfile
import com.vitran.shop.ui.sections.account.toAccountProfile
import com.vitran.shop.ui.shell.LocalDesktopLayout
import org.koin.compose.koinInject

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
    onOpenAdminPlans: () -> Unit = {},
    onOpenAdminShops: () -> Unit = {},
    onOpenAdminProducts: () -> Unit = {},
    onOpenAdminComments: () -> Unit = {},
    onOpenAdminTaxonomy: () -> Unit = {},
    onOpenAdminContent: () -> Unit = {},
    onSignOut: () -> Unit = {},
    isSigningOut: Boolean = false,
    signOutError: String? = null,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    onProductOpen: (
        id: String,
        title: String,
        imageUrl: String,
        storeName: String,
        priceLabel: String,
    ) -> Unit = { _, _, _, _, _ -> },
    accountRepository: AccountRepository = koinInject(),
    adminPermissions: AdminPermissions = koinInject(),
) {
    val currentUser by accountRepository.currentUserState.collectAsStateWithLifecycle()
    val profile = when (val state = currentUser) {
        is CurrentUserState.Available -> state.user.toAccountProfile()
        else -> accountProfileLoadingPlaceholder()
    }
    val referral = rememberMockReferralProfile()
    val extras = rememberMockAccountHubExtras()
    val roles = (currentUser as? CurrentUserState.Available)?.user?.roles.orEmpty()
    val canAccessAdmin = adminPermissions.canAccessAdmin(roles)

    AccountPageShell(
        dest = AccountDest.Hub,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onFooterLinkClick = onFooterLinkClick,
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
        if (canAccessAdmin && !LocalDesktopLayout.current) {
            AccountHubUsersRow(onClick = { onDestClick(AccountDest.Users) })
            AccountHubCitiesRow(onClick = { onDestClick(AccountDest.Cities) })
        }
        if (canAccessAdmin) {
            AccountHubAdminPlansRow(onClick = onOpenAdminPlans)
            AccountHubAdminRow(
                title = "بررسی فروشگاه‌ها",
                subtitle = "تأیید فروشگاه‌های در انتظار انتشار",
                onClick = onOpenAdminShops,
            )
            AccountHubAdminRow(
                title = "بررسی محصولات",
                subtitle = "تأیید محصولات و دیدگاه‌ها",
                onClick = onOpenAdminProducts,
            )
            AccountHubAdminRow(
                title = "تأیید دیدگاه",
                subtitle = "تأیید دیدگاه با شناسه",
                onClick = onOpenAdminComments,
            )
            AccountHubAdminRow(
                title = "طبقه‌بندی",
                subtitle = "ورود و ویرایش دسته‌بندی و ویژگی‌ها",
                onClick = onOpenAdminTaxonomy,
            )
            AccountHubAdminRow(
                title = "مدیریت محتوا",
                subtitle = "ویرایش صفحه‌های ثابت سایت",
                onClick = onOpenAdminContent,
            )
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
        AccountSignOutRow(
            onClick = onSignOut,
            isSigningOut = isSigningOut,
            errorMessage = signOutError,
        )
    }
}
