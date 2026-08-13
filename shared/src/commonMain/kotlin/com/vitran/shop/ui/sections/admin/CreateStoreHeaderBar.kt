package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_autosave_saved
import vitranshop.shared.generated.resources.admin_autosave_saving
import vitranshop.shared.generated.resources.admin_create_store_back_a11y
import vitranshop.shared.generated.resources.admin_create_store_subtitle
import vitranshop.shared.generated.resources.admin_create_store_title
import vitranshop.shared.generated.resources.ic_chevron_right

@Composable
fun CreateStoreHeaderBar(
    autosaveStatus: AutosaveStatus,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(AdminTokens.HeaderHeight)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = VitranSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(VitranSize.touchTarget)
                .clip(RoundedCornerShape(VitranRadius.small))
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = stringResource(Res.string.admin_create_store_back_a11y),
                size = VitranSize.iconMedium,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.admin_create_store_title),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                ),
            )
            Text(
                text = stringResource(Res.string.admin_create_store_subtitle),
                color = AdminTokens.Helper,
                fontSize = 13.sp,
            )
        }
        when (autosaveStatus) {
            AutosaveStatus.Idle -> Unit
            AutosaveStatus.Saving -> Text(
                text = stringResource(Res.string.admin_autosave_saving),
                color = AdminTokens.Helper,
                fontSize = 12.sp,
            )
            AutosaveStatus.Saved -> Text(
                text = stringResource(Res.string.admin_autosave_saved),
                color = AdminTokens.Success,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
        }
    }
}
