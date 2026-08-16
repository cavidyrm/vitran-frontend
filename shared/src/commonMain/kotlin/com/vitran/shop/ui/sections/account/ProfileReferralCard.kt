package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_referral_banner_illustration_a11y
import vitranshop.shared.generated.resources.account_referral_code
import vitranshop.shared.generated.resources.account_referral_copied
import vitranshop.shared.generated.resources.account_referral_copy
import vitranshop.shared.generated.resources.account_referral_copy_code
import vitranshop.shared.generated.resources.account_referral_link
import vitranshop.shared.generated.resources.account_referral_rules_body
import vitranshop.shared.generated.resources.account_referral_rules_link
import vitranshop.shared.generated.resources.account_referral_rules_title
import vitranshop.shared.generated.resources.account_referral_share
import vitranshop.shared.generated.resources.account_referral_share_copied
import vitranshop.shared.generated.resources.account_referral_stat_credits_title
import vitranshop.shared.generated.resources.account_referral_stat_credits_value
import vitranshop.shared.generated.resources.account_referral_stat_pending
import vitranshop.shared.generated.resources.account_referral_stat_reward_hint
import vitranshop.shared.generated.resources.account_referral_stat_rewarded
import vitranshop.shared.generated.resources.account_referral_stat_total
import vitranshop.shared.generated.resources.account_referral_subtitle
import vitranshop.shared.generated.resources.account_referral_title
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_clock
import vitranshop.shared.generated.resources.ic_copy
import vitranshop.shared.generated.resources.ic_gift
import vitranshop.shared.generated.resources.ic_people
import vitranshop.shared.generated.resources.ic_share
import vitranshop.shared.generated.resources.ill_referral_gift_box

@Composable
internal fun ProfileReferralCard(
    referral: ReferralProfile,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    var copiedCode by remember { mutableStateOf(false) }
    var copiedLink by remember { mutableStateOf(false) }
    var shared by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AccountTokens.SectionGap),
    ) {
        ReferralHeroCard(
            referral = referral,
            copiedCode = copiedCode,
            copiedLink = copiedLink,
            shared = shared,
            onCopyCode = {
                clipboard.setText(AnnotatedString(referral.code))
                copiedCode = true
                copiedLink = false
                shared = false
            },
            onCopyLink = {
                clipboard.setText(AnnotatedString(referral.inviteUrl))
                copiedLink = true
                copiedCode = false
                shared = false
            },
            onShare = {
                clipboard.setText(AnnotatedString(referral.inviteUrl))
                shared = true
                copiedCode = false
                copiedLink = false
            },
        )
        ReferralStatsGrid(stats = referral.stats)
        ReferralHowItWorksCard()
    }
}

@Composable
private fun ReferralHeroCard(
    referral: ReferralProfile,
    copiedCode: Boolean,
    copiedLink: Boolean,
    shared: Boolean,
    onCopyCode: () -> Unit,
    onCopyLink: () -> Unit,
    onShare: () -> Unit,
) {
    val shape = RoundedCornerShape(AccountTokens.CardRadius)
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = AccountTokens.CardElevation,
                shape = shape,
                clip = false,
                ambientColor = AccountTokens.CardShadow,
                spotColor = AccountTokens.CardShadow,
            )
            .clip(shape)
            .background(AccountTokens.ReferralHeroBg, shape)
            .border(1.dp, AccountTokens.CardBorder, shape)
            .padding(VitranSpacing.xl),
    ) {
        val sideBySide = maxWidth >= 560.dp
        if (sideBySide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xl),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ReferralHeroCopy(
                    referral = referral,
                    copiedCode = copiedCode,
                    copiedLink = copiedLink,
                    shared = shared,
                    onCopyCode = onCopyCode,
                    onCopyLink = onCopyLink,
                    onShare = onShare,
                    modifier = Modifier.weight(1f),
                )
                ReferralHeroIllustration()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ReferralHeroIllustration()
                ReferralHeroCopy(
                    referral = referral,
                    copiedCode = copiedCode,
                    copiedLink = copiedLink,
                    shared = shared,
                    onCopyCode = onCopyCode,
                    onCopyLink = onCopyLink,
                    onShare = onShare,
                )
            }
        }
    }
}

