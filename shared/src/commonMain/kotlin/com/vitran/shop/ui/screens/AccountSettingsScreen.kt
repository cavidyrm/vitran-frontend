package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountPrivacyPrefs
import com.vitran.shop.ui.sections.account.AccountSettingsSection
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.ProfileOthersSection
import com.vitran.shop.ui.sections.account.ProfilePreferencesSection
import com.vitran.shop.ui.sections.account.rememberMockAccountProfile
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_nav_settings

@Composable
fun AccountSettingsScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onOpenProfile: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val initial = rememberMockAccountProfile().copy(skinType = "نرمال")
    var profile by remember { mutableStateOf(initial) }
    var privacy by remember { mutableStateOf(AccountPrivacyPrefs()) }
    AccountPageShell(
        dest = AccountDest.Settings,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_nav_settings),
        contentMaxWidth = AccountTokens.SettingsContentMaxWidth,
        modifier = modifier,
    ) {
        AccountSettingsSection(
            privacy = privacy,
            onPrivacyChange = { privacy = it },
            onAccountInfoClick = onOpenProfile,
            onSignOut = onSignOut,
        )
        ProfilePreferencesSection(
            profile = profile,
            onProfileChange = { profile = it },
        )
        ProfileOthersSection(onAddClick = { /* mock */ })
    }
}
