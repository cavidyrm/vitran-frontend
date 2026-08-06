package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_close
import vitranshop.shared.generated.resources.product_detail_description
import vitranshop.shared.generated.resources.product_detail_description_close_a11y

/**
 * shop.app Description side sheet — opened from “View more” / بیشتر.
 * Shell animation lives in [ProductDetailSideSheet].
 */
@Composable
fun ProductDetailDescriptionSheet(
    description: String,
    onDismiss: () -> Unit,
) {
    val title = stringResource(Res.string.product_detail_description)
    val closeA11y = stringResource(Res.string.product_detail_description_close_a11y)

    ProductDetailSideSheet(onDismiss = onDismiss) { onClose ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SheetContentPad),
        ) {
            Box(
                modifier = Modifier
                    .size(CloseButtonSize)
                    .clip(CircleShape)
                    .background(CloseButtonFill)
                    .clickable(role = Role.Button, onClick = onClose)
                    .semantics { contentDescription = closeA11y },
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = null,
                    size = CloseGlyphSize,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(modifier = Modifier.height(VitranSpacing.md))

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            )
        }
    }
}

private val SheetContentPad = 24.dp
private val CloseButtonSize = 44.dp
private val CloseGlyphSize = 20.dp
private val CloseButtonFill = Color(0xFFF2F4F5)
