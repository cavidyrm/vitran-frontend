package com.vitran.shop.ui.sections.admin.plan

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_plans_help_a11y
import vitranshop.shared.generated.resources.admin_plans_notifications_a11y
import vitranshop.shared.generated.resources.admin_plans_role
import vitranshop.shared.generated.resources.auth_logo_a11y
import vitranshop.shared.generated.resources.ic_bell
import vitranshop.shared.generated.resources.ic_help
import vitranshop.shared.generated.resources.ic_shop_logo

/**
 * Platform-admin chrome for plan catalog management.
 * Matches `docs/ui-reference/admin/store-plan/admin-plans-desktop.png`.
 */
@Composable
fun AdminPlansTopBar(
    adminName: String,
    onHomeClick: () -> Unit,
    onHelpClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(StorePlanTokens.TopBarHeight)
            .background(MaterialTheme.colorScheme.surface)
            .border(width = 1.dp, color = StorePlanTokens.CardBorder)
            .padding(horizontal = if (compact) VitranSpacing.lg else VitranSpacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .clickable(role = Role.Button, onClick = onHomeClick)
                .padding(horizontal = VitranSpacing.xs, vertical = VitranSpacing.xs)
                .weight(1f, fill = false),
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(Res.drawable.ic_shop_logo),
                contentDescription = stringResource(Res.string.auth_logo_a11y),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = stringResource(Res.string.auth_logo_a11y),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) VitranSpacing.sm else VitranSpacing.md),
        ) {
            if (!compact) {
                IconChip(
                    onClick = onHelpClick,
                    contentDescription = stringResource(Res.string.admin_plans_help_a11y),
                ) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_help),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            IconChip(
                onClick = onNotificationsClick,
                contentDescription = stringResource(Res.string.admin_plans_notifications_a11y),
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_bell),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                if (!compact) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = adminName,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = stringResource(Res.string.admin_plans_role),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AdminPlansTokens.SoftPurple)
                        .semantics {
                            contentDescription = adminName
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = adminName.take(1),
                        color = AdminPlansTokens.PopularBorder,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}

@Composable
private fun IconChip(
    onClick: () -> Unit,
    contentDescription: String,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(1.dp, StorePlanTokens.CardBorder, CircleShape)
            .semantics { this.contentDescription = contentDescription }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
