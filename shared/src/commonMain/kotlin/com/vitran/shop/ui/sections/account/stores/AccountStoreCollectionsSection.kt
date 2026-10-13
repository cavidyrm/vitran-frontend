package com.vitran.shop.ui.sections.account.stores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.shop.presentation.ShopCollectionsViewModel
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.components.admin.AdminMultilineField
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountOutlinedButton
import com.vitran.shop.ui.sections.account.AccountPrimaryButton
import com.vitran.shop.ui.sections.account.AccountTextLink
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_stores_loading_more
import vitranshop.shared.generated.resources.admin_collections_add
import vitranshop.shared.generated.resources.admin_collections_cancel
import vitranshop.shared.generated.resources.admin_collections_count
import vitranshop.shared.generated.resources.admin_collections_delete
import vitranshop.shared.generated.resources.admin_collections_description
import vitranshop.shared.generated.resources.admin_collections_edit
import vitranshop.shared.generated.resources.admin_collections_empty
import vitranshop.shared.generated.resources.admin_collections_product_ids
import vitranshop.shared.generated.resources.admin_collections_product_ids_hint
import vitranshop.shared.generated.resources.admin_collections_retry
import vitranshop.shared.generated.resources.admin_collections_save
import vitranshop.shared.generated.resources.admin_collections_title
import vitranshop.shared.generated.resources.admin_collections_title_field

@Composable
internal fun AccountStoreCollectionsSection(
    shopId: ShopId,
    modifier: Modifier = Modifier,
    viewModel: ShopCollectionsViewModel = vitranKoinViewModel { parametersOf(shopId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AccountCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            VitranText(
                text = stringResource(Res.string.admin_collections_title),
                style = VitranTextStyle.Title,
            )
            state.message?.let { message ->
                VitranText(
                    text = message,
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            state.error?.let { message ->
                VitranText(
                    text = message,
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.error,
                )
                AccountTextLink(
                    label = stringResource(Res.string.admin_collections_retry),
                    onClick = viewModel::load,
                )
            }
            when {
                state.isLoading && state.items.isEmpty() ->
                    VitranText(
                        text = stringResource(Res.string.account_stores_loading_more),
                        style = VitranTextStyle.Body,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                state.items.isEmpty() && state.editor == null ->
                    VitranText(
                        text = stringResource(Res.string.admin_collections_empty),
                        style = VitranTextStyle.Body,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                else ->
                    state.items.forEach { collection ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                VitranText(text = collection.title, style = VitranTextStyle.Body)
                                VitranText(
                                    text = stringResource(
                                        Res.string.admin_collections_count,
                                        collection.productCount,
                                    ),
                                    style = VitranTextStyle.Label,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                                AccountTextLink(
                                    label = stringResource(Res.string.admin_collections_edit),
                                    onClick = { viewModel.startEdit(collection) },
                                )
                                AccountTextLink(
                                    label = stringResource(Res.string.admin_collections_delete),
                                    onClick = { viewModel.delete(collection.id) },
                                )
                            }
                        }
                    }
            }
            val editor = state.editor
            if (editor == null) {
                AccountOutlinedButton(
                    label = stringResource(Res.string.admin_collections_add),
                    onClick = viewModel::startCreate,
                )
            } else {
                AdminTextField(
                    label = stringResource(Res.string.admin_collections_title_field),
                    value = editor.title,
                    onValueChange = { value -> viewModel.updateEditor { it.copy(title = value) } },
                )
                AdminMultilineField(
                    label = stringResource(Res.string.admin_collections_description),
                    value = editor.description,
                    onValueChange = { value -> viewModel.updateEditor { it.copy(description = value) } },
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_collections_product_ids),
                    value = editor.productIdsText,
                    onValueChange = { value ->
                        viewModel.updateEditor { it.copy(productIdsText = value, productIdsEdited = true) }
                    },
                    placeholder = stringResource(Res.string.admin_collections_product_ids_hint),
                    ltr = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                    AccountPrimaryButton(
                        label = stringResource(Res.string.admin_collections_save),
                        onClick = viewModel::saveEditor,
                        enabled = !state.isSaving && editor.title.isNotBlank(),
                    )
                    AccountOutlinedButton(
                        label = stringResource(Res.string.admin_collections_cancel),
                        onClick = viewModel::cancelEditor,
                    )
                }
            }
        }
    }
}
