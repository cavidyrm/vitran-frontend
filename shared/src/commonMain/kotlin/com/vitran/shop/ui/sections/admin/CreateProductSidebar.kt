package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminSearchOrAddField
import com.vitran.shop.ui.components.admin.AdminSelectField
import com.vitran.shop.ui.components.admin.AdminSelectOption
import com.vitran.shop.ui.components.admin.AdminTagField
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_product_card_organization
import vitranshop.shared.generated.resources.admin_product_card_preview
import vitranshop.shared.generated.resources.admin_product_card_status
import vitranshop.shared.generated.resources.admin_product_field_brand
import vitranshop.shared.generated.resources.admin_product_field_brand_placeholder
import vitranshop.shared.generated.resources.admin_product_field_collections
import vitranshop.shared.generated.resources.admin_product_field_collections_helper
import vitranshop.shared.generated.resources.admin_product_field_collections_placeholder
import vitranshop.shared.generated.resources.admin_product_field_tags
import vitranshop.shared.generated.resources.admin_product_field_tags_helper
import vitranshop.shared.generated.resources.admin_product_field_tags_placeholder
import vitranshop.shared.generated.resources.admin_product_field_type
import vitranshop.shared.generated.resources.admin_product_field_type_helper
import vitranshop.shared.generated.resources.admin_product_field_type_placeholder
import vitranshop.shared.generated.resources.admin_product_preview_untitled
import vitranshop.shared.generated.resources.admin_product_price_prefix
import vitranshop.shared.generated.resources.admin_product_status_active
import vitranshop.shared.generated.resources.admin_product_status_active_desc
import vitranshop.shared.generated.resources.admin_product_status_draft
import vitranshop.shared.generated.resources.admin_product_status_draft_desc
import vitranshop.shared.generated.resources.admin_product_type_add

@Composable
fun CreateProductSidebar(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
    ) {
        CreateProductStatusCard(state, onStateChange)
        CreateProductOrganizationCard(state, onStateChange)
        CreateProductPreviewCard(state)
    }
}

@Composable
fun CreateProductStatusCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = rememberStatusOptions()
    val selected = options.firstOrNull { it.id == state.status.name }
    AdminFormCard(
        title = stringResource(Res.string.admin_product_card_status),
        modifier = modifier,
    ) {
        AdminSelectField(
            label = "",
            valueId = state.status.name,
            options = options,
            onSelect = { option ->
                val next = ProductPublishStatus.entries.first { it.name == option.id }
                onStateChange(state.copy(status = next).markedDirty())
            },
            helper = selected?.description,
        )
    }
}

@Composable
fun CreateProductOrganizationCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdminFormCard(
        title = stringResource(Res.string.admin_product_card_organization),
        hasError = state.errors.category != null && state.categoryId == null,
        modifier = modifier,
    ) {
        AdminSearchOrAddField(
            label = stringResource(Res.string.admin_product_field_type),
            value = state.type,
            options = CreateProductMocks.productTypes,
            onSelect = { onStateChange(state.copy(type = it).clearedErrors().markedDirty()) },
            placeholder = stringResource(Res.string.admin_product_field_type_placeholder),
            helper = stringResource(Res.string.admin_product_field_type_helper),
            formatAddLabel = { query -> stringResource(Res.string.admin_product_type_add, query) },
        )
        AdminSearchOrAddField(
            label = stringResource(Res.string.admin_product_field_brand),
            value = state.brand,
            options = CreateProductMocks.brands,
            onSelect = { onStateChange(state.copy(brand = it).markedDirty()) },
            placeholder = stringResource(Res.string.admin_product_field_brand_placeholder),
            formatAddLabel = { query -> stringResource(Res.string.admin_product_type_add, query) },
        )
        AdminTagField(
            label = stringResource(Res.string.admin_product_field_collections),
            tags = state.collections,
            input = state.collectionInput,
            onInputChange = { onStateChange(state.copy(collectionInput = it).markedDirty()) },
            onAdd = { onStateChange(state.addCollection()) },
            onRemove = { tag ->
                onStateChange(state.copy(collections = state.collections.filterNot { it == tag }).markedDirty())
            },
            placeholder = stringResource(Res.string.admin_product_field_collections_placeholder),
            helper = stringResource(Res.string.admin_product_field_collections_helper),
        )
        AdminTagField(
            label = stringResource(Res.string.admin_product_field_tags),
            tags = state.tags,
            input = state.tagInput,
            onInputChange = { onStateChange(state.copy(tagInput = it).markedDirty()) },
            onAdd = { onStateChange(state.addTag()) },
            onRemove = { tag ->
                onStateChange(state.copy(tags = state.tags.filterNot { it == tag }).markedDirty())
            },
            placeholder = stringResource(Res.string.admin_product_field_tags_placeholder),
            helper = stringResource(Res.string.admin_product_field_tags_helper),
        )
    }
}

@Composable
fun CreateProductPreviewCard(
    state: CreateProductFormState,
    modifier: Modifier = Modifier,
) {
    val statusLabel = stringResource(
        if (state.status == ProductPublishStatus.Active) {
            Res.string.admin_product_status_active
        } else {
            Res.string.admin_product_status_draft
        },
    )
    AdminFormCard(
        title = stringResource(Res.string.admin_product_card_preview),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val imageUrl = state.media.firstOrNull()?.url
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(AdminTokens.FieldRadius))
                    .background(AdminTokens.DropdownHover),
                contentAlignment = Alignment.Center,
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = resolveNetworkImageUrl(imageUrl),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                Text(
                    text = state.title.ifBlank { stringResource(Res.string.admin_product_preview_untitled) },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 2,
                )
                if (state.price.isNotBlank()) {
                    Text(
                        text = "${state.price} ${stringResource(Res.string.admin_product_price_prefix)}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                    )
                }
                Text(
                    text = statusLabel,
                    color = AdminTokens.Helper,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun rememberStatusOptions(): List<AdminSelectOption> {
    val active = stringResource(Res.string.admin_product_status_active)
    val activeDesc = stringResource(Res.string.admin_product_status_active_desc)
    val draft = stringResource(Res.string.admin_product_status_draft)
    val draftDesc = stringResource(Res.string.admin_product_status_draft_desc)
    return remember(active, activeDesc, draft, draftDesc) {
        productStatusOptions(draft, draftDesc, active, activeDesc)
    }
}