@Composable
private fun ReferralHeroCopy(
    referral: ReferralProfile,
    copiedCode: Boolean,
    copiedLink: Boolean,
    shared: Boolean,
    onCopyCode: () -> Unit,
    onCopyLink: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        VitranText(
            text = stringResource(Res.string.account_referral_title),
            style = VitranTextStyle.Headline,
            color = MaterialTheme.colorScheme.onSurface,
        )
        VitranText(
            text = stringResource(Res.string.account_referral_subtitle),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ReferralCopyField(
            label = stringResource(Res.string.account_referral_code),
            value = referral.code,
            onCopy = onCopyCode,
            copyLabel = if (copiedCode) {
                stringResource(Res.string.account_referral_copied)
            } else {
                stringResource(Res.string.account_referral_copy_code)
            },
        )
        ReferralCopyField(
            label = stringResource(Res.string.account_referral_link),
            value = referral.inviteUrl,
            onCopy = onCopyLink,
            copyLabel = if (copiedLink) {
                stringResource(Res.string.account_referral_copied)
            } else {
                stringResource(Res.string.account_referral_copy)
            },
            mono = true,
        )
        if (shared) {
            VitranText(
                text = stringResource(Res.string.account_referral_share_copied),
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AccountPrimaryButton(
                label = stringResource(Res.string.account_referral_share),
                onClick = onShare,
                icon = painterResource(Res.drawable.ic_share),
                modifier = Modifier.weight(1f),
            )
            AccountOutlinedButton(
                label = if (copiedLink) {
                    stringResource(Res.string.account_referral_copied)
                } else {
                    stringResource(Res.string.account_referral_copy)
                },
                onClick = onCopyLink,
                icon = painterResource(Res.drawable.ic_copy),
                borderColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ReferralCopyField(
    label: String,
    value: String,
    onCopy: () -> Unit,
    copyLabel: String,
    mono: Boolean = false,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    Column(
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        VitranText(
            text = label,
            style = VitranTextStyle.Label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AccountTokens.StackedFieldHeight)
                .clip(shape)
                .border(1.dp, AccountTokens.CardBorder, shape)
                .background(MaterialTheme.colorScheme.surface, shape)
                .padding(horizontal = VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = if (mono) 14.sp else 16.sp,
                    fontWeight = if (mono) FontWeight.Normal else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .clickable(role = Role.Button, onClick = onCopy)
                    .padding(VitranSpacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_copy),
                    contentDescription = copyLabel,
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ReferralHeroIllustration() {
    Image(
        painter = painterResource(Res.drawable.ill_referral_gift_box),
        contentDescription = stringResource(Res.string.account_referral_banner_illustration_a11y),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .width(AccountTokens.ReferralHeroIllustrationWidth)
            .height(AccountTokens.ReferralHeroIllustration),
    )
}

@Composable
private fun ReferralStatsGrid(stats: ReferralStats) {
    val items = listOf(
        ReferralStatUi(
            value = toPersianDigits(stats.total),
            label = stringResource(Res.string.account_referral_stat_total),
            icon = painterResource(Res.drawable.ic_people),
            iconTint = MaterialTheme.colorScheme.primary,
            iconBg = AccountTokens.ReferralStatIconBg,
        ),
        ReferralStatUi(
            value = toPersianDigits(stats.rewarded),
            label = stringResource(Res.string.account_referral_stat_rewarded),
            icon = painterResource(Res.drawable.ic_check),
            iconTint = AccountTokens.Success,
            iconBg = AccountTokens.ReferralSuccessSoft,
        ),
        ReferralStatUi(
            value = toPersianDigits(stats.pending),
            label = stringResource(Res.string.account_referral_stat_pending),
            icon = painterResource(Res.drawable.ic_clock),
            iconTint = AccountTokens.ReferralPending,
            iconBg = AccountTokens.ReferralPendingSoft,
        ),
        ReferralStatUi(
            value = stringResource(
                Res.string.account_referral_stat_credits_value,
                toPersianDigits(stats.remainingSellerDays),
            ),
            label = stringResource(Res.string.account_referral_stat_credits_title),
            hint = stringResource(Res.string.account_referral_stat_reward_hint),
            icon = painterResource(Res.drawable.ic_gift),
            iconTint = MaterialTheme.colorScheme.primary,
            iconBg = AccountTokens.ReferralStatIconBg,
        ),
    )
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val columns = when {
            maxWidth < 360.dp -> 1
            maxWidth < 560.dp -> 2
            else -> 4
        }
        if (columns == 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            ) {
                items.forEach { item ->
                    ReferralStatCard(item = item, modifier = Modifier.weight(1f))
                }
            }
        } else if (columns == 2) {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                    items.take(2).forEach { item ->
                        ReferralStatCard(item = item, modifier = Modifier.weight(1f))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                    items.drop(2).forEach { item ->
                        ReferralStatCard(item = item, modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                items.forEach { item ->
                    ReferralStatCard(item = item, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

private data class ReferralStatUi(
    val value: String,
    val label: String,
    val icon: Painter,
    val iconTint: Color,
    val iconBg: Color,
    val hint: String? = null,
)

@Composable
private fun ReferralStatCard(
    item: ReferralStatUi,
    modifier: Modifier = Modifier,
) {
    AccountCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(AccountTokens.ReferralStatIcon)
                    .clip(CircleShape)
                    .background(item.iconBg),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = item.icon,
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = item.iconTint,
                )
            }
            VitranText(
                text = item.value,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            VitranText(
                text = item.label,
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
            if (item.hint != null) {
                VitranText(
                    text = item.hint,
                    style = VitranTextStyle.Label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun ReferralHowItWorksCard() {
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
                    ReferralHowItWorksCopy(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(Res.drawable.ill_referral_gift_box),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(AccountTokens.ReferralRulesIllustration),
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ReferralHowItWorksCopy()
                    Image(
                        painter = painterResource(Res.drawable.ill_referral_gift_box),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(AccountTokens.ReferralRulesIllustration),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReferralHowItWorksCopy(modifier: Modifier = Modifier) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        VitranText(
            text = stringResource(Res.string.account_referral_rules_title),
            style = VitranTextStyle.Title,
            color = MaterialTheme.colorScheme.onSurface,
        )
        VitranText(
            text = stringResource(Res.string.account_referral_rules_body),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            modifier = Modifier.clickable(role = Role.Button, onClick = { /* mock */ }),
        ) {
            VitranText(
                text = stringResource(Res.string.account_referral_rules_link),
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.primary,
            )
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            )
        }
    }
}
