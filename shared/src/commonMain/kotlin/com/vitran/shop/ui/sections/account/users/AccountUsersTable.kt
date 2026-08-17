package com.vitran.shop.ui.sections.account.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_users_actions_a11y
import vitranshop.shared.generated.resources.account_users_col_actions
import vitranshop.shared.generated.resources.account_users_col_joined
import vitranshop.shared.generated.resources.account_users_col_phone
import vitranshop.shared.generated.resources.account_users_col_roles
import vitranshop.shared.generated.resources.account_users_col_status
import vitranshop.shared.generated.resources.account_users_col_user
import vitranshop.shared.generated.resources.account_users_empty
import vitranshop.shared.generated.resources.account_users_id
import vitranshop.shared.generated.resources.account_users_sort_joined_a11y
import vitranshop.shared.generated.resources.ic_more_horiz
import vitranshop.shared.generated.resources.ic_unfold

private const val ColUser = 1.7f
private const val ColRoles = 1.5f
private const val ColPhone = 1.2f
private const val ColStatus = 0.9f
private const val ColJoined = 1.1f
private const val ColActions = 0.45f

@Composable
internal fun AccountUsersTable(
    users: List<AccountUser>,
    sort: AccountUsersSort,
    onSortChange: (AccountUsersSort) -> Unit,
    onUserClick: (AccountUser) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    AccountCard(modifier = modifier) {
        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VitranSpacing.xxxl),
                contentAlignment = Alignment.Center,
            ) {
                VitranText(
                    text = stringResource(Res.string.account_users_empty),
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else if (isDesktop) {
            AccountUsersTableHeader(sort = sort, onSortChange = onSortChange)
            users.forEach { user ->
                HorizontalDivider(
                    thickness = VitranSize.borderHairline,
                    color = AccountTokens.FieldDivider,
                )
                AccountUsersTableRow(user = user, onUserClick = onUserClick)
            }
        } else {
            users.forEachIndexed { index, user ->
                AccountUsersCompactRow(user = user, onUserClick = onUserClick)
                if (index != users.lastIndex) {
                    HorizontalDivider(
                        thickness = VitranSize.borderHairline,
                        color = AccountTokens.FieldDivider,
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountUsersTableHeader(
    sort: AccountUsersSort,
    onSortChange: (AccountUsersSort) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AccountTokens.ImagePlaceholder)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccountUsersHeaderCell(
            label = stringResource(Res.string.account_users_col_user),
            modifier = Modifier.weight(ColUser),
        )
        AccountUsersHeaderCell(
            label = stringResource(Res.string.account_users_col_roles),
            modifier = Modifier.weight(ColRoles),
        )
        AccountUsersHeaderCell(
            label = stringResource(Res.string.account_users_col_phone),
            modifier = Modifier.weight(ColPhone),
        )
        AccountUsersHeaderCell(
            label = stringResource(Res.string.account_users_col_status),
            modifier = Modifier.weight(ColStatus),
        )
        Row(
            modifier = Modifier
                .weight(ColJoined)
                .clickable(role = Role.Button) {
                    onSortChange(
                        if (sort == AccountUsersSort.JoinedDesc) {
                            AccountUsersSort.JoinedAsc
                        } else {
                            AccountUsersSort.JoinedDesc
                        },
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            androidx.compose.material3.Text(
                text = stringResource(Res.string.account_users_col_joined),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
            )
            VitranIcon(
                painter = painterResource(Res.drawable.ic_unfold),
                contentDescription = stringResource(Res.string.account_users_sort_joined_a11y),
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer {
                    rotationZ = if (sort == AccountUsersSort.JoinedAsc) 180f else 0f
                },
            )
        }
        AccountUsersHeaderCell(
            label = stringResource(Res.string.account_users_col_actions),
            modifier = Modifier.weight(ColActions),
        )
    }
}

@Composable
private fun AccountUsersHeaderCell(
    label: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Text(
        text = label,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        ),
        modifier = modifier,
        maxLines = 1,
    )
}

@Composable
private fun AccountUsersTableRow(
    user: AccountUser,
    onUserClick: (AccountUser) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { onUserClick(user) }
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccountUsersIdentity(
            user = user,
            modifier = Modifier.weight(ColUser),
        )
        FlowRow(
            modifier = Modifier.weight(ColRoles),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            user.roles.forEach { role ->
                AccountUserRoleChip(role = role)
            }
        }
        VitranText(
            text = toPersianDigits(user.phone),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier.weight(ColPhone),
        )
        Box(modifier = Modifier.weight(ColStatus)) {
            AccountUserStatusChip(status = user.status)
        }
        VitranText(
            text = toPersianDigits(user.joinedJalali),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier.weight(ColJoined),
        )
        Box(
            modifier = Modifier.weight(ColActions),
            contentAlignment = Alignment.Center,
        ) {
            AccountUsersOverflowButton(onClick = { onUserClick(user) })
        }
    }
}

@Composable
private fun AccountUsersCompactRow(
    user: AccountUser,
    onUserClick: (AccountUser) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { onUserClick(user) }
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AccountUserAvatar(initial = user.initial)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            AccountUsersNameBlock(user = user)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                user.roles.forEach { role ->
                    AccountUserRoleChip(role = role)
                }
            }
            VitranText(
                text = toPersianDigits(user.phone),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            ) {
                AccountUserStatusChip(status = user.status)
                VitranText(
                    text = toPersianDigits(user.joinedJalali),
                    style = VitranTextStyle.Label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
        AccountUsersOverflowButton(onClick = { onUserClick(user) })
    }
}

@Composable
private fun AccountUsersIdentity(
    user: AccountUser,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AccountUserAvatar(initial = user.initial)
        AccountUsersNameBlock(user = user)
    }
}

@Composable
private fun AccountUsersNameBlock(user: AccountUser) {
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs)) {
        VitranText(
            text = user.fullName,
            style = VitranTextStyle.Title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        VitranText(
            text = stringResource(Res.string.account_users_id, toPersianDigits(user.id)),
            style = VitranTextStyle.Label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun AccountUsersOverflowButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(VitranSize.touchTarget)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_more_horiz),
            contentDescription = stringResource(Res.string.account_users_actions_a11y),
            size = VitranSize.iconMedium,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
