package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.vitran.shop.feature.admin.catalog.location.presentation.AdminCitiesUiState
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationQuery
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerUiState
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminSearchableSelect
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.components.admin.AdminSelectField
import com.vitran.shop.ui.components.admin.AdminSelectOption
import com.vitran.shop.ui.components.admin.AdminTaxonomyNode
import com.vitran.shop.ui.components.admin.AdminTaxonomyPicker
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.reference.toAdminTaxonomyNodes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing

@Composable
fun AdminShopsFilters(
    query: AdminModerationQuery,
    userIdText: String,
    onUserIdTextChange: (String) -> Unit,
    onActiveChange: (Boolean?) -> Unit,
    onCityIdChange: (Long?) -> Unit,
    onCategorySlugChange: (String?) -> Unit,
    onClear: () -> Unit,
    citiesState: AdminCitiesUiState,
    onCitiesRetry: () -> Unit,
    taxonomyState: TaxonomyPickerUiState,
    onTaxonomyRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taxonomyRoots = when (taxonomyState) {
        is TaxonomyPickerUiState.Content -> taxonomyState.roots.toAdminTaxonomyNodes()
        else -> emptyList()
    }
    val cityOptions = buildList {
        add(AdminSelectOption(id = AllCitiesId, label = "همه"))
        val cities = (citiesState as? AdminCitiesUiState.Content)?.cities.orEmpty()
        cities.forEach { city ->
            add(AdminSelectOption(id = city.id.value.toString(), label = city.name))
        }
        val selectedId = query.cityId?.toString()
        if (selectedId != null && none { it.id == selectedId }) {
            add(AdminSelectOption(id = selectedId, label = selectedId))
        }
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
                        CityFilter(
                            query = query,
                            options = cityOptions,
                            citiesState = citiesState,
                            onCityIdChange = onCityIdChange,
                            onCitiesRetry = onCitiesRetry,
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
                    CityFilter(
                        query = query,
                        options = cityOptions,
                        citiesState = citiesState,
                        onCityIdChange = onCityIdChange,
                        onCitiesRetry = onCitiesRetry,
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

@Composable
private fun ActiveFilter(
    query: AdminModerationQuery,
    onActiveChange: (Boolean?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedId = when (query.active) {
        null -> ActiveAllId
        true -> ActiveTrueId
        false -> ActiveFalseId
    }
    AdminSelectField(
        label = "وضعیت",
        valueId = selectedId,
        options = ActiveOptions,
        onSelect = { option ->
            onActiveChange(
                when (option.id) {
                    ActiveTrueId -> true
                    ActiveFalseId -> false
                    else -> null
                },
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun CityFilter(
    query: AdminModerationQuery,
    options: List<AdminSelectOption>,
    citiesState: AdminCitiesUiState,
    onCityIdChange: (Long?) -> Unit,
    onCitiesRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        AdminSearchableSelect(
            label = "شهر",
            valueId = query.cityId?.toString() ?: AllCitiesId,
            options = options,
            onSelect = { option -> onCityIdChange(option.id.toLongOrNull()) },
            placeholder = "همه",
            searchPlaceholder = "جستجوی شهر",
            showItemChevron = false,
        )
        when (citiesState) {
            AdminCitiesUiState.Loading ->
                VitranText("در حال بارگذاری شهرها…", VitranTextStyle.Label)
            AdminCitiesUiState.Empty ->
                VitranText("شهری ثبت نشده است", VitranTextStyle.Label)
            is AdminCitiesUiState.Error -> {
                VitranText(
                    text = citiesState.error.message ?: "خطا در دریافت شهرها",
                    style = VitranTextStyle.Label,
                    color = AdminTokens.Destructive,
                )
                AdminSecondaryButton(label = "تلاش مجدد", onClick = onCitiesRetry)
            }
            is AdminCitiesUiState.Content -> Unit
        }
    }
}

@Composable
private fun CategoryFilter(
    query: AdminModerationQuery,
    taxonomyRoots: List<AdminTaxonomyNode>,
    taxonomyState: TaxonomyPickerUiState,
    onCategorySlugChange: (String?) -> Unit,
    onTaxonomyRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        AdminTaxonomyPicker(
            label = "دسته‌بندی",
            valueId = query.categorySlug,
            roots = taxonomyRoots,
            onSelect = { node -> onCategorySlugChange(node.id) },
            placeholder = "همه",
        )
        if (query.categorySlug != null) {
            AdminSecondaryButton(
                label = "حذف دسته‌بندی",
                onClick = { onCategorySlugChange(null) },
            )
        }
        when (taxonomyState) {
            TaxonomyPickerUiState.Loading ->
                VitranText("در حال بارگذاری دسته‌بندی‌ها…", VitranTextStyle.Label)
            TaxonomyPickerUiState.Empty ->
                VitranText("دسته‌بندی‌ای ثبت نشده است", VitranTextStyle.Label)
            is TaxonomyPickerUiState.Error -> {
                VitranText(
                    text = taxonomyState.message,
                    style = VitranTextStyle.Label,
                    color = AdminTokens.Destructive,
                )
                AdminSecondaryButton(label = "تلاش مجدد", onClick = onTaxonomyRetry)
            }
            is TaxonomyPickerUiState.Content -> Unit
        }
    }
}

@Composable
private fun UserIdFilter(
    userIdText: String,
    onUserIdTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdminTextField(
        label = "شناسه کاربر",
        value = userIdText,
        onValueChange = onUserIdTextChange,
        placeholder = "همه",
        ltr = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
    )
}

private const val AllCitiesId = ""
private const val ActiveAllId = "all"
private const val ActiveTrueId = "true"
private const val ActiveFalseId = "false"

private val ActiveOptions = listOf(
    AdminSelectOption(id = ActiveAllId, label = "همه"),
    AdminSelectOption(id = ActiveFalseId, label = "غیرفعال"),
    AdminSelectOption(id = ActiveTrueId, label = "فعال"),
)
