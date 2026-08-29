package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_logo_a11y
import vitranshop.shared.generated.resources.ic_bell
import vitranshop.shared.generated.resources.ic_chevron_down
import vitranshop.shared.generated.resources.ic_nav_home
import vitranshop.shared.generated.resources.ic_shop_logo
import vitranshop.shared.generated.resources.store_plan_home_a11y
import vitranshop.shared.generated.resources.store_plan_notifications_a11y
import vitranshop.shared.generated.resources.store_plan_store_picker_a11y

/**
 * Merchant chrome for store-plan screens — logo + notifications + store chip.
 * Matches `docs/ui-reference/admin/store-plan/`.
 */
@Composable
fun MerchantPlanTopBar(
    storeName: String,
    onHomeClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    onStoreClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(StorePlanTokens.TopBarHeight)
            .background(MaterialTheme.colorScheme.surface)
            .border(width = 1.dp, color = StorePlanTokens.CardBorder)
            .padding(horizontal = VitranSpacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
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
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(VitranSize.touchTarget)
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .clickable(role = Role.Button, onClick = onNotificationsClick),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_bell),
                    contentDescription = stringResource(Res.string.store_plan_notifications_a11y),
                    size = VitranSize.iconMedium,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box(
                modifier = Modifier
                    .size(VitranSize.touchTarget)
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .clickable(role = Role.Button, onClick = onHomeClick),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_nav_home),
                    contentDescription = stringResource(Res.string.store_plan_home_a11y),
                    size = VitranSize.iconMedium,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val chipShape = RoundedCornerShape(VitranRadius.medium)
            Row(
                modifier = Modifier
                    .clip(chipShape)
                    .border(1.dp, StorePlanTokens.CardBorder, chipShape)
                    .clickable(role = Role.Button, onClick = onStoreClick)
                    .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_nav_home),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = AdminTokens.Brand,
                )
                Text(
                    text = storeName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    ),
                )
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_chevron_down),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
