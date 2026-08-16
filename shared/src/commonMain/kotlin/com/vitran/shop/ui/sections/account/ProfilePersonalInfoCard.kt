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
import vitranshop.shared.generated.resources.account_birthday_placeholder
import vitranshop.shared.generated.resources.account_email_change
import vitranshop.shared.generated.resources.account_email_change_hint
import vitranshop.shared.generated.resources.account_email_locked_a11y
import vitranshop.shared.generated.resources.account_field_birthday
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
import vitranshop.shared.generated.resources.account_username_taken
import vitranshop.shared.generated.resources.ic_calendar
import vitranshop.shared.generated.resources.ic_lock
import vitranshop.shared.generated.resources.ic_nav_profile

@Composable
internal fun ProfilePersonalInfoCard(
    profile: AccountProfile,
    onProfileChange: (AccountProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AccountTokens.SectionGap),
    ) {
        PersonalFieldsCard(profile = profile, onProfileChange = onProfileChange)
        ContactFieldsCard(profile = profile)
    }
}

@Composable
private fun PersonalFieldsCard(
    profile: AccountProfile,
    onProfileChange: (AccountProfile) -> Unit,
) {
    val genderUnspecified = AccountGender.Unspecified.label()
    val genderFemale = AccountGender.Female.label()
    val genderMale = AccountGender.Male.label()
    val genderOther = AccountGender.Other.label()
    val genderLabels = listOf(genderUnspecified, genderFemale, genderMale, genderOther)
    val selectedGenderLabel = profile.gender.label()
    val usernameAvailable = profile.username.length >= 3 &&
        !profile.username.equals("admin", ignoreCase = true)

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
                supportingText = if (usernameAvailable) {
                    stringResource(Res.string.account_username_available)
                } else {
                    stringResource(Res.string.account_username_taken)
                },
                supportingPositive = usernameAvailable,
                showSupportingCheck = true,
            )
            AccountStackedField(
                label = stringResource(Res.string.account_field_birthday),
                value = profile.birthday,
                onValueChange = { onProfileChange(profile.copy(birthday = it)) },
                placeholder = stringResource(Res.string.account_birthday_placeholder),
                trailing = {
                    AccountTrailingIcon(
                        painter = painterResource(Res.drawable.ic_calendar),
                        contentDescription = null,
                    )
                },
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
