package com.vitran.shop.ui.sections.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.about_story_body
import vitranshop.shared.generated.resources.about_story_eyebrow
import vitranshop.shared.generated.resources.about_story_image_a11y
import vitranshop.shared.generated.resources.about_story_title
import vitranshop.shared.generated.resources.ic_check

/**
 * Our story block: copy + checklist + image. Two columns from md.
 */
@Composable
fun AboutStorySection(
    checks: List<String>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val twoColumn = maxWidth >= VitranSize.mdBreakpoint
        if (twoColumn) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xxxl),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AboutStoryCopy(
                    checks = checks,
                    modifier = Modifier.weight(1f),
                )
                AboutStoryImage(modifier = Modifier.weight(1f))
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.xxl),
            ) {
                AboutStoryCopy(
                    checks = checks,
                    modifier = Modifier.fillMaxWidth(),
                )
                AboutStoryImage(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun AboutStoryCopy(
    checks: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.about_story_eyebrow) + " ›",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = ShopPurple,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.md))
        Text(
            text = stringResource(Res.string.about_story_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.md))
        Text(
            text = stringResource(Res.string.about_story_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.xl))
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
            checks.forEach { label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AboutTokens.SoftBar),
                        contentAlignment = Alignment.Center,
                    ) {
                        VitranIcon(
                            painter = painterResource(Res.drawable.ic_check),
                            contentDescription = null,
                            tint = ShopPurple,
                            size = VitranSize.iconSmall,
                        )
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutStoryImage(modifier: Modifier = Modifier) {
    AsyncImage(
        model = AboutMockImages.Story,
        contentDescription = stringResource(Res.string.about_story_image_a11y),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(AboutTokens.ImageRadius))
            .background(AboutTokens.SoftSurface),
    )
}
