package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_referral_banner_body
import vitranshop.shared.generated.resources.account_referral_banner_code
import vitranshop.shared.generated.resources.account_referral_banner_copy_a11y
import vitranshop.shared.generated.resources.account_referral_banner_cta
import vitranshop.shared.generated.resources.account_referral_banner_illustration_a11y
import vitranshop.shared.generated.resources.account_referral_banner_stat_credit
import vitranshop.shared.generated.resources.account_referral_banner_stat_pending
import vitranshop.shared.generated.resources.account_referral_banner_stat_rewarded
import vitranshop.shared.generated.resources.account_referral_banner_title
import vitranshop.shared.generated.resources.account_referral_copied
import vitranshop.shared.generated.resources.account_referral_view
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_clock
import vitranshop.shared.generated.resources.ic_copy
import vitranshop.shared.generated.resources.ic_gift
import vitranshop.shared.generated.resources.ic_people
import vitranshop.shared.generated.resources.ic_wallet
import vitranshop.shared.generated.resources.ill_referral_gift

private val TitlePurple = Color(0xFF3D2AE0)
private val CodePillBg = Color(0xFFEEEAFF)
private val StatsBorder = Color(0xFFE8E4F5)

/**
 * Account hub referral card — matches the invite design mock
 * (illustration + copy column, bordered stats, code pill below stats).
 */
@Composable
internal fun AccountReferralBanner(
    profile: ReferralProfile,
    onInviteClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCopyCode: (String) -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val shape = RoundedCornerShape(AccountTokens.CardRadius)
    var copied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = AccountTokens.CardElevation,
                shape = shape,
                clip = false,
                ambientColor = AccountTokens.CardShadow,
                spotColor = AccountTokens.CardShadow,
            )
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, AccountTokens.CardBorder, shape)
            .padding(VitranSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ReferralInviteCopy(
                    onInviteClick = onInviteClick,
                    onHistoryClick = onHistoryClick,
                    isRtl = isRtl,
                    modifier = Modifier.weight(1f),
                )
                ReferralIllustration(
                    modifier = Modifier
                        .width(AccountTokens.ReferralIllustrationWidth)
                        .height(AccountTokens.ReferralIllustrationHeight),
                )
            }
        } else {
            ReferralIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            )
            ReferralInviteCopy(
                onInviteClick = onInviteClick,
                onHistoryClick = onHistoryClick,
                isRtl = isRtl,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        ReferralStatsBar(stats = profile.stats)
        ReferralCodePill(
            code = profile.code,
            copied = copied,
            onCopy = {
                onCopyCode(profile.code)
                copied = true
            },
        )
    }
}

@Composable
private fun ReferralIllustration(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.ill_referral_gift),
        contentDescription = stringResource(Res.string.account_referral_banner_illustration_a11y),
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}

@Composable
private fun ReferralInviteCopy(
    onInviteClick: () -> Unit,
    onHistoryClick: () -> Unit,
    isRtl: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_gift),
                    contentDescription = null,
                    size = 18.dp,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(
                text = stringResource(Res.string.account_referral_banner_title),
                color = TitlePurple,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                ),
            )
        }
        VitranText(
            text = stringResource(Res.string.account_referral_banner_body),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ReferralCtaButton(
            label = stringResource(Res.string.account_referral_banner_cta),
            onClick = onInviteClick,
            isRtl = isRtl,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(role = Role.Button, onClick = onHistoryClick)
                .padding(vertical = VitranSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(
                text = stringResource(Res.string.account_referral_view),
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.primary,
            )
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = null,
                size = 14.dp,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            )
        }
    }
}

@Composable
private fun ReferralCtaButton(
    label: String,
    onClick: () -> Unit,
    isRtl: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(VitranRadius.medium)
    Row(
        modifier = modifier
            .height(VitranSize.buttonHeight)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(start = VitranSpacing.sm)
                .graphicsLayer { scaleX = if (isRtl) -1f else 1f },
        )
    }
}

@Composable
private fun ReferralStatsBar(stats: ReferralStats) {
    val shape = RoundedCornerShape(VitranRadius.medium)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StatsBorder, shape)
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReferralStatCell(
            painter = painterResource(Res.drawable.ic_people),
            text = stringResource(
                Res.string.account_referral_banner_stat_rewarded,
                toPersianDigits(stats.rewarded),
            ),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(28.dp)
                .background(StatsBorder),
        )
        ReferralStatCell(
            painter = painterResource(Res.drawable.ic_clock),
            text = stringResource(Res.string.account_referral_banner_stat_pending),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(28.dp)
                .background(StatsBorder),
        )
        ReferralStatCell(
            painter = painterResource(Res.drawable.ic_wallet),
            text = stringResource(
                Res.string.account_referral_banner_stat_credit,
                formatPersianAmount(stats.availableCreditToman),
            ),
            modifier = Modifier.weight(1.2f),
        )
    }
}

@Composable
private fun ReferralStatCell(
    painter: Painter,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm, Alignment.CenterHorizontally),
    ) {
        VitranIcon(
            painter = painter,
            contentDescription = null,
            size = 18.dp,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun ReferralCodePill(
    code: String,
    copied: Boolean,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .clip(shape)
            .background(CodePillBg, shape)
            .clickable(role = Role.Button, onClick = onCopy)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = if (copied) {
                stringResource(Res.string.account_referral_copied)
            } else {
                stringResource(Res.string.account_referral_banner_code, code)
            },
            color = TitlePurple,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            ),
            textAlign = TextAlign.Center,
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_copy),
            contentDescription = stringResource(Res.string.account_referral_banner_copy_a11y),
            size = 16.dp,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

/** Formats 200000 → «۲۰۰٬۰۰۰». */
internal fun formatPersianAmount(amount: Int): String {
    val raw = amount.coerceAtLeast(0).toString()
    val grouped = buildString {
        val offset = raw.length % 3
        if (offset > 0) {
            append(raw.substring(0, offset))
            if (raw.length > offset) append('٬')
        }
        var i = offset
        while (i < raw.length) {
            append(raw.substring(i, i + 3))
            i += 3
            if (i < raw.length) append('٬')
        }
    }
    return toPersianDigits(grouped)
}
