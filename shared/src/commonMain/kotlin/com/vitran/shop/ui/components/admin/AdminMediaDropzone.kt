package com.vitran.shop.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_media_add_a11y
import vitranshop.shared.generated.resources.admin_media_change
import vitranshop.shared.generated.resources.admin_media_drop_hint
import vitranshop.shared.generated.resources.admin_media_remove
import vitranshop.shared.generated.resources.admin_media_size_mock
import vitranshop.shared.generated.resources.ic_close
import vitranshop.shared.generated.resources.ic_plus

@Composable
fun AdminMediaDropzone(
    label: String,
    imageUrl: String?,
    onPick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = AdminTokens.CoverDropzoneHeight,
    circular: Boolean = false,
    hint: String? = null,
    specs: List<String> = emptyList(),
    zoom: Float = 1f,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            ),
        )
        if (imageUrl.isNullOrBlank()) {
            EmptyDropzone(
                circular = circular,
                height = height,
                hint = hint ?: stringResource(Res.string.admin_media_drop_hint),
                onClick = onPick,
            )
            if (specs.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    specs.forEach { line ->
                        Text(
                            text = line,
                            color = AdminTokens.Helper,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        )
                    }
                }
            }
        } else {
            FilledDropzone(
                imageUrl = imageUrl,
                circular = circular,
                height = height,
                onPick = onPick,
                onRemove = onRemove,
                zoom = zoom,
                offsetX = offsetX,
                offsetY = offsetY,
            )
        }
    }
}

@Composable
private fun EmptyDropzone(
    circular: Boolean,
    height: Dp,
    hint: String,
    onClick: () -> Unit,
) {
    val shape = if (circular) CircleShape else RoundedCornerShape(AdminTokens.FieldRadius)
    val dashColor = AdminTokens.FieldBorder
    Box(
        modifier = Modifier
            .then(if (circular) Modifier.size(height) else Modifier.fillMaxWidth().height(height))
            .clip(shape)
            .background(AdminTokens.DropdownHover, shape)
            .drawBehind {
                val stroke = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 8.dp.toPx())),
                )
                if (circular) {
                    drawCircle(color = dashColor, style = stroke)
                } else {
                    drawRoundRect(
                        color = dashColor,
                        style = stroke,
                        cornerRadius = CornerRadius(AdminTokens.FieldRadius.toPx()),
                    )
                }
            }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_plus),
                    contentDescription = stringResource(Res.string.admin_media_add_a11y),
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            if (!circular) {
                Text(
                    text = hint,
                    color = AdminTokens.Helper,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                )
            }
        }
    }
}

@Composable
private fun FilledDropzone(
    imageUrl: String,
    circular: Boolean,
    height: Dp,
    onPick: () -> Unit,
    onRemove: () -> Unit,
    zoom: Float = 1f,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
) {
    val shape = if (circular) CircleShape else RoundedCornerShape(AdminTokens.FieldRadius)
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        Box(
            modifier = Modifier
                .then(if (circular) Modifier.size(height) else Modifier.fillMaxWidth().height(height))
                .clip(shape)
                .clickable(role = Role.Button, onClick = onPick),
        ) {
            AsyncImage(
                model = resolveNetworkImageUrl(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .background(AdminTokens.DropdownHover)
                    .graphicsLayer {
                        scaleX = zoom
                        scaleY = zoom
                        translationX = offsetX * 48f
                        translationY = offsetY * 28f
                    },
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(VitranSpacing.sm)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                    .clickable(role = Role.Button, onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = stringResource(Res.string.admin_media_remove),
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        if (!circular) {
            Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                Text(
                    text = stringResource(Res.string.admin_media_size_mock),
                    color = AdminTokens.Helper,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                )
                Text(
                    text = stringResource(Res.string.admin_media_change),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                    ),
                    modifier = Modifier.clickable(role = Role.Button, onClick = onPick),
                )
            }
        }
    }
}
