package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.ErrorRed
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_admin_plans_hub_hint
import vitranshop.shared.generated.resources.account_admin_plans_hub_title
import vitranshop.shared.generated.resources.account_cities_hub_hint
import vitranshop.shared.generated.resources.account_nav_cities
import vitranshop.shared.generated.resources.account_nav_settings
import vitranshop.shared.generated.resources.account_nav_users
import vitranshop.shared.generated.resources.account_privacy_data
import vitranshop.shared.generated.resources.account_privacy_data_hint
import vitranshop.shared.generated.resources.account_privacy_delete
import vitranshop.shared.generated.resources.account_privacy_delete_hint
import vitranshop.shared.generated.resources.account_privacy_personalization
import vitranshop.shared.generated.resources.account_privacy_personalization_hint
import vitranshop.shared.generated.resources.account_privacy_public_lists
import vitranshop.shared.generated.resources.account_privacy_public_lists_hint
import vitranshop.shared.generated.resources.account_privacy_public_profile
import vitranshop.shared.generated.resources.account_privacy_public_profile_hint
import vitranshop.shared.generated.resources.account_privacy_title
import vitranshop.shared.generated.resources.account_section_settings
import vitranshop.shared.generated.resources.account_settings_account
import vitranshop.shared.generated.resources.account_settings_account_hint
import vitranshop.shared.generated.resources.account_settings_notifications
import vitranshop.shared.generated.resources.account_settings_notifications_hint
import vitranshop.shared.generated.resources.account_settings_privacy
import vitranshop.shared.generated.resources.account_settings_privacy_hint
import vitranshop.shared.generated.resources.account_settings_security
import vitranshop.shared.generated.resources.account_settings_security_hint
import vitranshop.shared.generated.resources.account_sign_out
import vitranshop.shared.generated.resources.account_sign_out_hint
import vitranshop.shared.generated.resources.account_store_plan_hub_hint
import vitranshop.shared.generated.resources.account_store_plan_hub_title
import vitranshop.shared.generated.resources.account_users_hub_hint
import vitranshop.shared.generated.resources.ic_bell
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_city
import vitranshop.shared.generated.resources.ic_database
import vitranshop.shared.generated.resources.ic_delete
import vitranshop.shared.generated.resources.ic_diamond
import vitranshop.shared.generated.resources.ic_list
import vitranshop.shared.generated.resources.ic_lock
import vitranshop.shared.generated.resources.ic_logout
import vitranshop.shared.generated.resources.ic_people
import vitranshop.shared.generated.resources.ic_settings
import vitranshop.shared.generated.resources.ic_shield
import vitranshop.shared.generated.resources.ic_sparkles
import vitranshop.shared.generated.resources.ic_user
import vitranshop.shared.generated.resources.ic_visibility
import vitranshop.shared.generated.resources.ic_wallet

@Composable
internal fun AccountSettingsSection(
    privacy: AccountPrivacyPrefs,
    onPrivacyChange: (AccountPrivacyPrefs) -> Unit,
    onAccountInfoClick: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    if (isDesktop) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            SettingsMenuCard(
                onAccountInfoClick = onAccountInfoClick,
                onSignOut = onSignOut,
                modifier = Modifier.weight(1f),
            )
            PrivacyCard(
                privacy = privacy,
                onPrivacyChange = onPrivacyChange,
                modifier = Modifier.weight(1f),
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AccountTokens.SectionGap),
        ) {
            SettingsMenuCard(
                onAccountInfoClick = onAccountInfoClick,
                onSignOut = onSignOut,
            )
            PrivacyCard(
                privacy = privacy,
                onPrivacyChange = onPrivacyChange,
            )
        }
    }
}

@Composable
private fun SettingsMenuCard(
    onAccountInfoClick: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AccountCard(modifier = modifier) {
        Column(modifier = Modifier.padding(bottom = VitranSpacing.md)) {
            AccountSoftHeader(
                title = stringResource(Res.string.account_section_settings),
                icon = painterResource(Res.drawable.ic_settings),
                modifier = Modifier.padding(
                    start = VitranSpacing.lg,
                    end = VitranSpacing.lg,
                    top = VitranSpacing.lg,
                    bottom = VitranSpacing.sm,
                ),
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_settings_account),
                subtitle = stringResource(Res.string.account_settings_account_hint),
                icon = painterResource(Res.drawable.ic_user),
                onClick = onAccountInfoClick,
                trailing = { SettingsChevron() },
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_settings_security),
                subtitle = stringResource(Res.string.account_settings_security_hint),
                icon = painterResource(Res.drawable.ic_shield),
                onClick = {},
                trailing = { SettingsChevron() },
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_settings_notifications),
                subtitle = stringResource(Res.string.account_settings_notifications_hint),
                icon = painterResource(Res.drawable.ic_bell),
                onClick = {},
                trailing = { SettingsChevron() },
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_settings_privacy),
                subtitle = stringResource(Res.string.account_settings_privacy_hint),
                icon = painterResource(Res.drawable.ic_lock),
                onClick = {},
                trailing = { SettingsChevron() },
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_sign_out),
                subtitle = stringResource(Res.string.account_sign_out_hint),
                icon = painterResource(Res.drawable.ic_logout),
                onClick = onSignOut,
                destructive = true,
            )
        }
    }
}

