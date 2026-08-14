package com.vitran.shop.ui.components.admin

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_check

@Composable
fun AdminToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(role = Role.Button) { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            ),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = if (checked) "فعال" else "غیرفعال",
            color = AdminTokens.Helper,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
        )
        AdminToggle(checked = checked)
    }
}

@Composable
fun AdminCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier
            .size(VitranSize.touchTarget)
            .clip(RoundedCornerShape(VitranRadius.small))
            .clickable(role = Role.Button, onClick = { onCheckedChange(!checked) }),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (checked) AdminTokens.Brand else MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    if (checked) AdminTokens.Brand else AdminTokens.FieldBorder,
                    RoundedCornerShape(4.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_check),
                    contentDescription = contentDescription,
                    size = 14.dp,
                    tint = AdminTokens.OnBrand,
                )
            }
        }
    }
}

@Composable
fun AdminToggle(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    val trackWidth = 36.dp
    val trackHeight = 20.dp
    val thumb = 16.dp
    val offset by animateDpAsState(if (checked) 16.dp else 2.dp, label = "admin-toggle")
    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(if (checked) AdminTokens.Brand else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (checked) AdminTokens.Brand else AdminTokens.FieldBorder,
                shape = RoundedCornerShape(percent = 50),
            ),
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .offset(x = offset)
                .size(thumb)
                .clip(CircleShape)
                .background(if (checked) MaterialTheme.colorScheme.surface else AdminTokens.Helper),
        )
    }
}
