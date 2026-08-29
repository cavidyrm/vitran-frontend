package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminTaxonomyNode
import com.vitran.shop.ui.components.admin.AdminTaxonomyPicker
import com.vitran.shop.ui.components.admin.AdminTokens
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_product_field_category
import vitranshop.shared.generated.resources.admin_product_field_category_helper
import vitranshop.shared.generated.resources.admin_product_field_category_placeholder
import vitranshop.shared.generated.resources.admin_product_field_category_search

@Composable
fun CreateCategoryMainColumn(
    state: CreateCategoryFormState,
    onStateChange: (CreateCategoryFormState) -> Unit,
    taxonomyRoots: List<AdminTaxonomyNode>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
    ) {
        AdminFormCard {
            AdminTaxonomyPicker(
                label = stringResource(Res.string.admin_product_field_category),
                valueId = state.categoryId,
                roots = taxonomyRoots,
                onSelect = { onStateChange(state.copy(categoryId = it.id)) },
                placeholder = stringResource(Res.string.admin_product_field_category_placeholder),
                helper = stringResource(Res.string.admin_product_field_category_helper),
                searchPlaceholder = stringResource(Res.string.admin_product_field_category_search),
            )
        }
    }
}
