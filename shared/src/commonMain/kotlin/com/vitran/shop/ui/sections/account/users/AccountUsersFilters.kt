package com.vitran.shop.ui.sections.account.users

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountDropdownField
import com.vitran.shop.ui.sections.account.AccountOutlinedButton
import com.vitran.shop.ui.sections.account.AccountStackedField
import com.vitran.shop.ui.sections.account.AccountTextLink
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.AccountTrailingIcon
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_users_clear_filters
import vitranshop.shared.generated.resources.account_users_filter_phone_chip
import vitranshop.shared.generated.resources.account_users_filter_role_chip
import vitranshop.shared.generated.resources.account_users_filter_search_chip
import vitranshop.shared.generated.resources.account_users_filter_status_chip
import vitranshop.shared.generated.resources.account_users_filters
import vitranshop.shared.generated.resources.account_users_phone
import vitranshop.shared.generated.resources.account_users_phone_clear_a11y
import vitranshop.shared.generated.resources.account_users_role
import vitranshop.shared.generated.resources.account_users_role_all
import vitranshop.shared.generated.resources.account_users_search_clear_a11y
import vitranshop.shared.generated.resources.account_users_search_placeholder
import vitranshop.shared.generated.resources.account_users_status
import vitranshop.shared.generated.resources.account_users_status_all
import vitranshop.shared.generated.resources.ic_close
import vitranshop.shared.generated.resources.ic_filter_sliders
import vitranshop.shared.generated.resources.ic_search