@Composable
private fun PrivacyCard(
    privacy: AccountPrivacyPrefs,
    onPrivacyChange: (AccountPrivacyPrefs) -> Unit,
    modifier: Modifier = Modifier,
) {
    AccountCard(modifier = modifier) {
        Column(modifier = Modifier.padding(bottom = VitranSpacing.md)) {
            AccountSoftHeader(
                title = stringResource(Res.string.account_privacy_title),
                icon = painterResource(Res.drawable.ic_lock),
                modifier = Modifier.padding(
                    start = VitranSpacing.lg,
                    end = VitranSpacing.lg,
                    top = VitranSpacing.lg,
                    bottom = VitranSpacing.sm,
                ),
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_privacy_public_profile),
                subtitle = stringResource(Res.string.account_privacy_public_profile_hint),
                icon = painterResource(Res.drawable.ic_visibility),
                onClick = {
                    onPrivacyChange(privacy.copy(publicProfile = !privacy.publicProfile))
                },
                trailing = {
                    SettingsToggle(
                        checked = privacy.publicProfile,
                        onCheckedChange = {
                            onPrivacyChange(privacy.copy(publicProfile = it))
                        },
                    )
                },
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_privacy_public_lists),
                subtitle = stringResource(Res.string.account_privacy_public_lists_hint),
                icon = painterResource(Res.drawable.ic_list),
                onClick = {
                    onPrivacyChange(privacy.copy(publicLists = !privacy.publicLists))
                },
                trailing = {
                    SettingsToggle(
                        checked = privacy.publicLists,
                        onCheckedChange = {
                            onPrivacyChange(privacy.copy(publicLists = it))
                        },
                    )
                },
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_privacy_personalization),
                subtitle = stringResource(Res.string.account_privacy_personalization_hint),
                icon = painterResource(Res.drawable.ic_sparkles),
                onClick = {
                    onPrivacyChange(privacy.copy(personalization = !privacy.personalization))
                },
                trailing = {
                    SettingsToggle(
                        checked = privacy.personalization,
                        onCheckedChange = {
                            onPrivacyChange(privacy.copy(personalization = it))
                        },
                    )
                },
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_privacy_data),
                subtitle = stringResource(Res.string.account_privacy_data_hint),
                icon = painterResource(Res.drawable.ic_database),
                onClick = {},
                trailing = { SettingsChevron() },
            )
            SettingsActionRow(
                title = stringResource(Res.string.account_privacy_delete),
                subtitle = stringResource(Res.string.account_privacy_delete_hint),
                icon = painterResource(Res.drawable.ic_delete),
                onClick = {},
                destructive = true,
                trailing = { SettingsChevron(destructive = true) },
            )
        }
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    val titleColor = if (destructive) ErrorRed else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (destructive) {
        ErrorRed.copy(alpha = 0.72f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val rowShape = RoundedCornerShape(VitranRadius.medium)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (destructive) {
                    Modifier.padding(
                        horizontal = VitranSpacing.md,
                        vertical = VitranSpacing.xs,
                    )
                } else {
                    Modifier
                },
            )
            .clip(rowShape)
            .background(if (destructive) AccountTokens.DangerSoft else Color.Transparent)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(
                horizontal = if (destructive) VitranSpacing.md else VitranSpacing.lg,
                vertical = VitranSpacing.md,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AccountSoftIcon(painter = icon, destructive = destructive)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(
                text = title,
                style = VitranTextStyle.Title,
                color = titleColor,
                maxLines = 1,
            )
            VitranText(
                text = subtitle,
                style = VitranTextStyle.Body,
                color = subtitleColor,
                maxLines = 2,
            )
        }
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
private fun SettingsChevron(destructive: Boolean = false) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    VitranIcon(
        painter = painterResource(Res.drawable.ic_chevron_right),
        contentDescription = null,
        size = VitranSize.iconSmall,
        tint = if (destructive) ErrorRed else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
    )
}

@Composable
private fun SettingsToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            checkedBorderColor = MaterialTheme.colorScheme.primary,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = AccountTokens.ChipBorder,
            uncheckedBorderColor = AccountTokens.ChipBorder,
        ),
    )
}

@Composable
internal fun AccountHubSettingsRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AccountCard(modifier = modifier) {
        SettingsActionRow(
            title = stringResource(Res.string.account_nav_settings),
            subtitle = stringResource(Res.string.account_settings_privacy_hint),
            icon = painterResource(Res.drawable.ic_settings),
            onClick = onClick,
            trailing = { SettingsChevron() },
        )
    }
}

@Composable
internal fun AccountHubUsersRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AccountCard(modifier = modifier) {
        SettingsActionRow(
            title = stringResource(Res.string.account_nav_users),
            subtitle = stringResource(Res.string.account_users_hub_hint),
            icon = painterResource(Res.drawable.ic_people),
            onClick = onClick,
            trailing = { SettingsChevron() },
        )
    }
}

@Composable
internal fun AccountHubCitiesRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AccountCard(modifier = modifier) {
        SettingsActionRow(
            title = stringResource(Res.string.account_nav_cities),
            subtitle = stringResource(Res.string.account_cities_hub_hint),
            icon = painterResource(Res.drawable.ic_city),
            onClick = onClick,
            trailing = { SettingsChevron() },
        )
    }
}

@Composable
internal fun AccountHubStorePlanRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AccountCard(modifier = modifier) {
        SettingsActionRow(
            title = stringResource(Res.string.account_store_plan_hub_title),
            subtitle = stringResource(Res.string.account_store_plan_hub_hint),
            icon = painterResource(Res.drawable.ic_wallet),
            onClick = onClick,
            trailing = { SettingsChevron() },
        )
    }
}

@Composable
internal fun AccountHubAdminPlansRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AccountCard(modifier = modifier) {
        SettingsActionRow(
            title = stringResource(Res.string.account_admin_plans_hub_title),
            subtitle = stringResource(Res.string.account_admin_plans_hub_hint),
            icon = painterResource(Res.drawable.ic_diamond),
            onClick = onClick,
            trailing = { SettingsChevron() },
        )
    }
}
