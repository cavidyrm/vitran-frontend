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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountSelectMenu
import com.vitran.shop.ui.sections.account.AccountStackedField
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_user_detail_basics
import vitranshop.shared.generated.resources.account_user_detail_email_placeholder
import vitranshop.shared.generated.resources.account_user_detail_field_email
import vitranshop.shared.generated.resources.account_user_detail_field_id
import vitranshop.shared.generated.resources.account_user_detail_field_name
import vitranshop.shared.generated.resources.account_user_detail_field_phone
import vitranshop.shared.generated.resources.account_user_detail_note_counter
import vitranshop.shared.generated.resources.account_user_detail_note_hint
import vitranshop.shared.generated.resources.account_user_detail_note_placeholder
import vitranshop.shared.generated.resources.account_user_detail_note_title
import vitranshop.shared.generated.resources.account_user_detail_roles_hint
import vitranshop.shared.generated.resources.account_user_detail_roles_label
import vitranshop.shared.generated.resources.account_user_detail_roles_title
import vitranshop.shared.generated.resources.account_user_detail_status_title
import vitranshop.shared.generated.resources.account_user_detail_toggle_active
import vitranshop.shared.generated.resources.account_user_detail_toggle_active_hint
import vitranshop.shared.generated.resources.account_user_detail_toggle_notify
import vitranshop.shared.generated.resources.account_user_detail_toggle_notify_hint
import vitranshop.shared.generated.resources.account_user_detail_toggle_verified
import vitranshop.shared.generated.resources.account_user_detail_toggle_verified_hint
import vitranshop.shared.generated.resources.ic_chevron_down

