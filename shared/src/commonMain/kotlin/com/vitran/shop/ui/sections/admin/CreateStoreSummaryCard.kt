package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_summary_never_saved
import vitranshop.shared.generated.resources.admin_preview_store_fallback
import vitranshop.shared.generated.resources.admin_status_draft
import vitranshop.shared.generated.resources.admin_status_published
import vitranshop.shared.generated.resources.admin_summary_copy_link
import vitranshop.shared.generated.resources.admin_summary_last_saved
import vitranshop.shared.generated.resources.admin_summary_percent
import vitranshop.shared.generated.resources.admin_summary_progress
import vitranshop.shared.generated.resources.admin_summary_public_url
import vitranshop.shared.generated.resources.admin_summary_step
import vitranshop.shared.generated.resources.admin_summary_title
import vitranshop.shared.generated.resources.admin_url_copied
import vitranshop.shared.generated.resources.admin_url_prefix

@Composable
fun CreateStoreSummaryCard(
    state: CreateStoreFormState,
    currentStep: CreateStoreStep,
    lastSavedLabel: String?,
    modifier: Modifier = Modifier,
) {
    val fraction = state.wizardFraction(currentStep)
    val percent = (fraction * 100).toInt()
    val stepIndex = currentStep.ordinal + 1
    val stepTotal = CreateStoreStepOrder.size
    var copied by remember { mutableStateOf(false) }
    AdminFormCard(
        modifier = modifier,
        title = stringResource(Res.string.admin_summary_title),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.storeName.ifBlank {
                    stringResource(Res.string.admin_preview_store_fallback)
                },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(
                    if (state.published) Res.string.admin_status_published else Res.string.admin_status_draft,
                ),
                color = if (state.published) AdminTokens.Success else AdminTokens.Helper,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            )
        }
        SummaryDivider()
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            Text(
                text = stringResource(Res.string.admin_summary_progress),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            )
            Text(
                text = stringResource(
                    Res.string.admin_summary_step,
                    stepIndex.toString(),
                    stepTotal.toString(),
                ),
                color = AdminTokens.Helper,
                fontSize = 12.sp,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(AdminTokens.DropdownHover),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction.coerceIn(0.08f, 1f))
                            .height(8.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(state.theme.primary),
                    )
                }
                Text(
                    text = stringResource(Res.string.admin_summary_percent, percent.toString()),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
        }
        SummaryDivider()
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            Text(
                text = stringResource(Res.string.admin_summary_public_url),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            )
            Text(
                text = state.shareUrl.ifBlank {
                    stringResource(Res.string.admin_url_prefix)
                },
                color = if (state.slug.isBlank()) AdminTokens.Helper else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val copyEnabled = state.slug.isNotBlank()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .border(1.dp, AdminTokens.FieldBorder, RoundedCornerShape(VitranRadius.small))
                    .clickable(enabled = copyEnabled, role = Role.Button) { copied = true },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(
                        if (copied) Res.string.admin_url_copied else Res.string.admin_summary_copy_link,
                    ),
                    color = if (copied) AdminTokens.Success else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                )
            }
        }
        SummaryDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.admin_summary_last_saved),
                color = AdminTokens.Helper,
                fontSize = 12.sp,
            )
            Text(
                text = lastSavedLabel ?: stringResource(Res.string.admin_summary_never_saved),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun SummaryDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = VitranSpacing.xs)
            .height(1.dp)
            .background(AdminTokens.CardBorder),
    )
}
