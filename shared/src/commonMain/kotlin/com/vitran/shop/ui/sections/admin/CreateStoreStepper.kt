package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_step_basics
import vitranshop.shared.generated.resources.admin_step_basics_caption
import vitranshop.shared.generated.resources.admin_step_brand
import vitranshop.shared.generated.resources.admin_step_brand_caption
import vitranshop.shared.generated.resources.admin_step_contact
import vitranshop.shared.generated.resources.admin_step_contact_caption
import vitranshop.shared.generated.resources.admin_step_policies
import vitranshop.shared.generated.resources.admin_step_policies_caption
import vitranshop.shared.generated.resources.admin_step_publish
import vitranshop.shared.generated.resources.admin_step_publish_caption
import vitranshop.shared.generated.resources.ic_check

val CreateStoreStepOrder = listOf(
    CreateStoreStep.Basics,
    CreateStoreStep.Brand,
    CreateStoreStep.Contact,
    CreateStoreStep.Policies,
    CreateStoreStep.Publish,
)

@Composable
fun CreateStoreStep.label(): String = when (this) {
    CreateStoreStep.Basics -> stringResource(Res.string.admin_step_basics)
    CreateStoreStep.Brand -> stringResource(Res.string.admin_step_brand)
    CreateStoreStep.Contact -> stringResource(Res.string.admin_step_contact)
    CreateStoreStep.Policies -> stringResource(Res.string.admin_step_policies)
    CreateStoreStep.Publish -> stringResource(Res.string.admin_step_publish)
}

@Composable
fun CreateStoreStep.caption(): String = when (this) {
    CreateStoreStep.Basics -> stringResource(Res.string.admin_step_basics_caption)
    CreateStoreStep.Brand -> stringResource(Res.string.admin_step_brand_caption)
    CreateStoreStep.Contact -> stringResource(Res.string.admin_step_contact_caption)
    CreateStoreStep.Policies -> stringResource(Res.string.admin_step_policies_caption)
    CreateStoreStep.Publish -> stringResource(Res.string.admin_step_publish_caption)
}

@Composable
fun CreateStoreStepper(
    current: CreateStoreStep,
    state: CreateStoreFormState,
    onStepClick: (CreateStoreStep) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentIndex = CreateStoreStepOrder.indexOf(current)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        CreateStoreStepOrder.forEachIndexed { index, step ->
            val selected = step == current
            val done = index < currentIndex || (state.stepComplete(step) && !selected)
            Row(
                modifier = Modifier.clickable(role = Role.Button) { onStepClick(step) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                Box(
                    modifier = Modifier
                        .size(AdminTokens.StepDot)
                        .clip(CircleShape)
                        .then(
                            when {
                                selected -> Modifier.background(state.theme.primary)
                                done -> Modifier.background(AdminTokens.Success)
                                else -> Modifier
                                    .background(AdminTokens.DropdownHover)
                                    .border(1.5.dp, AdminTokens.FieldBorder, CircleShape)
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        done -> VitranIcon(
                            painter = painterResource(Res.drawable.ic_check),
                            contentDescription = null,
                            size = VitranSize.iconSmall,
                            tint = MaterialTheme.colorScheme.surface,
                        )
                        selected -> Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(state.theme.onPrimary),
                        )
                        else -> Unit
                    }
                }
                Column {
                    Text(
                        text = step.label(),
                        color = when {
                            selected -> MaterialTheme.colorScheme.onSurface
                            done -> AdminTokens.Success
                            else -> AdminTokens.Helper
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            fontSize = 13.sp,
                        ),
                    )
                    Text(
                        text = step.caption(),
                        color = AdminTokens.Helper,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                    )
                }
            }
            if (index < CreateStoreStepOrder.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = VitranSpacing.xs)
                        .widthIn(min = 20.dp)
                        .height(2.dp)
                        .background(
                            when {
                                index < currentIndex -> AdminTokens.Success
                                else -> AdminTokens.CardBorder
                            },
                        ),
                )
            }
        }
    }
}
