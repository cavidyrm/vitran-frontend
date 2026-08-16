package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.ShopPurpleDark
import com.vitran.shop.ui.theme.ShopPurpleSoft
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranShapes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_empty_referral_cta
import vitranshop.shared.generated.resources.account_referral_credit_active
import vitranshop.shared.generated.resources.account_referral_credit_plan
import vitranshop.shared.generated.resources.account_referral_credit_remaining
import vitranshop.shared.generated.resources.account_referral_credits
import vitranshop.shared.generated.resources.account_referral_empty
import vitranshop.shared.generated.resources.account_referral_filter_all
import vitranshop.shared.generated.resources.account_referral_filter_pending
import vitranshop.shared.generated.resources.account_referral_filter_successful
import vitranshop.shared.generated.resources.account_referral_invite_avatar_a11y
import vitranshop.shared.generated.resources.account_referral_invited_at
import vitranshop.shared.generated.resources.account_referral_invites
import vitranshop.shared.generated.resources.account_referral_rewarded_at
import vitranshop.shared.generated.resources.account_referral_signed_up
import vitranshop.shared.generated.resources.account_referral_status_pending
import vitranshop.shared.generated.resources.account_referral_status_successful
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_gift
import vitranshop.shared.generated.resources.ic_nav_profile
import vitranshop.shared.generated.resources.ic_wallet

private enum class InviteFilter { All, Successful, Pending }

