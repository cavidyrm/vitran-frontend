package com.vitran.shop.ui.sections.account.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.ShopPurpleTint
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_user_detail_events_title
import vitranshop.shared.generated.resources.account_user_detail_event_created
import vitranshop.shared.generated.resources.account_user_detail_event_role
import vitranshop.shared.generated.resources.account_user_detail_event_verified
import vitranshop.shared.generated.resources.account_user_detail_joined
import vitranshop.shared.generated.resources.account_user_detail_last_login
import vitranshop.shared.generated.resources.account_user_detail_stat_credit
import vitranshop.shared.generated.resources.account_user_detail_stat_credit_value
import vitranshop.shared.generated.resources.account_user_detail_stat_invites
import vitranshop.shared.generated.resources.account_user_detail_stat_orders
import vitranshop.shared.generated.resources.account_user_detail_stats_title
import vitranshop.shared.generated.resources.account_user_detail_status_title
import vitranshop.shared.generated.resources.account_user_detail_unverified
import vitranshop.shared.generated.resources.account_user_detail_verified
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.ic_gift
import vitranshop.shared.generated.resources.ic_policy_order
import vitranshop.shared.generated.resources.ic_shield
import vitranshop.shared.generated.resources.ic_sparkles
import vitranshop.shared.generated.resources.ic_user

private val EventVerified = Color(0xFF15803D)
private val EventCreated = Color(0xFF0369A1)

@Composable
internal fun AccountUserSummaryRail(
    user: AccountUser,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(AccountTokens.UserDetailRailWidth),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AccountUserProfileCard(user = user)
        AccountUserStatusSummaryCard(user = user)
        AccountUserStatsCard(user = user)
        AccountUserEventsCard(events = user.events)
    }
}

@Composable
internal fun AccountUserSummaryStack(
    user: AccountUser,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AccountUserProfileCard(user = user)
        AccountUserStatusSummaryCard(user = user)
        AccountUserStatsCard(user = user)
        AccountUserEventsCard(events = user.events)
    }
}

@Composable
private fun AccountUserProfileCard(user: AccountUser) {
    AccountCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(AccountTokens.UserDetailAvatar)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_user),
                    contentDescription = null,
                    size = VitranSize.iconLarge,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            VitranText(
                text = user.fullName,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            VitranText(
                text = toPersianDigits(user.phone),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            VitranText(
                text = stringResource(
                    Res.string.account_user_detail_joined,
                    toPersianDigits(user.joinedJalali),
                ),
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            VitranText(
                text = stringResource(
                    Res.string.account_user_detail_last_login,
                    user.lastLoginLabel,
                ),
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AccountUserStatusSummaryCard(user: AccountUser) {
    AccountCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            VitranText(
                text = stringResource(Res.string.account_user_detail_status_title),
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                user.roles.forEach { role ->
                    AccountUserRoleChip(role = role)
                }
                AccountUserVerifiedChip(verified = user.phoneVerified)
                AccountUserStatusChip(status = user.status)
            }
        }
    }
}

@Composable
private fun AccountUserVerifiedChip(verified: Boolean) {
    val fg = if (verified) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurfaceVariant
    val bg = if (verified) AccountTokens.ReferralSuccessSoft else AccountTokens.ImagePlaceholder
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(VitranRadius.medium))
            .background(bg)
            .padding(horizontal = VitranSpacing.sm, vertical = VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        VitranIcon(
            painter = painterResource(
                if (verified) Res.drawable.ic_shield else Res.drawable.ic_check,
            ),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = fg,
        )
        VitranText(
            text = stringResource(
                if (verified) {
                    Res.string.account_user_detail_verified
                } else {
                    Res.string.account_user_detail_unverified
                },
            ),
            style = VitranTextStyle.Label,
            color = fg,
            maxLines = 1,
        )
    }
}

@Composable
private fun AccountUserStatsCard(user: AccountUser) {
    AccountCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            VitranText(
                text = stringResource(Res.string.account_user_detail_stats_title),
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                AccountUserStatCell(
                    value = toPersianDigits(user.orderCount),
                    label = stringResource(Res.string.account_user_detail_stat_orders),
                    icon = painterResource(Res.drawable.ic_policy_order),
                    modifier = Modifier.weight(1f),
                )
                AccountUserStatCell(
                    value = toPersianDigits(user.inviteCount),
                    label = stringResource(Res.string.account_user_detail_stat_invites),
                    icon = painterResource(Res.drawable.ic_gift),
                    modifier = Modifier.weight(1f),
                )
                AccountUserStatCell(
                    value = stringResource(
                        Res.string.account_user_detail_stat_credit_value,
                        toPersianDigits(user.creditDays),
                    ),
                    label = stringResource(Res.string.account_user_detail_stat_credit),
                    icon = painterResource(Res.drawable.ic_sparkles),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AccountUserStatCell(
    value: String,
    label: String,
    icon: Painter,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(VitranRadius.medium))
            .background(ShopPurpleTint)
            .padding(vertical = VitranSpacing.md, horizontal = VitranSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        VitranIcon(
            painter = icon,
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = ShopPurple,
        )
        VitranText(
            text = value,
            style = VitranTextStyle.Title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        VitranText(
            text = label,
            style = VitranTextStyle.Label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun AccountUserEventsCard(events: List<AccountUserEvent>) {
    AccountCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            VitranText(
                text = stringResource(Res.string.account_user_detail_events_title),
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            events.forEach { event ->
                AccountUserEventRow(event = event)
            }
        }
    }
}

@Composable
private fun AccountUserEventRow(event: AccountUserEvent) {
    val (title, dot) = when (event.kind) {
        AccountUserEventKind.Verified ->
            stringResource(Res.string.account_user_detail_event_verified) to EventVerified
        AccountUserEventKind.RoleUpdated ->
            stringResource(Res.string.account_user_detail_event_role) to ShopPurple
        AccountUserEventKind.Created ->
            stringResource(Res.string.account_user_detail_event_created) to EventCreated
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(AccountTokens.StatusDot)
                .clip(CircleShape)
                .background(dot),
        )
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs)) {
            VitranText(
                text = title,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurface,
            )
            VitranText(
                text = toPersianDigits(event.timestamp),
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
