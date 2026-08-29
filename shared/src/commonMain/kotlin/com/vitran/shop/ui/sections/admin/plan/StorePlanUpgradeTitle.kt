package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.store_plan_upgrade_subtitle
import vitranshop.shared.generated.resources.store_plan_upgrade_title

@Composable
fun StorePlanUpgradeTitle(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.store_plan_upgrade_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
            ),
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.store_plan_upgrade_subtitle),
            color = AdminTokens.Helper,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
            textAlign = TextAlign.Center,
        )
    }
}