@Composable
internal fun AccountUsersFilters(
    filters: AccountUsersFilterState,
    onFiltersChange: (AccountUsersFilterState) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    val roleAll = stringResource(Res.string.account_users_role_all)
    val statusAll = stringResource(Res.string.account_users_status_all)
    val roleChoices = listOf(
        null to roleAll,
        AccountUserRole.Customer to AccountUserRole.Customer.label(),
        AccountUserRole.Seller to AccountUserRole.Seller.label(),
        AccountUserRole.Manager to AccountUserRole.Manager.label(),
        AccountUserRole.Support to AccountUserRole.Support.label(),
    )
    val statusChoices = listOf(
        null to statusAll,
        AccountUserStatus.Active to AccountUserStatus.Active.label(),
        AccountUserStatus.Inactive to AccountUserStatus.Inactive.label(),
    )
    val roleOptions = roleChoices.map { it.second }
    val statusOptions = statusChoices.map { it.second }
    val selectedRole = when (val role = filters.role) {
        null -> roleAll
        else -> role.label()
    }
    val selectedStatus = when (val status = filters.status) {
        null -> statusAll
        else -> status.label()
    }

    AccountCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            if (isDesktop) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    AccountUsersSearchField(
                        value = filters.search,
                        onValueChange = { onFiltersChange(filters.copy(search = it)) },
                        modifier = Modifier.weight(1.4f),
                    )
                    if (expanded) {
                        AccountDropdownField(
                            label = stringResource(Res.string.account_users_role),
                            value = selectedRole,
                            placeholder = roleAll,
                            options = roleOptions,
                            onSelect = { label ->
                                val role = roleChoices.firstOrNull { it.second == label }?.first
                                onFiltersChange(filters.copy(role = role))
                            },
                            modifier = Modifier.weight(1f),
                        )
                        AccountDropdownField(
                            label = stringResource(Res.string.account_users_status),
                            value = selectedStatus,
                            placeholder = statusAll,
                            options = statusOptions,
                            onSelect = { label ->
                                val status = statusChoices.firstOrNull { it.second == label }?.first
                                onFiltersChange(filters.copy(status = status))
                            },
                            modifier = Modifier.weight(1f),
                        )
                        AccountStackedField(
                            label = stringResource(Res.string.account_users_phone),
                            value = filters.phone,
                            onValueChange = { onFiltersChange(filters.copy(phone = it)) },
                            trailing = {
                                if (filters.phone.isNotBlank()) {
                                    Box(
                                        modifier = Modifier.clickable(role = Role.Button) {
                                            onFiltersChange(filters.copy(phone = ""))
                                        },
                                    ) {
                                        AccountTrailingIcon(
                                            painter = painterResource(Res.drawable.ic_close),
                                            contentDescription = stringResource(
                                                Res.string.account_users_phone_clear_a11y,
                                            ),
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    AccountUsersFiltersToggle(
                        expanded = expanded,
                        onClick = { onExpandedChange(!expanded) },
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                    ) {
                        AccountUsersSearchField(
                            value = filters.search,
                            onValueChange = { onFiltersChange(filters.copy(search = it)) },
                            modifier = Modifier.weight(1f),
                        )
                        AccountUsersFiltersToggle(
                            expanded = expanded,
                            onClick = { onExpandedChange(!expanded) },
                        )
                    }
                    if (expanded) {
                        AccountDropdownField(
                            label = stringResource(Res.string.account_users_role),
                            value = selectedRole,
                            placeholder = roleAll,
                            options = roleOptions,
                            onSelect = { label ->
                                val role = roleChoices.firstOrNull { it.second == label }?.first
                                onFiltersChange(filters.copy(role = role))
                            },
                        )
                        AccountDropdownField(
                            label = stringResource(Res.string.account_users_status),
                            value = selectedStatus,
                            placeholder = statusAll,
                            options = statusOptions,
                            onSelect = { label ->
                                val status = statusChoices.firstOrNull { it.second == label }?.first
                                onFiltersChange(filters.copy(status = status))
                            },
                        )
                        AccountStackedField(
                            label = stringResource(Res.string.account_users_phone),
                            value = filters.phone,
                            onValueChange = { onFiltersChange(filters.copy(phone = it)) },
                            trailing = {
                                if (filters.phone.isNotBlank()) {
                                    Box(
                                        modifier = Modifier.clickable(role = Role.Button) {
                                            onFiltersChange(filters.copy(phone = ""))
                                        },
                                    ) {
                                        AccountTrailingIcon(
                                            painter = painterResource(Res.drawable.ic_close),
                                            contentDescription = stringResource(
                                                Res.string.account_users_phone_clear_a11y,
                                            ),
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }
            if (filters.hasActiveFilters) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                    ) {
                        if (filters.search.isNotBlank()) {
                            AccountUsersFilterChip(
                                label = stringResource(
                                    Res.string.account_users_filter_search_chip,
                                    toPersianDigits(filters.search),
                                ),
                                onRemove = { onFiltersChange(filters.copy(search = "")) },
                            )
                        }
                        val roleFilter = filters.role
                        if (roleFilter != null) {
                            AccountUsersFilterChip(
                                label = stringResource(
                                    Res.string.account_users_filter_role_chip,
                                    roleFilter.label(),
                                ),
                                onRemove = { onFiltersChange(filters.copy(role = null)) },
                            )
                        }
                        val statusFilter = filters.status
                        if (statusFilter != null) {
                            AccountUsersFilterChip(
                                label = stringResource(
                                    Res.string.account_users_filter_status_chip,
                                    statusFilter.label(),
                                ),
                                onRemove = { onFiltersChange(filters.copy(status = null)) },
                            )
                        }
                        if (filters.phone.isNotBlank()) {
                            AccountUsersFilterChip(
                                label = stringResource(
                                    Res.string.account_users_filter_phone_chip,
                                    toPersianDigits(filters.phone),
                                ),
                                onRemove = { onFiltersChange(filters.copy(phone = "")) },
                            )
                        }
                    }
                    AccountTextLink(
                        label = stringResource(Res.string.account_users_clear_filters),
                        onClick = { onFiltersChange(AccountUsersFilterState()) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountUsersFiltersToggle(
    expanded: Boolean,
    onClick: () -> Unit,
) {
    AccountOutlinedButton(
        label = stringResource(Res.string.account_users_filters),
        onClick = onClick,
        icon = painterResource(Res.drawable.ic_filter_sliders),
        borderColor = if (expanded) ShopPurple else AccountTokens.CardBorder,
        contentColor = if (expanded) ShopPurple else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.widthIn(min = 112.dp),
    )
}

@Composable
private fun AccountUsersSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(AccountTokens.StackedFieldHeight)
            .clip(shape)
            .border(1.dp, AccountTokens.CardBorder, shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .padding(horizontal = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { inner ->
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    if (value.isBlank()) {
                        VitranText(
                            text = stringResource(Res.string.account_users_search_placeholder),
                            style = VitranTextStyle.Body,
                            color = AccountTokens.Placeholder,
                            maxLines = 1,
                        )
                    }
                    inner()
                }
            },
            modifier = Modifier.weight(1f),
        )
        if (value.isNotBlank()) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = stringResource(Res.string.account_users_search_clear_a11y),
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(role = Role.Button) { onValueChange("") },
            )
        }
    }
}
