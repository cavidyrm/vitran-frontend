package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminMediaTile
import com.vitran.shop.ui.components.admin.AdminMediaUploadPanel
import com.vitran.shop.ui.components.admin.AdminMultilineField
import com.vitran.shop.ui.components.admin.AdminSearchableSelect
import com.vitran.shop.ui.components.admin.AdminSelectOption
import com.vitran.shop.ui.components.admin.AdminTextButton
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.components.admin.AdminToggleRow
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_product_add_compare_at
import vitranshop.shared.generated.resources.admin_product_add_sku
import vitranshop.shared.generated.resources.admin_product_card_basics
import vitranshop.shared.generated.resources.admin_product_card_basics_sub
import vitranshop.shared.generated.resources.admin_product_card_inventory
import vitranshop.shared.generated.resources.admin_product_card_media
import vitranshop.shared.generated.resources.admin_product_card_price
import vitranshop.shared.generated.resources.admin_product_card_seo
import vitranshop.shared.generated.resources.admin_product_card_shipping
import vitranshop.shared.generated.resources.admin_product_category_baby
import vitranshop.shared.generated.resources.admin_product_category_beauty
import vitranshop.shared.generated.resources.admin_product_category_fitness
import vitranshop.shared.generated.resources.admin_product_category_food
import vitranshop.shared.generated.resources.admin_product_category_home
import vitranshop.shared.generated.resources.admin_product_category_men
import vitranshop.shared.generated.resources.admin_product_category_pet
import vitranshop.shared.generated.resources.admin_product_category_sporting
import vitranshop.shared.generated.resources.admin_product_category_toys
import vitranshop.shared.generated.resources.admin_product_category_women
import vitranshop.shared.generated.resources.admin_product_continue_selling
import vitranshop.shared.generated.resources.admin_product_field_barcode
import vitranshop.shared.generated.resources.admin_product_field_barcode_placeholder
import vitranshop.shared.generated.resources.admin_product_field_category
import vitranshop.shared.generated.resources.admin_product_field_category_helper
import vitranshop.shared.generated.resources.admin_product_field_category_placeholder
import vitranshop.shared.generated.resources.admin_product_field_category_search
import vitranshop.shared.generated.resources.admin_product_field_compare_at
import vitranshop.shared.generated.resources.admin_product_field_description
import vitranshop.shared.generated.resources.admin_product_field_price
import vitranshop.shared.generated.resources.admin_product_field_price_placeholder
import vitranshop.shared.generated.resources.admin_product_field_seo_desc
import vitranshop.shared.generated.resources.admin_product_field_seo_desc_placeholder
import vitranshop.shared.generated.resources.admin_product_field_seo_title
import vitranshop.shared.generated.resources.admin_product_field_sku
import vitranshop.shared.generated.resources.admin_product_field_sku_placeholder
import vitranshop.shared.generated.resources.admin_product_field_slug
import vitranshop.shared.generated.resources.admin_product_field_title
import vitranshop.shared.generated.resources.admin_product_field_title_count
import vitranshop.shared.generated.resources.admin_product_field_title_placeholder
import vitranshop.shared.generated.resources.admin_product_field_weight
import vitranshop.shared.generated.resources.admin_product_inventory_empty
import vitranshop.shared.generated.resources.admin_product_inventory_location
import vitranshop.shared.generated.resources.admin_product_inventory_tracked
import vitranshop.shared.generated.resources.admin_product_inventory_zero
import vitranshop.shared.generated.resources.admin_product_media_removed
import vitranshop.shared.generated.resources.admin_product_media_undo
import vitranshop.shared.generated.resources.admin_product_price_default_helper
import vitranshop.shared.generated.resources.admin_product_price_prefix
import vitranshop.shared.generated.resources.admin_product_price_variant_helper
import vitranshop.shared.generated.resources.admin_product_seo_collapse
import vitranshop.shared.generated.resources.admin_product_seo_expand
import vitranshop.shared.generated.resources.admin_product_seo_preview_fallback_desc
import vitranshop.shared.generated.resources.admin_product_seo_preview_fallback_title
import vitranshop.shared.generated.resources.admin_product_seo_preview_title
import vitranshop.shared.generated.resources.admin_product_shipping_helper
import vitranshop.shared.generated.resources.admin_product_shipping_needed
import vitranshop.shared.generated.resources.admin_product_slug_available
import vitranshop.shared.generated.resources.admin_product_url_prefix
import vitranshop.shared.generated.resources.admin_product_weight_placeholder
import vitranshop.shared.generated.resources.admin_product_weight_unit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateProductMainColumn(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
    titleAnchor: BringIntoViewRequester,
    priceAnchor: BringIntoViewRequester,
    categoryAnchor: BringIntoViewRequester,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
    ) {
        if (state.errors.summary != null) {
            Text(
                text = state.errors.summary,
                color = AdminTokens.Destructive,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            )
        }
        TitleDescriptionCard(state, onStateChange, titleAnchor)
        MediaCard(state, onStateChange)
        CategoryCard(state, onStateChange, categoryAnchor)
        PriceCard(state, onStateChange, priceAnchor)
        InventoryCard(state, onStateChange)
        ShippingCard(state, onStateChange)
        CreateProductVariantsCard(state, onStateChange)
        SeoCard(state, onStateChange)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TitleDescriptionCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
    titleAnchor: BringIntoViewRequester,
) {
    AdminFormCard(
        title = stringResource(Res.string.admin_product_card_basics),
        subtitle = stringResource(Res.string.admin_product_card_basics_sub),
        hasError = state.errors.title != null,
        modifier = Modifier.bringIntoViewRequester(titleAnchor),
    ) {
        AdminTextField(
            label = stringResource(Res.string.admin_product_field_title),
            value = state.title,
            onValueChange = { onStateChange(state.withTitle(it)) },
            placeholder = stringResource(Res.string.admin_product_field_title_placeholder),
            required = true,
            error = state.errors.title,
        )
        if (state.title.length >= CreateProductMocks.TitleMaxLength - 20) {
            Text(
                text = stringResource(Res.string.admin_product_field_title_count, state.title.length),
                color = AdminTokens.Helper,
                fontSize = 12.sp,
            )
        }
        AdminMultilineField(
            label = stringResource(Res.string.admin_product_field_description),
            value = state.description,
            onValueChange = { onStateChange(state.copy(description = it).markedDirty()) },
            showToolbar = true,
        )
    }
}

