package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
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
import vitranshop.shared.generated.resources.ic_arrow_right
import vitranshop.shared.generated.resources.ic_chart
import vitranshop.shared.generated.resources.ic_star_outline
import vitranshop.shared.generated.resources.store_plan_promo_body
import vitranshop.shared.generated.resources.store_plan_promo_cta
import vitranshop.shared.generated.resources.store_plan_promo_title

@Composable
fun StorePlanPromoBanner(
    onViewPlansClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(StorePlanTokens.CardRadius)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(StorePlanTokens.CtaBannerBg, shape)
            .padding(VitranSpacing.xl),
    ) {
        val desktop = maxWidth >= VitranSize.mdBreakpoint
        if (desktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xl),
            ) {
                PromoArt()
                PromoCopy(modifier = Modifier.weight(1f))
                ViewPlansButton(onClick = onViewPlansClick, isRtl = isRtl)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                PromoArt()
                PromoCopy()
                ViewPlansButton(
                    onClick = onViewPlansClick,
                    isRtl = isRtl,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun PromoArt() {
    Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(VitranRadius.medium))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chart),
                contentDescription = null,
                size = VitranSize.iconLarge,
                tint = AdminTokens.Brand,
            )
        }
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(VitranRadius.medium))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_star_outline),
                contentDescription = null,
                size = VitranSize.iconLarge,
                tint = AdminTokens.Brand,
            )
        }
    }
}

@Composable
private fun PromoCopy(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Text(
            text = stringResource(Res.string.store_plan_promo_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            ),
        )
        Text(
            text = stringResource(Res.string.store_plan_promo_body),
            color = AdminTokens.Helper,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
        )
    }
}

@Composable
private fun ViewPlansButton(
    onClick: () -> Unit,
    isRtl: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(VitranRadius.medium)
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(AdminTokens.Brand, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.store_plan_promo_cta),
            color = AdminTokens.OnBrand,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            ),
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = AdminTokens.OnBrand,
            modifier = Modifier
                .padding(start = VitranSpacing.sm)
                .graphicsLayer { scaleX = if (isRtl) -1f else 1f },
        )
    }
}
