package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vitran.shop.ui.sections.admin.plan.AdminPlanDefinition
import com.vitran.shop.ui.sections.admin.plan.AdminPlansEditorCard
import com.vitran.shop.ui.sections.admin.plan.AdminPlansPageHeader
import com.vitran.shop.ui.sections.admin.plan.AdminPlansPreviewCard
import com.vitran.shop.ui.sections.admin.plan.AdminPlansStatsRow
import com.vitran.shop.ui.sections.admin.plan.AdminPlansTableCard
import com.vitran.shop.ui.sections.admin.plan.AdminPlansTopBar
import com.vitran.shop.ui.sections.admin.plan.StorePlanTokens
import com.vitran.shop.ui.sections.admin.plan.emptyAdminPlanForm
import com.vitran.shop.ui.sections.admin.plan.mockAdminPlans
import com.vitran.shop.ui.sections.admin.plan.mockAdminPlansStats
import com.vitran.shop.ui.sections.admin.plan.toDefinition
import com.vitran.shop.ui.sections.admin.plan.toFormState
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/**
 * Platform admin — manage store plan catalog. Route `/admin/plans`.
 * Mock create / edit / delete only; no network.
 * Responsive: compact stack; two-column from [VitranSize.desktopBreakpoint].
 */
@Composable
fun AdminPlansScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var plans by remember { mutableStateOf(mockAdminPlans()) }
    var selectedId by remember { mutableStateOf(plans.firstOrNull { it.popular }?.id ?: plans.firstOrNull()?.id) }
    var form by remember {
        mutableStateOf(
            plans.firstOrNull { it.id == selectedId }?.toFormState() ?: emptyAdminPlanForm(),
        )
    }

    val stats = remember(plans) { mockAdminPlansStats(plans) }
    val selectedPlan = plans.firstOrNull { it.id == selectedId }

    fun selectPlan(plan: AdminPlanDefinition) {
        selectedId = plan.id
        form = plan.toFormState()
    }

    fun startCreate() {
        selectedId = null
        form = emptyAdminPlanForm()
    }

    fun saveForm() {
        val existingIcon = plans.firstOrNull { it.id == form.id }?.icon
        val saved = form.toDefinition(existingIcon).let { def ->
            if (form.isNew) {
                def.copy(id = "plan-${def.slug}-${plans.size + 1}")
            } else {
                def.copy(
                    popular = plans.firstOrNull { it.id == def.id }?.popular == true,
                )
            }
        }
        plans = if (form.isNew || plans.none { it.id == saved.id }) {
            plans + saved
        } else {
            plans.map { if (it.id == saved.id) saved else it }
        }
        selectedId = saved.id
        form = saved.toFormState()
    }

    fun deleteSelected() {
        val id = form.id ?: return
        val remaining = plans.filterNot { it.id == id }
        plans = remaining
        val next = remaining.firstOrNull()
        selectedId = next?.id
        form = next?.toFormState() ?: emptyAdminPlanForm()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(StorePlanTokens.PageBackground),
    ) {
        val twoColumn = maxWidth >= VitranSize.desktopBreakpoint
        val horizontalPad = if (twoColumn) StorePlanTokens.PageHorizontal else VitranSpacing.lg

        Column(modifier = Modifier.fillMaxSize()) {
            AdminPlansTopBar(
                adminName = "علی محمدی",
                onHomeClick = onBack,
                compact = !twoColumn,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = StorePlanTokens.PageMaxWidth)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = horizontalPad,
                            vertical = VitranSpacing.xl,
                        ),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.xl),
                ) {
                    AdminPlansPageHeader(
                        onCreateClick = ::startCreate,
                        compact = !twoColumn,
                    )
                    AdminPlansStatsRow(stats = stats)
                    if (twoColumn) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier.weight(1.35f),
                                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                            ) {
                                AdminPlansTableCard(
                                    plans = plans,
                                    selectedId = selectedId,
                                    onSelect = ::selectPlan,
                                    onEdit = ::selectPlan,
                                    onDelete = {
                                        selectPlan(it)
                                        deleteSelected()
                                    },
                                    compact = false,
                                )
                                AdminPlansPreviewCard(
                                    plan = selectedPlan,
                                    form = form,
                                    compact = false,
                                )
                            }
                            AdminPlansEditorCard(
                                form = form,
                                onFormChange = { form = it },
                                onSave = ::saveForm,
                                onDelete = ::deleteSelected,
                                compact = false,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        AdminPlansTableCard(
                            plans = plans,
                            selectedId = selectedId,
                            onSelect = ::selectPlan,
                            onEdit = ::selectPlan,
                            onDelete = {
                                selectPlan(it)
                                deleteSelected()
                            },
                            compact = true,
                        )
                        AdminPlansEditorCard(
                            form = form,
                            onFormChange = { form = it },
                            onSave = ::saveForm,
                            onDelete = ::deleteSelected,
                            compact = true,
                        )
                        AdminPlansPreviewCard(
                            plan = selectedPlan,
                            form = form,
                            compact = true,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AdminPlansScreenPreview() {
    VitranTheme {
        AdminPlansScreen(onBack = {})
    }
}
