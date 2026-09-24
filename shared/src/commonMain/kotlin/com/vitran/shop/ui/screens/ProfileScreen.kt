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
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.account.presentation.profile.ProfileGender
import com.vitran.shop.feature.account.presentation.profile.ProfileUiAction
import com.vitran.shop.feature.account.presentation.profile.ProfileUiState
import com.vitran.shop.feature.account.presentation.profile.ProfileViewModel
import com.vitran.shop.feature.account.presentation.profile.UsernameCheckUiStatus
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountCityOption
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountGender
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountProfile
import com.vitran.shop.ui.sections.account.AccountSaveBar
import com.vitran.shop.ui.sections.account.ProfileAvatarSection
import com.vitran.shop.ui.sections.account.ProfilePersonalInfoCard
import com.vitran.shop.ui.sections.account.ProfileSizingCard
import com.vitran.shop.ui.sections.account.UsernameCheckDisplay
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.util.formatIsoDateAsJalali
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_city_clear
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
    val clearCityLabel = stringResource(Res.string.account_city_clear)

    LaunchedEffect(
        uiState.username,
        uiState.email,
        uiState.phone,
        uiState.firstName,
        uiState.lastName,
        uiState.avatarUrl,
        uiState.cityId,
        uiState.cityName,
        uiState.cities,
        uiState.citiesError,
        uiState.isCitiesLoading,
        uiState.gender,
        uiState.birthdayIso,
        uiState.upperBodySize,
        uiState.lowerBodySize,
        uiState.shoeSize,
        uiState.isLoading,
    ) {
        if (!uiState.isLoading) {
            profile = profileFromUiState(uiState, profile)
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
                    previewBytes = uiState.avatarPreviewBytes,
                    editEnabled = !uiState.isPickingAvatar && !uiState.isUploadingAvatar,
                    avatarUrlValue = uiState.avatarUrl,
                    onAvatarUrlChange = { url ->
                        profile = current.copy(avatarUrl = url.ifBlank { null })
                        viewModel.onAction(ProfileUiAction.AvatarUrlChanged(url))
                    },
                    onEditClick = { viewModel.onAction(ProfileUiAction.PickAvatar) },
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
                        if (updated.firstName != current.firstName) {
                            viewModel.onAction(ProfileUiAction.FirstNameChanged(updated.firstName))
                        }
                        if (updated.lastName != current.lastName) {
                            viewModel.onAction(ProfileUiAction.LastNameChanged(updated.lastName))
                        }
                        if (updated.gender != current.gender) {
                            viewModel.onAction(ProfileUiAction.GenderChanged(updated.gender.toProfileGender()))
                        }
                    },
                    cities = uiState.cities.map { AccountCityOption(id = it.id, name = it.name) },
                    clearCityLabel = clearCityLabel,
                    onCitySelect = { cityId ->
                        val name = cityId?.let { id ->
                            uiState.cities.firstOrNull { it.id == id }?.name
                        }
                        profile = current.copy(cityId = cityId, cityName = name)
                        viewModel.onAction(ProfileUiAction.CitySelected(cityId))
                    },
                    birthdayIso = uiState.birthdayIso,
                    onBirthdayChange = { iso ->
                        viewModel.onAction(ProfileUiAction.BirthdayChanged(iso))
                    },
                    citiesError = uiState.citiesError,
                    isCitiesLoading = uiState.isCitiesLoading,
                    onCitiesRetry = { viewModel.onAction(ProfileUiAction.Retry) },
                    usernameCheck = uiState.usernameCheck.toDisplay(),
                )
                ProfileSizingCard(
                    upperBodySlug = uiState.upperBodySize,
                    lowerBodySlug = uiState.lowerBodySize,
                    shoeSlug = uiState.shoeSize,
                    onUpperBodyChange = {
                        viewModel.onAction(ProfileUiAction.UpperBodySizeChanged(it))
                    },
                    onLowerBodyChange = {
                        viewModel.onAction(ProfileUiAction.LowerBodySizeChanged(it))
                    },
                    onShoeChange = {
                        viewModel.onAction(ProfileUiAction.ShoeSizeChanged(it))
                    },
                )
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                    )
                } else if (uiState.sizingError != null) {
                    Text(
                        text = uiState.sizingError!!,
                        modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                    )
                }
                AccountSaveBar(
                    onCancel = onBack,
                    onSave = { viewModel.onAction(ProfileUiAction.Save) },
                    saveEnabled = !uiState.isUpdating &&
                        !uiState.isUploadingAvatar &&
                        uiState.usernameCheck !is UsernameCheckUiStatus.Taken &&
                        uiState.usernameCheck !is UsernameCheckUiStatus.Checking,
                )
            }
        }
    }
}

private fun profileFromUiState(
    uiState: ProfileUiState,
    existing: AccountProfile?,
): AccountProfile {
    val cityName = uiState.cityId?.let { id ->
        uiState.cities.firstOrNull { it.id == id }?.name
            ?: uiState.cityName
            ?: existing?.cityName
    }
    val birthdayDisplay = uiState.birthdayIso
        ?.takeIf { it.isNotBlank() }
        ?.let { formatIsoDateAsJalali(it) }
        .orEmpty()
    val gender = uiState.gender.toAccountGender()
    return existing?.copy(
        username = uiState.username,
        email = uiState.email,
        phone = uiState.phone,
        firstName = uiState.firstName,
        lastName = uiState.lastName,
        avatarUrl = uiState.avatarUrl.ifBlank { null },
        cityId = uiState.cityId,
        cityName = cityName,
        gender = gender,
        birthday = birthdayDisplay,
        shoeSize = uiState.shoeSize,
        topSize = uiState.upperBodySize,
        bottomSize = uiState.lowerBodySize,
    ) ?: AccountProfile(
        id = "",
        username = uiState.username,
        firstName = uiState.firstName,
        lastName = uiState.lastName,
        email = uiState.email,
        emailVerified = false,
        phone = uiState.phone,
        roles = emptyList(),
        hasStore = false,
        gender = gender,
        birthday = birthdayDisplay,
        shoeSize = uiState.shoeSize,
        topSize = uiState.upperBodySize,
        bottomSize = uiState.lowerBodySize,
        skinType = null,
        skinUndertone = null,
        skinTone = null,
        hairType = null,
        hairColor = null,
        avatarUrl = uiState.avatarUrl.ifBlank { null },
        cityId = uiState.cityId,
        cityName = cityName,
    )
}

private fun ProfileGender.toAccountGender(): AccountGender = when (this) {
    ProfileGender.Unspecified -> AccountGender.Unspecified
    ProfileGender.Female -> AccountGender.Female
    ProfileGender.Male -> AccountGender.Male
    ProfileGender.Other -> AccountGender.Other
}

private fun AccountGender.toProfileGender(): ProfileGender = when (this) {
    AccountGender.Unspecified -> ProfileGender.Unspecified
    AccountGender.Female -> ProfileGender.Female
    AccountGender.Male -> ProfileGender.Male
    AccountGender.Other -> ProfileGender.Other
}

private fun UsernameCheckUiStatus.toDisplay(): UsernameCheckDisplay = when (this) {
    UsernameCheckUiStatus.Idle -> UsernameCheckDisplay.Idle
    UsernameCheckUiStatus.Checking -> UsernameCheckDisplay.Checking
    is UsernameCheckUiStatus.Available -> UsernameCheckDisplay.Available
    is UsernameCheckUiStatus.Taken -> UsernameCheckDisplay.Taken
    is UsernameCheckUiStatus.Error -> UsernameCheckDisplay.Error
}
