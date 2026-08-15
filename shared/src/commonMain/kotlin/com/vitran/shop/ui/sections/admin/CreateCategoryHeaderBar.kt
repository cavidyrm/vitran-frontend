package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import vitranshop.shared.generated.resources.admin_create_category_back_a11y
import vitranshop.shared.generated.resources.admin_create_category_breadcrumb
import vitranshop.shared.generated.resources.admin_create_category_subtitle
import vitranshop.shared.generated.resources.admin_create_category_title
import vitranshop.shared.generated.resources.admin_create_product_store
import vitranshop.shared.generated.resources.ic_chevron_right

@Composable
fun CreateCategoryHeaderBar(
    storeName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
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
                contentDescription = stringResource(Res.string.admin_create_category_back_a11y),
                size = VitranSize.iconMedium,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            Text(
                text = stringResource(Res.string.admin_create_category_breadcrumb),
                color = AdminTokens.Helper,
                fontSize = 12.sp,
            )
            Text(
                text = stringResource(Res.string.admin_create_category_title),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                ),
            )
            Text(
                text = stringResource(Res.string.admin_create_category_subtitle),
                color = AdminTokens.Helper,
                fontSize = 13.sp,
            )
            Text(
                text = stringResource(Res.string.admin_create_product_store, storeName),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            )
        }
    }
}
