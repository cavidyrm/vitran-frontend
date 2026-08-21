package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountSaveBar
import com.vitran.shop.ui.sections.account.ProfileAvatarSection
import com.vitran.shop.ui.sections.account.ProfilePersonalInfoCard
import com.vitran.shop.ui.sections.account.ProfileSizingCard
import com.vitran.shop.ui.sections.account.rememberMockAccountProfile
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_nav_profile

/**
 * Profile editor — route `/account/profile`.
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val initial = rememberMockAccountProfile()
    var profile by remember { mutableStateOf(initial) }

    AccountPageShell(
        dest = AccountDest.Hub,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_nav_profile),
        showSearch = false,
        onFooterLinkClick = onFooterLinkClick,
        modifier = modifier,
    ) {
        ProfileAvatarSection(
            profile = profile,
            onEditClick = { /* mock — photo picker not wired */ },
        )
        ProfilePersonalInfoCard(
            profile = profile,
            onProfileChange = { profile = it },
        )
        ProfileSizingCard(
            profile = profile,
            onProfileChange = { profile = it },
        )
        AccountSaveBar(
            onCancel = onBack,
            onSave = onBack,
        )
    }
}
