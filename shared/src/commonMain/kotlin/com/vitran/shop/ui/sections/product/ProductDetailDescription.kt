package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.product_detail_description
import vitranshop.shared.generated.resources.product_detail_view_more

/**
 * PDP description block (shop.app Description + View more → side sheet).
 * Collapsed preview stays on-page; “بیشتر” opens [ProductDetailDescriptionSheet].
 */
@Composable
fun ProductDetailDescription(
    description: String,
    modifier: Modifier = Modifier,
) {
    var sheetOpen by remember { mutableStateOf(false) }
    val title = stringResource(Res.string.product_detail_description)
    val viewMore = stringResource(Res.string.product_detail_view_more)

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            ),
            modifier = Modifier.padding(bottom = VitranSpacing.lg),
        )
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp,
            ),
            maxLines = CollapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = viewMore,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            ),
            modifier = Modifier
                .padding(top = VitranSpacing.sm)
                .clickable(role = Role.Button, onClick = { sheetOpen = true }),
        )
    }

    if (sheetOpen) {
        ProductDetailDescriptionSheet(
            description = description,
            onDismiss = { sheetOpen = false },
        )
    }
}

private const val CollapsedMaxLines = 4
