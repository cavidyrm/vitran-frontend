package com.vitran.shop.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_product_media_add
import vitranshop.shared.generated.resources.admin_product_media_add_a11y
import vitranshop.shared.generated.resources.admin_product_media_helper
import vitranshop.shared.generated.resources.admin_product_media_move_next
import vitranshop.shared.generated.resources.admin_product_media_move_prev
import vitranshop.shared.generated.resources.admin_product_media_primary
import vitranshop.shared.generated.resources.admin_product_media_remove_a11y
import vitranshop.shared.generated.resources.admin_product_media_select_existing
import vitranshop.shared.generated.resources.admin_product_media_set_primary
import vitranshop.shared.generated.resources.admin_product_media_upload
import vitranshop.shared.generated.resources.admin_product_media_uploading
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_close
import vitranshop.shared.generated.resources.ic_plus

private val MediaTileSize = 88.dp
private val PrimaryMediaSize = 168.dp
private val EmptyDropzoneHeight = 140.dp

@Immutable
data class AdminMediaTile(
    val id: String,
    val url: String,
    val uploading: Boolean = false,
    val isPrimary: Boolean = false,
    val previewBytes: ByteArray? = null,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminMediaUploadPanel(
    tiles: List<AdminMediaTile>,
    onUpload: () -> Unit,
    onSelectExisting: () -> Unit,
    onRemove: (String) -> Unit,
    onSetPrimary: (String) -> Unit,
    onMove: (String, towardStart: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val helper = stringResource(Res.string.admin_product_media_helper)
    if (tiles.isEmpty()) {
        EmptyMediaDropzone(
            uploadLabel = stringResource(Res.string.admin_product_media_upload),
            selectLabel = stringResource(Res.string.admin_product_media_select_existing),
            helper = helper,
            onUpload = onUpload,
            onSelectExisting = onSelectExisting,
            modifier = modifier,
        )
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                verticalAlignment = Alignment.Top,
            ) {
                val primary = tiles.first()
                MediaThumb(
                    tile = primary,
                    size = PrimaryMediaSize,
                    showPrimaryBadge = true,
                    onRemove = { onRemove(primary.id) },
                    onSetPrimary = null,
                    onMoveStart = null,
                    onMoveEnd = if (tiles.size > 1) ({ onMove(primary.id, false) }) else null,
                )
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    tiles.drop(1).forEachIndexed { index, tile ->
                        MediaThumb(
                            tile = tile,
                            size = MediaTileSize,
                            showPrimaryBadge = false,
                            onRemove = { onRemove(tile.id) },
                            onSetPrimary = { onSetPrimary(tile.id) },
                            onMoveStart = { onMove(tile.id, true) },
                            onMoveEnd = if (index < tiles.size - 2) ({ onMove(tile.id, false) }) else null,
                        )
                    }
                    AddMediaTile(onClick = onUpload)
                }
            }
            Text(
                text = helper,
                color = AdminTokens.Helper,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            )
        }
    }
}

@Composable
private fun EmptyMediaDropzone(
    uploadLabel: String,
    selectLabel: String,
    helper: String,
    onUpload: () -> Unit,
    onSelectExisting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(AdminTokens.FieldRadius)
    val dashColor = AdminTokens.FieldBorder
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(EmptyDropzoneHeight)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .drawBehind {
                val stroke = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 8.dp.toPx())),
                )
                drawRoundRect(
                    color = dashColor,
                    style = stroke,
                    cornerRadius = CornerRadius(AdminTokens.FieldRadius.toPx()),
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm, Alignment.CenterVertically),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AdminSecondaryButton(label = uploadLabel, onClick = onUpload)
            AdminTextLink(label = selectLabel, onClick = onSelectExisting)
        }
        Text(
            text = helper,
            color = AdminTokens.Helper,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
        )
    }
}

@Composable
private fun MediaThumb(
    tile: AdminMediaTile,
    size: androidx.compose.ui.unit.Dp,
    showPrimaryBadge: Boolean,
    onRemove: () -> Unit,
    onSetPrimary: (() -> Unit)?,
    onMoveStart: (() -> Unit)?,
    onMoveEnd: (() -> Unit)?,
) {
    val shape = RoundedCornerShape(AdminTokens.FieldRadius)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(AdminTokens.DropdownHover, shape)
            .border(1.dp, AdminTokens.CardBorder, shape),
    ) {
        AsyncImage(
            model = tile.previewBytes ?: resolveNetworkImageUrl(tile.url),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        if (tile.uploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.admin_product_media_uploading),
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(VitranSpacing.sm),
                )
            }
        }
        if (showPrimaryBadge) {
            Text(
                text = stringResource(Res.string.admin_product_media_primary),
                color = AdminTokens.OnBrand,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(VitranSpacing.sm)
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .background(AdminTokens.Brand)
                    .padding(horizontal = VitranSpacing.sm, vertical = 2.dp),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(VitranSpacing.xs)
                .size(VitranSize.touchTarget / 2)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                .clickable(role = Role.Button, onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = stringResource(Res.string.admin_product_media_remove_a11y),
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(VitranSpacing.xs),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (onMoveStart != null) {
                MediaIconButton(
                    icon = painterResource(Res.drawable.ic_chevron_right),
                    label = stringResource(Res.string.admin_product_media_move_prev),
                    onClick = onMoveStart,
                )
            }
            if (onMoveEnd != null) {
                MediaIconButton(
                    icon = painterResource(Res.drawable.ic_chevron_right),
                    label = stringResource(Res.string.admin_product_media_move_next),
                    onClick = onMoveEnd,
                    rotate = true,
                )
            }
        }
        if (onSetPrimary != null) {
            Text(
                text = stringResource(Res.string.admin_product_media_set_primary),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(VitranSpacing.xs)
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                    .clickable(role = Role.Button, onClick = onSetPrimary)
                    .padding(horizontal = VitranSpacing.sm, vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun MediaIconButton(
    icon: androidx.compose.ui.graphics.painter.Painter,
    label: String,
    onClick: () -> Unit,
    rotate: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = icon,
            contentDescription = label,
            size = VitranSize.iconSmall,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = if (rotate) Modifier.rotate(180f) else Modifier,
        )
    }
}

@Composable
private fun AddMediaTile(onClick: () -> Unit) {
    val shape = RoundedCornerShape(AdminTokens.FieldRadius)
    val dashColor = AdminTokens.FieldBorder
    Column(
        modifier = Modifier
            .size(MediaTileSize)
            .clip(shape)
            .background(AdminTokens.DropdownHover, shape)
            .drawBehind {
                val stroke = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx())),
                )
                drawRoundRect(
                    color = dashColor,
                    style = stroke,
                    cornerRadius = CornerRadius(AdminTokens.FieldRadius.toPx()),
                )
            }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(VitranSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_plus),
            contentDescription = stringResource(Res.string.admin_product_media_add_a11y),
            size = VitranSize.iconSmall,
            tint = AdminTokens.Brand,
        )
        Text(
            text = stringResource(Res.string.admin_product_media_add),
            color = AdminTokens.Brand,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