@Composable
internal fun ProfileReferralHistory(
    referral: ReferralProfile,
    onInviteClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var filter by remember { mutableStateOf(InviteFilter.All) }
    val all = referral.successful + referral.pending
    val visible = when (filter) {
        InviteFilter.All -> all
        InviteFilter.Successful -> referral.successful
        InviteFilter.Pending -> referral.pending
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AccountTokens.SectionGap),
    ) {
        AccountCard {
            Column(modifier = Modifier.padding(bottom = VitranSpacing.sm)) {
                VitranText(
                    text = stringResource(Res.string.account_referral_invites),
                    style = VitranTextStyle.Title,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = VitranSpacing.lg,
                        end = VitranSpacing.lg,
                        top = VitranSpacing.lg,
                        bottom = VitranSpacing.md,
                    ),
                )
                Row(
                    modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    FilterChip(
                        label = stringResource(Res.string.account_referral_filter_all),
                        selected = filter == InviteFilter.All,
                        onClick = { filter = InviteFilter.All },
                    )
                    FilterChip(
                        label = stringResource(Res.string.account_referral_filter_successful),
                        selected = filter == InviteFilter.Successful,
                        onClick = { filter = InviteFilter.Successful },
                    )
                    FilterChip(
                        label = stringResource(Res.string.account_referral_filter_pending),
                        selected = filter == InviteFilter.Pending,
                        onClick = { filter = InviteFilter.Pending },
                    )
                }
                if (visible.isEmpty()) {
                    Column(
                        modifier = Modifier.padding(VitranSpacing.lg),
                        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                    ) {
                        VitranText(
                            text = stringResource(Res.string.account_referral_empty),
                            style = VitranTextStyle.Body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        AccountPrimaryButton(
                            label = stringResource(Res.string.account_empty_referral_cta),
                            onClick = onInviteClick,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    visible.forEachIndexed { index, entry ->
                        InviteRow(
                            entry = entry,
                            showDivider = index != visible.lastIndex,
                        )
                    }
                }
            }
        }
        referral.credits.firstOrNull()?.let { credit ->
            SellerCreditCard(credit = credit)
        }
    }
}

@Composable
private fun InviteRow(
    entry: ReferralEntry,
    showDivider: Boolean,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val statusLabel = when (entry.status) {
        ReferralStatus.Successful -> stringResource(Res.string.account_referral_status_successful)
        ReferralStatus.Pending -> stringResource(Res.string.account_referral_status_pending)
    }
    val statusBg = when (entry.status) {
        ReferralStatus.Successful -> AccountTokens.ReferralSuccessSoft
        ReferralStatus.Pending -> AccountTokens.ReferralPendingSoft
    }
    val statusFg = when (entry.status) {
        ReferralStatus.Successful -> AccountTokens.Success
        ReferralStatus.Pending -> AccountTokens.ReferralPending
    }
    val dateLine = when (entry.status) {
        ReferralStatus.Successful -> stringResource(
            Res.string.account_referral_signed_up,
            formatIsoDate(entry.signedUpAt),
        )
        ReferralStatus.Pending -> stringResource(
            Res.string.account_referral_invited_at,
            formatIsoDate(entry.signedUpAt),
        )
    }
    val reward = entry.rewardedAt?.let {
        stringResource(Res.string.account_referral_rewarded_at, formatIsoDate(it))
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(AccountTokens.ShortcutThumb)
                    .clip(CircleShape)
                    .background(AccountTokens.ReferralStatIconBg),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_nav_profile),
                    contentDescription = stringResource(Res.string.account_referral_invite_avatar_a11y),
                    size = VitranSize.iconMedium,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                VitranText(
                    // LTR isolate so masked mobile digits keep logical order in RTL.
                    text = "\u2066${toPersianDigits(entry.phoneMasked)}\u2069",
                    style = VitranTextStyle.Title,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                VitranText(
                    text = dateLine,
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (reward != null) {
                    VitranText(
                        text = reward,
                        style = VitranTextStyle.Body,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            StatusBadge(label = statusLabel, background = statusBg, foreground = statusFg)
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                thickness = VitranSize.borderHairline,
                color = AccountTokens.FieldDivider,
            )
        }
    }
}

@Composable
private fun StatusBadge(
    label: String,
    background: Color,
    foreground: Color,
) {
    val shape = VitranShapes.pill
    Box(
        modifier = Modifier
            .clip(shape)
            .background(background, shape)
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        VitranText(
            text = label,
            style = VitranTextStyle.Label,
            color = foreground,
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = VitranShapes.pill
    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape,
            )
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else AccountTokens.ChipBorder,
                shape = shape,
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        VitranText(
            text = label,
            style = VitranTextStyle.Label,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}

@Composable
private fun SellerCreditCard(credit: ReferralCredit) {
    val progress = if (credit.totalDays > 0) {
        (credit.durationDays.toFloat() / credit.totalDays.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    AccountCard {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.lg),
        ) {
            val sideBySide = maxWidth >= 480.dp
            if (sideBySide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SellerCreditCopy(
                        credit = credit,
                        progress = progress,
                        modifier = Modifier.weight(1f),
                    )
                    SellerPlanBadge(planTitle = credit.planTitle)
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                ) {
                    SellerCreditCopy(credit = credit, progress = progress)
                    SellerPlanBadge(
                        planTitle = credit.planTitle,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
        }
    }
}

@Composable
private fun SellerCreditCopy(
    credit: ReferralCredit,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(AccountTokens.SectionIconTile)
                    .clip(RoundedCornerShape(VitranRadius.medium))
                    .background(AccountTokens.ReferralStatIconBg),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_wallet),
                    contentDescription = null,
                    size = VitranSize.iconMedium,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            VitranText(
                text = stringResource(Res.string.account_referral_credits),
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            VitranText(
                text = stringResource(Res.string.account_referral_credit_plan, credit.planTitle),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (credit.isActive) {
                StatusBadge(
                    label = stringResource(Res.string.account_referral_credit_active),
                    background = AccountTokens.ReferralSuccessSoft,
                    foreground = AccountTokens.Success,
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AccountTokens.ReferralProgressHeight)
                    .clip(VitranShapes.pill)
                    .background(AccountTokens.FieldDivider),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(AccountTokens.ReferralProgressHeight)
                        .clip(VitranShapes.pill)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
            VitranText(
                text = stringResource(
                    Res.string.account_referral_credit_remaining,
                    toPersianDigits(credit.durationDays),
                ),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SellerPlanBadge(
    planTitle: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(VitranRadius.large)
    Box(
        modifier = modifier
            .width(AccountTokens.ReferralPlanCardWidth)
            .height(AccountTokens.ReferralPlanCardHeight)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ShopPurple, ShopPurpleDark),
                ),
                shape = shape,
            )
            .padding(VitranSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ShopPurpleSoft.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_gift),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            VitranText(
                text = planTitle,
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