@Composable
internal fun AccountUserEditForm(
    user: AccountUser,
    onUserChange: (AccountUser) -> Unit,
    modifier: Modifier = Modifier,
    adminMode: Boolean = false,
    availableRoles: List<AccountUserRole> = AccountUserRole.entries,
) {
    val isDesktop = LocalDesktopLayout.current
    AccountCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xxl),
        ) {
            AccountUserFormSection(title = stringResource(Res.string.account_user_detail_basics)) {
                if (isDesktop) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                    ) {
                        AccountStackedField(
                            label = stringResource(Res.string.account_user_detail_field_id),
                            value = toPersianDigits(user.id),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f),
                        )
                        AccountStackedField(
                            label = stringResource(Res.string.account_user_detail_field_phone),
                            value = user.phone,
                            onValueChange = { onUserChange(user.copy(phone = it)) },
                            required = true,
                            readOnly = adminMode,
                            modifier = Modifier.weight(1f),
                        )
                        AccountStackedField(
                            label = stringResource(Res.string.account_user_detail_field_name),
                            value = user.fullName,
                            onValueChange = { name ->
                                val parts = name.trim().split(" ", limit = 2)
                                onUserChange(
                                    user.copy(
                                        firstName = parts.getOrElse(0) { "" },
                                        lastName = parts.getOrElse(1) { "" },
                                    ),
                                )
                            },
                            required = true,
                            readOnly = adminMode,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    AccountStackedField(
                        label = stringResource(Res.string.account_user_detail_field_id),
                        value = toPersianDigits(user.id),
                        onValueChange = {},
                        readOnly = true,
                    )
                    AccountStackedField(
                        label = stringResource(Res.string.account_user_detail_field_phone),
                        value = user.phone,
                        onValueChange = { onUserChange(user.copy(phone = it)) },
                        required = true,
                        readOnly = adminMode,
                    )
                    AccountStackedField(
                        label = stringResource(Res.string.account_user_detail_field_name),
                        value = user.fullName,
                        onValueChange = { name ->
                            val parts = name.trim().split(" ", limit = 2)
                            onUserChange(
                                user.copy(
                                    firstName = parts.getOrElse(0) { "" },
                                    lastName = parts.getOrElse(1) { "" },
                                ),
                            )
                        },
                        required = true,
                        readOnly = adminMode,
                    )
                }
                AccountStackedField(
                    label = stringResource(Res.string.account_user_detail_field_email),
                    value = user.email,
                    onValueChange = { onUserChange(user.copy(email = it)) },
                    placeholder = stringResource(Res.string.account_user_detail_email_placeholder),
                    readOnly = adminMode,
                )
            }
            AccountUserFormSection(title = stringResource(Res.string.account_user_detail_roles_title)) {
                AccountUserRolesField(
                    selected = user.roles,
                    onSelectedChange = { onUserChange(user.copy(roles = it)) },
                    availableRoles = availableRoles,
                )
                VitranText(
                    text = stringResource(Res.string.account_user_detail_roles_hint),
                    style = VitranTextStyle.Label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            AccountUserFormSection(title = stringResource(Res.string.account_user_detail_status_title)) {
                AccountUserToggleRow(
                    title = stringResource(Res.string.account_user_detail_toggle_active),
                    hint = stringResource(Res.string.account_user_detail_toggle_active_hint),
                    checked = user.status == AccountUserStatus.Active,
                    onCheckedChange = { checked ->
                        onUserChange(
                            user.copy(
                                status = if (checked) {
                                    AccountUserStatus.Active
                                } else {
                                    AccountUserStatus.Inactive
                                },
                            ),
                        )
                    },
                )
                if (!adminMode) {
                    AccountUserToggleRow(
                        title = stringResource(Res.string.account_user_detail_toggle_verified),
                        hint = stringResource(Res.string.account_user_detail_toggle_verified_hint),
                        checked = user.phoneVerified,
                        onCheckedChange = { onUserChange(user.copy(phoneVerified = it)) },
                    )
                    AccountUserToggleRow(
                        title = stringResource(Res.string.account_user_detail_toggle_notify),
                        hint = stringResource(Res.string.account_user_detail_toggle_notify_hint),
                        checked = user.notificationsEnabled,
                        onCheckedChange = { onUserChange(user.copy(notificationsEnabled = it)) },
                    )
                }
            }
            if (!adminMode) {
                AccountUserFormSection(title = stringResource(Res.string.account_user_detail_note_title)) {
                    AccountUserNoteField(
                        value = user.internalNote,
                        onValueChange = { note ->
                            onUserChange(user.copy(internalNote = note.take(AccountUserNoteMaxLength)))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountUserFormSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        VitranText(
            text = title,
            style = VitranTextStyle.Title,
            color = MaterialTheme.colorScheme.onSurface,
        )
        content()
    }
}

@Composable
private fun AccountUserRolesField(
    selected: List<AccountUserRole>,
    onSelectedChange: (List<AccountUserRole>) -> Unit,
    availableRoles: List<AccountUserRole>,
) {
    val customerLabel = AccountUserRole.Customer.label()
    val sellerLabel = AccountUserRole.Seller.label()
    val managerLabel = AccountUserRole.Manager.label()
    val supportLabel = AccountUserRole.Support.label()
    val roleLabels = mapOf(
        AccountUserRole.Customer to customerLabel,
        AccountUserRole.Seller to sellerLabel,
        AccountUserRole.Manager to managerLabel,
        AccountUserRole.Support to supportLabel,
    )
    val allRoles = availableRoles
    val available = allRoles.filterNot { selected.contains(it) }
    var open by remember { mutableStateOf(false) }
    var fieldWidthPx by remember { mutableIntStateOf(0) }
    var fieldHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val shape = RoundedCornerShape(VitranRadius.small)

    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        VitranText(
            text = stringResource(Res.string.account_user_detail_roles_label),
            style = VitranTextStyle.Label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    fieldWidthPx = size.width
                    fieldHeightPx = size.height
                }
                .clip(shape)
                .border(1.dp, AccountTokens.CardBorder, shape)
                .background(MaterialTheme.colorScheme.surface, shape)
                .clickable(enabled = available.isNotEmpty(), role = Role.Button) { open = true }
                .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                ) {
                    selected.forEach { role ->
                        AccountUserRoleChip(
                            role = role,
                            onRemove = { onSelectedChange(selected - role) },
                        )
                    }
                }
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_chevron_down),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            AccountSelectMenu(
                expanded = open && available.isNotEmpty(),
                onDismiss = { open = false },
                options = available.map { roleLabels.getValue(it) },
                selected = "",
                onSelect = { label ->
                    val role = allRoles.firstOrNull { roleLabels[it] == label }
                    if (role != null) {
                        onSelectedChange(selected + role)
                        open = false
                    }
                },
                menuWidth = with(density) {
                    if (fieldWidthPx > 0) fieldWidthPx.toDp() else 0.dp
                },
                anchorHeightPx = fieldHeightPx,
            )
        }
    }
}

@Composable
private fun AccountUserToggleRow(
    title: String,
    hint: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(
                text = title,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            VitranText(
                text = hint,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
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
}

@Composable
private fun AccountUserNoteField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AccountTokens.UserNoteMinHeight)
                .clip(shape)
                .border(1.dp, AccountTokens.CardBorder, shape)
                .background(MaterialTheme.colorScheme.surface, shape)
                .padding(VitranSpacing.md),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { inner ->
                    Box {
                        if (value.isBlank()) {
                            VitranText(
                                text = stringResource(Res.string.account_user_detail_note_placeholder),
                                style = VitranTextStyle.Body,
                                color = AccountTokens.Placeholder,
                            )
                        }
                        inner()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        VitranText(
            text = stringResource(
                Res.string.account_user_detail_note_counter,
                toPersianDigits(value.length),
                toPersianDigits(AccountUserNoteMaxLength),
            ),
            style = VitranTextStyle.Label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        VitranText(
            text = stringResource(Res.string.account_user_detail_note_hint),
            style = VitranTextStyle.Label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
