package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationQuery
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerUiState
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.sections.reference.toAdminTaxonomyNodes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing

@Composable
fun AdminProductsFilters(
    query: AdminModerationQuery,
    shopIdText: String,
    onShopIdTextChange: (String) -> Unit,
    userIdText: String,
    onUserIdTextChange: (String) -> Unit,
    onActiveChange: (Boolean?) -> Unit,
    onCategorySlugChange: (String?) -> Unit,
    onClear: () -> Unit,
    taxonomyState: TaxonomyPickerUiState,
    onTaxonomyRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taxonomyRoots = when (taxonomyState) {
        is TaxonomyPickerUiState.Content -> taxonomyState.roots.toAdminTaxonomyNodes()
        else -> emptyList()
    }
    AdminFormCard(title = "فیلترها", modifier = modifier) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth >= VitranSize.mdBreakpoint) {
                Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                    ) {
                        ActiveFilter(query, onActiveChange, Modifier.weight(1f))
                        UserIdFilter(
                            userIdText = shopIdText,
                            onUserIdTextChange = onShopIdTextChange,
                            label = "شناسه فروشگاه",
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                    ) {
                        CategoryFilter(
                            query = query,
                            taxonomyRoots = taxonomyRoots,
                            taxonomyState = taxonomyState,
                            onCategorySlugChange = onCategorySlugChange,
                            onTaxonomyRetry = onTaxonomyRetry,
                            modifier = Modifier.weight(1f),
                        )
                        UserIdFilter(
                            userIdText = userIdText,
                            onUserIdTextChange = onUserIdTextChange,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                    ActiveFilter(query, onActiveChange, Modifier.fillMaxWidth())
                    UserIdFilter(
                        userIdText = shopIdText,
                        onUserIdTextChange = onShopIdTextChange,
                        label = "شناسه فروشگاه",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    CategoryFilter(
                        query = query,
                        taxonomyRoots = taxonomyRoots,
                        taxonomyState = taxonomyState,
                        onCategorySlugChange = onCategorySlugChange,
                        onTaxonomyRetry = onTaxonomyRetry,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    UserIdFilter(
                        userIdText = userIdText,
                        onUserIdTextChange = onUserIdTextChange,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        AdminSecondaryButton(label = "پاک کردن فیلترها", onClick = onClear)
    }
}
