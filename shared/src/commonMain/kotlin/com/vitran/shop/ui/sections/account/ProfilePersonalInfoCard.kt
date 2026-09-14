package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_email_change
import vitranshop.shared.generated.resources.account_email_change_hint
import vitranshop.shared.generated.resources.account_email_locked_a11y
import vitranshop.shared.generated.resources.account_city_placeholder
import vitranshop.shared.generated.resources.account_field_city
import vitranshop.shared.generated.resources.account_field_email
import vitranshop.shared.generated.resources.account_field_first_name
import vitranshop.shared.generated.resources.account_field_gender
import vitranshop.shared.generated.resources.account_field_last_name
import vitranshop.shared.generated.resources.account_field_phone
import vitranshop.shared.generated.resources.account_field_username
import vitranshop.shared.generated.resources.account_phone_change
import vitranshop.shared.generated.resources.account_phone_change_hint
import vitranshop.shared.generated.resources.account_phone_locked_a11y
import vitranshop.shared.generated.resources.account_section_contact
import vitranshop.shared.generated.resources.account_section_contact_hint
import vitranshop.shared.generated.resources.account_section_personal
import vitranshop.shared.generated.resources.account_section_personal_hint
import vitranshop.shared.generated.resources.account_username_available
import vitranshop.shared.generated.resources.account_username_check_error
import vitranshop.shared.generated.resources.account_username_checking
import vitranshop.shared.generated.resources.account_username_taken
import vitranshop.shared.generated.resources.ic_lock
import vitranshop.shared.generated.resources.ic_nav_profile

/** Username availability feedback shown under the profile username field. */
enum class UsernameCheckDisplay {
    Idle,
    Checking,
    Available,
    Taken,
    Error,
}

@Composable
internal fun ProfilePersonalInfoCard(
    profile: AccountProfile,
    onProfileChange: (AccountProfile) -> Unit,
    cities: List<AccountCityOption>,
    clearCityLabel: String,
    onCitySelect: (Long?) -> Unit,
    birthdayIso: String?,
    onBirthdayChange: (String?) -> Unit,
    citiesError: String? = null,
    usernameCheck: UsernameCheckDisplay = UsernameCheckDisplay.Idle,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AccountTokens.SectionGap),
    ) {
        PersonalFieldsCard(
            profile = profile,
            onProfileChange = onProfileChange,
            cities = cities,
            clearCityLabel = clearCityLabel,
            onCitySelect = onCitySelect,
            birthdayIso = birthdayIso,
            onBirthdayChange = onBirthdayChange,
            citiesError = citiesError,
            usernameCheck = usernameCheck,
        )
        ContactFieldsCard(profile = profile)
    }
}

@Composable
private fun PersonalFieldsCard(
    profile: AccountProfile,
    onProfileChange: (AccountProfile) -> Unit,
    cities: List<AccountCityOption>,
    clearCityLabel: String,
    onCitySelect: (Long?) -> Unit,
    birthdayIso: String?,
    onBirthdayChange: (String?) -> Unit,
    citiesError: String?,
    usernameCheck: UsernameCheckDisplay,
) {
    val genderUnspecified = AccountGender.Unspecified.label()
    val genderFemale = AccountGender.Female.label()
    val genderMale = AccountGender.Male.label()
    val genderOther = AccountGender.Other.label()
    val genderLabels = listOf(genderUnspecified, genderFemale, genderMale, genderOther)
    val selectedGenderLabel = profile.gender.label()
    val usernameSupporting = when (usernameCheck) {
        UsernameCheckDisplay.Idle -> null to false
        UsernameCheckDisplay.Checking ->
            stringResource(Res.string.account_username_checking) to false
        UsernameCheckDisplay.Available ->
            stringResource(Res.string.account_username_available) to true
        UsernameCheckDisplay.Taken ->
            stringResource(Res.string.account_username_taken) to false
        UsernameCheckDisplay.Error ->
            stringResource(Res.string.account_username_check_error) to false
    }

    AccountCard {
        Column(
            modifier = Modifier.padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            AccountSectionHeader(
                title = stringResource(Res.string.account_section_personal),
                hint = stringResource(Res.string.account_section_personal_hint),
                icon = painterResource(Res.drawable.ic_nav_profile),
            )
            AccountStackedField(
                label = stringResource(Res.string.account_field_first_name),
                value = profile.firstName,
                onValueChange = { onProfileChange(profile.copy(firstName = it)) },
            )
            AccountStackedField(
                label = stringResource(Res.string.account_field_last_name),
                value = profile.lastName,
                onValueChange = { onProfileChange(profile.copy(lastName = it)) },
            )
            AccountStackedField(
                label = stringResource(Res.string.account_field_username),
                value = profile.username,
                onValueChange = {
                    onProfileChange(
                        profile.copy(username = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' }),
                    )
                },
                supportingText = usernameSupporting.first,
                supportingPositive = usernameSupporting.second,
                showSupportingCheck = usernameCheck == UsernameCheckDisplay.Available,
            )
            AccountBirthdayField(
                birthdayIso = birthdayIso,
                onBirthdayChange = onBirthdayChange,
            )
            val cityOptions = listOf(clearCityLabel) + cities.map { it.name }
            val selectedCityName = when {
                profile.cityId == null -> ""
                else -> cities.firstOrNull { it.id == profile.cityId }?.name
                    ?: profile.cityName.orEmpty()
            }
            AccountDropdownField(
                label = stringResource(Res.string.account_field_city),
                value = selectedCityName,
                placeholder = stringResource(Res.string.account_city_placeholder),
                options = cityOptions,
                onSelect = { label ->
                    if (label == clearCityLabel) {
                        onCitySelect(null)
                    } else {
                        onCitySelect(cities.firstOrNull { it.name == label }?.id)
                    }
                },
                error = citiesError,
            )
            AccountDropdownField(
                label = stringResource(Res.string.account_field_gender),
                value = if (profile.gender == AccountGender.Unspecified) "" else selectedGenderLabel,
                placeholder = genderUnspecified,
                options = genderLabels,
                onSelect = { label ->
                    val next = when (label) {
                        genderFemale -> AccountGender.Female
                        genderMale -> AccountGender.Male
                        genderOther -> AccountGender.Other
                        else -> AccountGender.Unspecified
                    }
                    onProfileChange(profile.copy(gender = next))
                },
            )
        }
    }
}

@Composable
private fun ContactFieldsCard(profile: AccountProfile) {
    AccountCard {
        Column(
            modifier = Modifier.padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            AccountSectionHeader(
                title = stringResource(Res.string.account_section_contact),
                hint = stringResource(Res.string.account_section_contact_hint),
                icon = painterResource(Res.drawable.ic_lock),
            )
            AccountLockedField(
                label = stringResource(Res.string.account_field_email),
                value = profile.email,
                hint = stringResource(Res.string.account_email_change_hint),
                changeLabel = stringResource(Res.string.account_email_change),
                lockedA11y = stringResource(Res.string.account_email_locked_a11y),
                onChangeClick = { /* mock — verify-then-change */ },
            )
            AccountLockedField(
                label = stringResource(Res.string.account_field_phone),
                value = profile.formattedPhone,
                hint = stringResource(Res.string.account_phone_change_hint),
                changeLabel = stringResource(Res.string.account_phone_change),
                lockedA11y = stringResource(Res.string.account_phone_locked_a11y),
                onChangeClick = { /* mock — verify-then-change */ },
            )
        }
    }
}
