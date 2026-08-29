package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.account.presentation.profile.ProfileUiAction
import com.vitran.shop.feature.account.presentation.profile.ProfileViewModel
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountProfile
import com.vitran.shop.ui.sections.account.AccountSaveBar
import com.vitran.shop.ui.sections.account.ProfileAvatarSection
import com.vitran.shop.ui.sections.account.ProfilePersonalInfoCard
import com.vitran.shop.ui.sections.account.ProfileSizingCard
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import com.vitran.shop.di.vitranKoinViewModel
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
    viewModel: ProfileViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var profile by remember { mutableStateOf<AccountProfile?>(null) }

    LaunchedEffect(uiState.username, uiState.email, uiState.phone, uiState.isLoading) {
        if (!uiState.isLoading) {
            profile = profileFromUiState(uiState.username, uiState.email, uiState.phone, profile)
        }
    }

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
        when {
            uiState.isLoading && profile == null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(VitranSpacing.xl),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null && profile == null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(VitranSpacing.xl),
                    contentAlignment = Alignment.Center,
                ) {
                    TextButton(onClick = { viewModel.onAction(ProfileUiAction.Retry) }) {
                        Text(uiState.error ?: "بارگذاری ناموفق")
                    }
                }
            }
            profile != null -> {
                val current = profile!!
                ProfileAvatarSection(
                    profile = current,
                    onEditClick = { /* photo picker not wired */ },
                )
                ProfilePersonalInfoCard(
                    profile = current,
                    onProfileChange = { updated ->
                        profile = updated
                        if (updated.username != current.username) {
                            viewModel.onAction(ProfileUiAction.UsernameChanged(updated.username))
                        }
                        if (updated.email != current.email) {
                            viewModel.onAction(ProfileUiAction.EmailChanged(updated.email))
                        }
                    },
                )
                ProfileSizingCard(
                    profile = current,
                    onProfileChange = { profile = it },
                )
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                    )
                }
                AccountSaveBar(
                    onCancel = onBack,
                    onSave = { viewModel.onAction(ProfileUiAction.Save) },
                )
            }
        }
    }
}

private fun profileFromUiState(
    username: String,
    email: String,
    phone: String,
    existing: AccountProfile?,
): AccountProfile =
    existing?.copy(username = username, email = email, phone = phone)
        ?: AccountProfile(
            id = "",
            username = username,
            firstName = "",
            lastName = "",
            email = email,
            emailVerified = false,
            phone = phone,
            roles = emptyList(),
            hasStore = false,
            gender = com.vitran.shop.ui.sections.account.AccountGender.Unspecified,
            birthday = "",
            shoeSize = null,
            topSize = null,
            bottomSize = null,
            skinType = null,
            skinUndertone = null,
            skinTone = null,
            hairType = null,
            hairColor = null,
            avatarUrl = null,
        )