@Composable
private fun MediaCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
) {
    AdminFormCard(title = stringResource(Res.string.admin_product_card_media)) {
        AdminMediaUploadPanel(
            tiles = state.media.mapIndexed { index, item ->
                AdminMediaTile(
                    id = item.id,
                    url = item.url,
                    uploading = item.uploading,
                    isPrimary = index == 0,
                )
            },
            onUpload = {
                val url = CreateProductMocks.nextMediaUrl(state.mediaUrls)
                onStateChange(state.addMedia(CreateProductMocks.newMedia(url, uploading = true)))
            },
            onSelectExisting = {
                val url = CreateProductMocks.existingMediaUrl(state.mediaUrls)
                onStateChange(state.addMedia(CreateProductMocks.newMedia(url, uploading = false)))
            },
            onRemove = { id -> onStateChange(state.removeMedia(id)) },
            onSetPrimary = { id -> onStateChange(state.setPrimaryMedia(id)) },
            onMove = { id, towardStart -> onStateChange(state.moveMedia(id, towardStart)) },
        )
        val removed = state.removedMedia
        if (removed != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                Text(
                    text = stringResource(Res.string.admin_product_media_removed),
                    color = AdminTokens.Helper,
                    fontSize = 13.sp,
                )
                AdminTextButton(
                    label = stringResource(Res.string.admin_product_media_undo),
                    onClick = { onStateChange(state.undoRemoveMedia()) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
    categoryAnchor: BringIntoViewRequester,
) {
    val options = rememberProductCategoryOptions()
    AdminFormCard(
        hasError = state.errors.category != null,
        modifier = Modifier.bringIntoViewRequester(categoryAnchor),
    ) {
        AdminSearchableSelect(
            label = stringResource(Res.string.admin_product_field_category),
            valueId = state.categoryId,
            options = options,
            onSelect = { onStateChange(state.copy(categoryId = it.id).clearedErrors().markedDirty()) },
            placeholder = stringResource(Res.string.admin_product_field_category_placeholder),
            helper = stringResource(Res.string.admin_product_field_category_helper),
            searchPlaceholder = stringResource(Res.string.admin_product_field_category_search),
            showItemChevron = false,
        )
        val categoryError = state.errors.category
        if (categoryError != null) {
            Text(
                text = categoryError,
                color = AdminTokens.Destructive,
                fontSize = 12.sp,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PriceCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
    priceAnchor: BringIntoViewRequester,
) {
    AdminFormCard(
        title = stringResource(Res.string.admin_product_card_price),
        hasError = state.errors.price != null || state.errors.compareAt != null,
        modifier = Modifier.bringIntoViewRequester(priceAnchor),
    ) {
        AdminTextField(
            label = stringResource(Res.string.admin_product_field_price),
            value = state.price,
            onValueChange = { onStateChange(state.copy(price = formatMoneyInput(it)).clearedErrors().markedDirty().let { next ->
                next.copy(errors = next.errors.copy(compareAt = next.liveCompareAtError()))
            }) },
            placeholder = stringResource(Res.string.admin_product_field_price_placeholder),
            prefix = stringResource(Res.string.admin_product_price_prefix),
            required = true,
            helper = if (state.variantsEnabled) {
                stringResource(Res.string.admin_product_price_variant_helper)
            } else {
                null
            },
            error = state.errors.price,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        if (!state.compareAtEnabled) {
            AdminTextButton(
                label = stringResource(Res.string.admin_product_add_compare_at),
                onClick = { onStateChange(state.copy(compareAtEnabled = true).markedDirty()) },
            )
        } else {
            AdminTextField(
                label = stringResource(Res.string.admin_product_field_compare_at),
                value = state.compareAtPrice,
                onValueChange = {
                    onStateChange(
                        state.copy(compareAtPrice = formatMoneyInput(it)).clearedErrors().markedDirty().let { next ->
                            next.copy(errors = next.errors.copy(compareAt = next.liveCompareAtError()))
                        },
                    )
                },
                placeholder = stringResource(Res.string.admin_product_field_price_placeholder),
                prefix = stringResource(Res.string.admin_product_price_prefix),
                error = state.errors.compareAt,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }
}

@Composable
private fun InventoryCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
) {
    AdminFormCard(title = stringResource(Res.string.admin_product_card_inventory)) {
        AdminToggleRow(
            label = stringResource(Res.string.admin_product_inventory_tracked),
            checked = state.inventoryTracked,
            onCheckedChange = { onStateChange(state.copy(inventoryTracked = it).markedDirty()) },
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.inventoryTracked) {
            AdminTextField(
                label = stringResource(Res.string.admin_product_inventory_location),
                value = state.quantity,
                onValueChange = { onStateChange(state.copy(quantity = it).markedDirty()) },
                helper = if (state.variantsEnabled) {
                    stringResource(Res.string.admin_product_price_default_helper)
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            val qty = parseMoney(state.quantity)
            if (state.quantity.isBlank()) {
                Text(
                    text = stringResource(Res.string.admin_product_inventory_empty),
                    color = AdminTokens.Helper,
                    fontSize = 12.sp,
                )
            } else if (qty == 0L) {
                Text(
                    text = stringResource(Res.string.admin_product_inventory_zero),
                    color = AdminTokens.Helper,
                    fontSize = 12.sp,
                )
            }
        }
        if (!state.skuEnabled) {
            AdminTextButton(
                label = stringResource(Res.string.admin_product_add_sku),
                onClick = { onStateChange(state.copy(skuEnabled = true).markedDirty()) },
            )
        } else {
            AdminTextField(
                label = stringResource(Res.string.admin_product_field_sku),
                value = state.sku,
                onValueChange = { onStateChange(state.copy(sku = it).markedDirty()) },
                placeholder = stringResource(Res.string.admin_product_field_sku_placeholder),
                ltr = true,
            )
            AdminTextField(
                label = stringResource(Res.string.admin_product_field_barcode),
                value = state.barcode,
                onValueChange = { onStateChange(state.copy(barcode = it).markedDirty()) },
                placeholder = stringResource(Res.string.admin_product_field_barcode_placeholder),
                ltr = true,
            )
        }
        AdminToggleRow(
            label = stringResource(Res.string.admin_product_continue_selling),
            checked = state.continueSelling,
            onCheckedChange = { onStateChange(state.copy(continueSelling = it).markedDirty()) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ShippingCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
) {
    AdminFormCard(
        title = stringResource(Res.string.admin_product_card_shipping),
        subtitle = stringResource(Res.string.admin_product_shipping_helper),
    ) {
        AdminToggleRow(
            label = stringResource(Res.string.admin_product_shipping_needed),
            checked = state.needsShipping,
            onCheckedChange = { onStateChange(state.copy(needsShipping = it).markedDirty()) },
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.needsShipping) {
            AdminTextField(
                label = stringResource(Res.string.admin_product_field_weight),
                value = state.weight,
                onValueChange = { onStateChange(state.copy(weight = it).markedDirty()) },
                placeholder = stringResource(Res.string.admin_product_weight_placeholder),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailing = {
                    Text(
                        text = stringResource(Res.string.admin_product_weight_unit),
                        color = AdminTokens.Helper,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    )
                },
            )
        }
    }
}

@Composable
private fun SeoCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
) {
    val previewTitle = state.seoTitle.ifBlank { state.title }.ifBlank {
        stringResource(Res.string.admin_product_seo_preview_fallback_title)
    }
    val previewUrl = CreateProductMocks.UrlPrefix + state.slug.ifBlank { "product" }
    val previewDesc = state.seoDescription.ifBlank { state.description }.ifBlank {
        stringResource(Res.string.admin_product_seo_preview_fallback_desc)
    }
    AdminFormCard(title = stringResource(Res.string.admin_product_card_seo)) {
        SeoSearchPreview(
            title = previewTitle,
            url = previewUrl,
            description = previewDesc,
        )
        AdminTextButton(
            label = stringResource(
                if (state.seoExpanded) Res.string.admin_product_seo_collapse else Res.string.admin_product_seo_expand,
            ),
            onClick = { onStateChange(state.copy(seoExpanded = !state.seoExpanded)) },
        )
        if (state.seoExpanded) {
            AdminTextField(
                label = stringResource(Res.string.admin_product_field_seo_title),
                value = state.seoTitle,
                onValueChange = { onStateChange(state.copy(seoTitle = it).markedDirty()) },
                placeholder = state.title.ifBlank { null },
            )
            AdminMultilineField(
                label = stringResource(Res.string.admin_product_field_seo_desc),
                value = state.seoDescription,
                onValueChange = { onStateChange(state.copy(seoDescription = it).markedDirty()) },
                placeholder = stringResource(Res.string.admin_product_field_seo_desc_placeholder),
            )
            AdminTextField(
                label = stringResource(Res.string.admin_product_field_slug),
                value = state.slug,
                onValueChange = {
                    onStateChange(
                        state.copy(
                            slug = slugifyStoreName(it).ifBlank { it.trim().lowercase() },
                            slugManuallyEdited = true,
                        ).markedDirty(),
                    )
                },
                prefix = stringResource(Res.string.admin_product_url_prefix),
                helper = if (state.slug.isNotBlank()) {
                    stringResource(Res.string.admin_product_slug_available)
                } else {
                    null
                },
                ltr = true,
            )
        }
    }
}

@Composable
private fun SeoSearchPreview(
    title: String,
    url: String,
    description: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AdminTokens.FieldRadius))
            .border(1.dp, AdminTokens.CardBorder, RoundedCornerShape(AdminTokens.FieldRadius))
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Text(
            text = stringResource(Res.string.admin_product_seo_preview_title),
            color = AdminTokens.Helper,
            fontSize = 12.sp,
        )
        Text(
            text = title,
            color = Color(0xFF1A0DAB),
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            maxLines = 2,
        )
        Text(
            text = url,
            color = AdminTokens.Success,
            fontSize = 13.sp,
            maxLines = 1,
        )
        Text(
            text = description,
            color = AdminTokens.Helper,
            fontSize = 13.sp,
            maxLines = 2,
        )
    }
}

@Composable
private fun rememberProductCategoryOptions(): List<AdminSelectOption> {
    val beauty = stringResource(Res.string.admin_product_category_beauty)
    val women = stringResource(Res.string.admin_product_category_women)
    val men = stringResource(Res.string.admin_product_category_men)
    val home = stringResource(Res.string.admin_product_category_home)
    val fitness = stringResource(Res.string.admin_product_category_fitness)
    val baby = stringResource(Res.string.admin_product_category_baby)
    val sporting = stringResource(Res.string.admin_product_category_sporting)
    val food = stringResource(Res.string.admin_product_category_food)
    val toys = stringResource(Res.string.admin_product_category_toys)
    val pet = stringResource(Res.string.admin_product_category_pet)
    return remember(beauty, women, men, home, fitness, baby, sporting, food, toys, pet) {
        listOf(
            AdminSelectOption("beauty", beauty),
            AdminSelectOption("women", women),
            AdminSelectOption("men", men),
            AdminSelectOption("home", home),
            AdminSelectOption("fitness", fitness),
            AdminSelectOption("baby", baby),
            AdminSelectOption("sporting", sporting),
            AdminSelectOption("food", food),
            AdminSelectOption("toys", toys),
            AdminSelectOption("pet", pet),
        )
    }
}
