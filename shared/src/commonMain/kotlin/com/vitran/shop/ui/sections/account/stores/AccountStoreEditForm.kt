package com.vitran.shop.ui.sections.account.stores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.vitran.shop.feature.seller.shop.domain.model.ShopPublicationState
import com.vitran.shop.feature.seller.shop.presentation.EditShopFormState
import com.vitran.shop.feature.seller.shop.presentation.SlugCheckUiStatus
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.components.admin.AdminSelectField
import com.vitran.shop.ui.components.admin.AdminSelectOption
import com.vitran.shop.ui.components.admin.AdminTaxonomyMultiPicker
import com.vitran.shop.ui.components.admin.AdminTaxonomyNode
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountPrimaryButton
import com.vitran.shop.ui.sections.reference.ReferenceDataError
import com.vitran.shop.ui.sections.reference.ReferenceDataLoading
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_store_edit_save
import vitranshop.shared.generated.resources.admin_field_about
import vitranshop.shared.generated.resources.admin_field_about_placeholder
import vitranshop.shared.generated.resources.admin_field_address
import vitranshop.shared.generated.resources.admin_field_avatar_url
import vitranshop.shared.generated.resources.admin_field_avatar_url_placeholder
import vitranshop.shared.generated.resources.admin_field_category_helper
import vitranshop.shared.generated.resources.admin_field_category_placeholder
import vitranshop.shared.generated.resources.admin_field_category_prompt
import vitranshop.shared.generated.resources.admin_field_city
import vitranshop.shared.generated.resources.admin_field_city_placeholder
import vitranshop.shared.generated.resources.admin_field_instagram
import vitranshop.shared.generated.resources.admin_field_phone
import vitranshop.shared.generated.resources.admin_field_phone_placeholder
import vitranshop.shared.generated.resources.admin_field_slug
import vitranshop.shared.generated.resources.admin_field_store_name
import vitranshop.shared.generated.resources.admin_field_store_name_placeholder
import vitranshop.shared.generated.resources.admin_field_support_times
import vitranshop.shared.generated.resources.admin_field_support_times_placeholder
import vitranshop.shared.generated.resources.admin_field_telegram
import vitranshop.shared.generated.resources.admin_field_website
import vitranshop.shared.generated.resources.admin_field_whatsapp
import vitranshop.shared.generated.resources.admin_url_available
import vitranshop.shared.generated.resources.admin_url_checking
import vitranshop.shared.generated.resources.admin_url_taken

