package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
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
import vitranshop.shared.generated.resources.account_edit_profile
import vitranshop.shared.generated.resources.account_open_profile_a11y
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_edit
import vitranshop.shared.generated.resources.ic_nav_profile

private val AvatarSoftBg = Color(0xFFEEEAFF)
private val EditPillBg = Color(0xFFEEEAFF)
private val ChevronBtnBg = Color(0xFFF5F2FF)
private val ChevronBtnBorder = Color(0xFFE0D9F5)

/**
 * Account hub identity card — avatar + name/email + soft edit pill + chevron control.
 */
@Composable
internal fun AccountHubHeader(
    profile: AccountProfile,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    AccountCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(Res.string.account_open_profile_a11y),
                    onClick = onEditClick,
                )
                .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(AccountTokens.AvatarHubHeader)
                    .clip(CircleShape)
                    .background(AvatarSoftBg),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_nav_profile),
                    contentDescription = null,
                    size = VitranSize.iconLarge,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = profile.displayName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                    ),
                    maxLines = 1,
                )
                VitranText(
                    text = profile.email,
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
                EditProfilePill(onClick = onEditClick)
            }
            ChevronSquareButton(
                isRtl = isRtl,
                onClick = onEditClick,
            )
        }
    }
}

@Composable
private fun EditProfilePill(onClick: () -> Unit) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(EditPillBg, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_edit),
            contentDescription = null,
            size = 14.dp,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(Res.string.account_edit_profile),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            ),
        )
    }
}

@Composable
private fun ChevronSquareButton(
    isRtl: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(VitranRadius.medium)
    Box(
        modifier = Modifier
            .size(AccountTokens.HubChevronBtn)
            .clip(shape)
            .background(ChevronBtnBg, shape)
            .border(1.dp, ChevronBtnBorder, shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = stringResource(Res.string.account_open_profile_a11y),
            size = VitranSize.iconSmall,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
        )
    }
}
