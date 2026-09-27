package com.vitran.shop.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_social_remove
import vitranshop.shared.generated.resources.admin_taxonomy_back_a11y
import vitranshop.shared.generated.resources.admin_taxonomy_search
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.ic_chevron_right

@Composable
fun AdminTaxonomyPicker(
    label: String,
    valueId: String?,
    roots: List<AdminTaxonomyNode>,
    onSelect: (AdminTaxonomyNode) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helper: String? = null,
    searchPlaceholder: String? = null,
) {
    val displayText = valueId?.let { roots.breadcrumbLabel(it) }
    val queryPlaceholder = searchPlaceholder ?: stringResource(Res.string.admin_taxonomy_search)
    AdminDropdownAnchor(
        label = label,
        helper = helper,
        displayText = displayText,
        placeholder = placeholder,
        enabled = true,
        modifier = modifier,
    ) { dismiss ->
        TaxonomyMenu(
            roots = roots,
            selectedIds = setOfNotNull(valueId),
            searchPlaceholder = queryPlaceholder,
            onSelect = { node ->
                onSelect(node)
                dismiss()
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminTaxonomyMultiPicker(
    label: String,
    selectedIds: List<String>,
    roots: List<AdminTaxonomyNode>,
    onToggle: (AdminTaxonomyNode) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helper: String? = null,
    error: String? = null,
    enabled: Boolean = true,
) {
    val queryPlaceholder = stringResource(Res.string.admin_taxonomy_search)
    val selected = selectedIds.toSet()
    val selectedLabels = selectedIds.map { id -> roots.breadcrumbLabel(id) ?: id }
    val displayText = selectedIds
        .map { id -> roots.pathTo(id).lastOrNull()?.label ?: id }
        .takeIf { it.isNotEmpty() }
        ?.joinToString("، ")
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        if (selectedIds.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                selectedIds.forEachIndexed { index, id ->
                    val labelText = selectedLabels[index]
                    TaxonomySelectedChip(
                        label = labelText,
                        onRemove = { onToggle(AdminTaxonomyNode(id = id, label = labelText)) },
                    )
                }
            }
        }
        AdminDropdownAnchor(
            label = label,
            helper = helper,
            displayText = displayText,
            placeholder = placeholder,
            enabled = enabled && roots.isNotEmpty(),
            error = error,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TaxonomyMenu(
                roots = roots,
                selectedIds = selected,
                searchPlaceholder = queryPlaceholder,
                leafOnly = true,
                onSelect = onToggle,
            )
        }
    }
}

@Composable
private fun TaxonomySelectedChip(
    label: String,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .border(1.dp, AdminTokens.FieldBorder, RoundedCornerShape(percent = 50))
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
        Text(
            text = stringResource(Res.string.admin_social_remove),
            color = AdminTokens.Helper,
            fontSize = 12.sp,
            modifier = Modifier.clickable(role = Role.Button, onClick = onRemove),
        )
    }
}

@Composable
private fun TaxonomyMenu(
    roots: List<AdminTaxonomyNode>,
    selectedIds: Set<String>,
    searchPlaceholder: String,
    onSelect: (AdminTaxonomyNode) -> Unit,
    leafOnly: Boolean = false,
) {
    val anchorId = selectedIds.firstOrNull()
    val selectedPath = remember(roots, anchorId) {
        anchorId?.let { roots.pathTo(it) }.orEmpty()
    }
    var query by remember { mutableStateOf("") }
    var currentPath by remember(roots, anchorId) {
        mutableStateOf(
            if (selectedPath.size > 1) selectedPath.dropLast(1) else emptyList(),
        )
    }
    val searching = query.trim().isNotEmpty()
    val hits = remember(query, roots) { roots.searchHits(query) }
    val levelNodes = if (currentPath.isEmpty()) roots else currentPath.last().children

    Column {
        AdminDropdownSearch(
            query = query,
            onQueryChange = { query = it },
            placeholder = searchPlaceholder,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AdminTokens.CardBorder),
        )
        if (searching) {
            TaxonomyHitList(
                hits = hits,
                selectedIds = selectedIds,
                leafOnly = leafOnly,
                onSelect = onSelect,
            )
        } else {
            TaxonomyLevelList(
                nodes = levelNodes,
                currentPath = currentPath,
                selectedIds = selectedIds,
                onDrill = { currentPath = currentPath + it },
                onPop = { currentPath = currentPath.dropLast(1) },
                onSelect = onSelect,
            )
        }
    }
}

@Composable
private fun TaxonomyLevelList(
    nodes: List<AdminTaxonomyNode>,
    currentPath: List<AdminTaxonomyNode>,
    selectedIds: Set<String>,
    onDrill: (AdminTaxonomyNode) -> Unit,
    onPop: () -> Unit,
    onSelect: (AdminTaxonomyNode) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = AdminTokens.DropdownMaxHeight),
    ) {
        if (currentPath.isNotEmpty()) {
            item(key = "taxonomy-back") {
                TaxonomyBackRow(
                    label = currentPath.last().label,
                    onClick = onPop,
                )
            }
        }
        items(nodes, key = { it.id }) { node ->
            TaxonomyNodeRow(
                node = node,
                selected = node.containsSelection(selectedIds),
                showChevron = node.hasChildren,
                description = null,
                onClick = {
                    if (node.hasChildren) onDrill(node) else onSelect(node)
                },
            )
        }
    }
}

@Composable
private fun TaxonomyHitList(
    hits: List<AdminTaxonomyHit>,
    selectedIds: Set<String>,
    leafOnly: Boolean,
    onSelect: (AdminTaxonomyNode) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = AdminTokens.DropdownMaxHeight),
    ) {
        items(hits, key = { it.node.id }) { hit ->
            TaxonomyNodeRow(
                node = hit.node,
                selected = hit.node.containsSelection(selectedIds),
                showChevron = leafOnly && hit.node.hasChildren,
                description = hit.breadcrumb.ifEmpty { null },
                onClick = {
                    if (!leafOnly || !hit.node.hasChildren) onSelect(hit.node)
                },
            )
        }
    }
}

@Composable
private fun TaxonomyBackRow(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = stringResource(Res.string.admin_taxonomy_back_a11y),
            size = VitranSize.iconSmall,
            tint = AdminTokens.Helper,
        )
        Text(
            text = label,
            style = adminFieldTextStyle(),
            color = AdminTokens.Helper,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TaxonomyNodeRow(
    node: AdminTaxonomyNode,
    selected: Boolean,
    showChevron: Boolean,
    description: String?,
    onClick: () -> Unit,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) AdminTokens.DropdownHover else MaterialTheme.colorScheme.surface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        if (selected) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_check),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = node.label,
                style = adminFieldTextStyle(),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = AdminTokens.Helper,
                )
            }
        }
        if (showChevron) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = AdminTokens.Helper,
                modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            )
        }
    }
}