@Composable
internal fun AccountStoreEditForm(
    form: EditShopFormState,
    publication: ShopPublicationState?,
    fieldErrors: Map<String, String>,
    slugCheck: SlugCheckUiStatus,
    generalError: String?,
    savedMessage: String?,
    isSubmitting: Boolean,
    cityOptions: List<AdminSelectOption>,
    citiesLoading: Boolean,
    citiesError: String?,
    onCitiesRetry: () -> Unit,
    taxonomyRoots: List<AdminTaxonomyNode>,
    taxonomyLoading: Boolean,
    taxonomyError: String?,
    onTaxonomyRetry: () -> Unit,
    onTitleChange: (String) -> Unit,
    onSlugChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSupportTimesChange: (String) -> Unit,
    onAvatarChange: (String) -> Unit,
    onWhatsappChange: (String) -> Unit,
    onTelegramChange: (String) -> Unit,
    onInstagramChange: (String) -> Unit,
    onWebsiteChange: (String) -> Unit,
    onCitySelect: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val slugTaken = slugCheck is SlugCheckUiStatus.Taken
    val slugHelper = when (slugCheck) {
        SlugCheckUiStatus.Checking -> stringResource(Res.string.admin_url_checking)
        is SlugCheckUiStatus.Available -> stringResource(Res.string.admin_url_available)
        else -> null
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        if (publication != null) {
            VitranText(
                text = shopPublicationLabel(publication),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!savedMessage.isNullOrBlank()) {
            VitranText(
                text = savedMessage,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        if (!generalError.isNullOrBlank()) {
            VitranText(
                text = generalError,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.error,
            )
        }
        AccountCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VitranSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            ) {
                AdminTextField(
                    label = stringResource(Res.string.admin_field_store_name),
                    value = form.title,
                    onValueChange = onTitleChange,
                    placeholder = stringResource(Res.string.admin_field_store_name_placeholder),
                    error = fieldErrors["title"],
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_field_slug),
                    value = form.slug,
                    onValueChange = onSlugChange,
                    helper = slugHelper,
                    error = fieldErrors["slug"] ?: if (slugTaken) {
                        stringResource(Res.string.admin_url_taken)
                    } else {
                        null
                    },
                    ltr = true,
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_field_about),
                    value = form.description,
                    onValueChange = onDescriptionChange,
                    placeholder = stringResource(Res.string.admin_field_about_placeholder),
                    singleLine = false,
                    error = fieldErrors["description"],
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_field_address),
                    value = form.address,
                    onValueChange = onAddressChange,
                    singleLine = false,
                    error = fieldErrors["address"],
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_field_phone),
                    value = form.phoneNumber,
                    onValueChange = onPhoneChange,
                    placeholder = stringResource(Res.string.admin_field_phone_placeholder),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    error = fieldErrors["phone_number"],
                    ltr = true,
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_field_support_times),
                    value = form.supportTimes,
                    onValueChange = onSupportTimesChange,
                    placeholder = stringResource(Res.string.admin_field_support_times_placeholder),
                    error = fieldErrors["support_times"],
                )
                when {
                    citiesLoading -> ReferenceDataLoading(message = "در حال بارگذاری شهرها…")
                    citiesError != null -> ReferenceDataError(message = citiesError, onRetry = onCitiesRetry)
                    else -> AdminSelectField(
                        label = stringResource(Res.string.admin_field_city),
                        valueId = form.cityId?.value?.toString(),
                        options = cityOptions,
                        onSelect = { onCitySelect(it.id) },
                        placeholder = stringResource(Res.string.admin_field_city_placeholder),
                        error = fieldErrors["city_id"],
                    )
                }
                AdminTextField(
                    label = stringResource(Res.string.admin_field_avatar_url),
                    value = form.avatarUrl,
                    onValueChange = onAvatarChange,
                    placeholder = stringResource(Res.string.admin_field_avatar_url_placeholder),
                    error = fieldErrors["avatar_url"],
                    ltr = true,
                )
                if (taxonomyLoading) {
                    ReferenceDataLoading(message = "در حال بارگذاری دسته‌ها…")
                }
                taxonomyError?.let { message ->
                    ReferenceDataError(message = message, onRetry = onTaxonomyRetry)
                }
                AdminTaxonomyMultiPicker(
                    label = stringResource(Res.string.admin_field_category_prompt),
                    selectedIds = form.categorySlugs,
                    roots = taxonomyRoots,
                    onToggle = { onToggleCategory(it.id) },
                    placeholder = stringResource(Res.string.admin_field_category_placeholder),
                    helper = stringResource(Res.string.admin_field_category_helper),
                    error = fieldErrors["category_slugs"],
                    enabled = taxonomyRoots.isNotEmpty(),
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_field_whatsapp),
                    value = form.whatsapp,
                    onValueChange = onWhatsappChange,
                    error = fieldErrors["whatsapp"],
                    ltr = true,
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_field_telegram),
                    value = form.telegram,
                    onValueChange = onTelegramChange,
                    error = fieldErrors["telegram"],
                    ltr = true,
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_field_instagram),
                    value = form.instagram,
                    onValueChange = onInstagramChange,
                    error = fieldErrors["instagram"],
                    ltr = true,
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_field_website),
                    value = form.website,
                    onValueChange = onWebsiteChange,
                    error = fieldErrors["website"],
                    ltr = true,
                )
            }
        }
        AccountPrimaryButton(
            label = stringResource(Res.string.account_store_edit_save),
            onClick = onSave,
            enabled = !isSubmitting && !slugTaken,
        )
    }
}
