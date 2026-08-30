package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.AttributeNameEditViewModel
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.CategoryEditViewModel
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.TaxonomyImportViewModel
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.ValueNameEditViewModel
import com.vitran.shop.feature.admin.content.domain.CreateStaticPageCommand
import com.vitran.shop.feature.admin.content.domain.UpdateStaticPageCommand
import com.vitran.shop.feature.admin.content.presentation.AdminStaticPageEditorUiState
import com.vitran.shop.feature.admin.content.presentation.AdminStaticPageEditorViewModel
import com.vitran.shop.feature.admin.content.presentation.AdminStaticPagesViewModel
import com.vitran.shop.feature.admin.moderation.presentation.AdminCommentsViewModel
import com.vitran.shop.feature.admin.moderation.presentation.AdminProductDetailsUiState
import com.vitran.shop.feature.admin.moderation.presentation.AdminProductDetailsViewModel
import com.vitran.shop.feature.admin.moderation.presentation.AdminProductsViewModel
import com.vitran.shop.feature.admin.moderation.presentation.AdminShopsViewModel
import com.vitran.shop.feature.content.domain.model.HtmlContent
import com.vitran.shop.feature.content.domain.model.StaticPageId
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.taxonomy.domain.model.AttributeSlug
import com.vitran.shop.feature.taxonomy.domain.model.AttributeValueSlug
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminMultilineField
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.components.admin.AdminToggleRow
import com.vitran.shop.ui.sections.admin.plan.StorePlanTokens
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.di.vitranKoinViewModel
import org.koin.core.parameter.parametersOf

@Composable
private fun AdminThinScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StorePlanTokens.PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(VitranSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AdminSecondaryButton(label = "بازگشت", onClick = onBack)
            VitranText(title, VitranTextStyle.Headline)
        }
        content()
    }
}

@Composable
fun AdminShopsModerationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminShopsViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdminThinScaffold("بررسی فروشگاه‌ها", onBack, modifier) {
        if (state.loading) CircularProgressIndicator()
        state.shops.forEach { shop ->
            AdminFormCard(
                title = shop.title ?: shop.slug,
                subtitle = "شناسه ${shop.id.value}",
            ) {
                VitranText(if (shop.confirmed) "تأیید شده" else "در انتظار تأیید", VitranTextStyle.Body)
                if (!shop.confirmed) {
                    AdminPrimaryButton(
                        label = "تأیید فروشگاه",
                        onClick = { viewModel.confirm(shop.id) },
                    )
                }
            }
        }
        state.error?.let { VitranText(it.message ?: "خطا در دریافت فروشگاه‌ها", VitranTextStyle.Body) }
    }
}

@Composable
fun AdminProductsModerationScreen(
    onBack: () -> Unit,
    onProductOpen: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminProductsViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdminThinScaffold("بررسی محصولات", onBack, modifier) {
        if (state.loading) CircularProgressIndicator()
        state.products.forEach { product ->
            AdminFormCard(
                title = product.title,
                subtitle = "شناسه ${product.id.value}",
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                    AdminSecondaryButton(
                        label = "جزئیات",
                        onClick = { onProductOpen(product.id.value) },
                    )
                    if (!product.confirmed) {
                        AdminPrimaryButton(
                            label = "تأیید",
                            onClick = { viewModel.confirm(product.id) },
                        )
                    }
                }
            }
        }
        state.error?.let { VitranText(it.message ?: "خطا در دریافت محصولات", VitranTextStyle.Body) }
    }
}

@Composable
fun AdminProductModerationDetailScreen(
    productId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminProductDetailsViewModel = vitranKoinViewModel {
        parametersOf(ProductId(productId))
    },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdminThinScaffold("جزئیات محصول", onBack, modifier) {
        when (val current = state) {
            AdminProductDetailsUiState.Loading -> CircularProgressIndicator()
            is AdminProductDetailsUiState.Error ->
                VitranText(current.error.message ?: "دریافت محصول انجام نشد", VitranTextStyle.Body)
            is AdminProductDetailsUiState.Content -> AdminFormCard(
                title = current.product.title,
                subtitle = "شناسه ${current.product.id.value}",
            ) {
                VitranText(
                    if (current.product.confirmed) "محصول تأیید شده است" else "محصول در انتظار تأیید است",
                    VitranTextStyle.Body,
                )
                VitranText("قیمت: ${current.product.priceAmount ?: 0}", VitranTextStyle.Body)
                if (!current.product.confirmed) {
                    AdminPrimaryButton(
                        label = if (current.confirming) "در حال تأیید…" else "تأیید محصول",
                        onClick = viewModel::confirm,
                    )
                }
            }
        }
    }
}

@Composable
fun AdminCommentConfirmScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminCommentsViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdminThinScaffold("تأیید دیدگاه", onBack, modifier) {
        AdminFormCard(
            title = "تأیید با شناسه",
            subtitle = "برای دیدگاه‌ها صف دریافت وجود ندارد.",
        ) {
            AdminTextField(
                label = "شناسه دیدگاه",
                value = state.commentIdText,
                onValueChange = viewModel::setCommentId,
                required = true,
            )
            AdminPrimaryButton(
                label = if (state.confirming) "در حال تأیید…" else "تأیید دیدگاه",
                onClick = viewModel::confirm,
            )
            state.confirmedComment?.let {
                VitranText("دیدگاه ${it.id.value} تأیید شد.", VitranTextStyle.Body)
            }
            state.error?.let { VitranText(it.message ?: "تأیید انجام نشد", VitranTextStyle.Body) }
        }
    }
}

@Composable
fun AdminTaxonomyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    importViewModel: TaxonomyImportViewModel = vitranKoinViewModel(),
) {
    val importState by importViewModel.uiState.collectAsStateWithLifecycle()
    var categoriesJson by remember { mutableStateOf("") }
    var attributesJson by remember { mutableStateOf("") }
    var categorySlug by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var iconContent by remember { mutableStateOf("") }
    var attributeSlug by remember { mutableStateOf("") }
    var attributeName by remember { mutableStateOf("") }
    var valueSlug by remember { mutableStateOf("") }
    var valueName by remember { mutableStateOf("") }

    AdminThinScaffold("مدیریت طبقه‌بندی", onBack, modifier) {
        AdminFormCard(title = "ورود طبقه‌بندی", subtitle = "محتوای دو فایل JSON را وارد کنید.") {
            AdminMultilineField("دسته‌بندی‌ها", categoriesJson, { categoriesJson = it })
            AdminMultilineField("ویژگی‌ها", attributesJson, { attributesJson = it })
            AdminToggleRow("اطلاعات را بررسی کرده‌ام", importState.isConfirmed, importViewModel::setConfirmed)
            AdminPrimaryButton(
                label = if (importState.isSubmitting) "در حال ورود…" else "ورود اطلاعات",
                onClick = {
                    importViewModel.setCategoriesFile(
                        SelectedFile.fromBytes("categories.json", categoriesJson.encodeToByteArray(), "application/json"),
                    )
                    importViewModel.setAttributesFile(
                        SelectedFile.fromBytes("attributes.json", attributesJson.encodeToByteArray(), "application/json"),
                    )
                    importViewModel.import()
                },
            )
            if (importState.imported) VitranText("طبقه‌بندی وارد شد.", VitranTextStyle.Body)
            importState.error?.let { VitranText(it.message ?: "ورود انجام نشد", VitranTextStyle.Body) }
        }
        TaxonomyRenameForms(
            categorySlug = categorySlug,
            onCategorySlug = { categorySlug = it },
            categoryName = categoryName,
            onCategoryName = { categoryName = it },
            iconContent = iconContent,
            onIconContent = { iconContent = it },
            attributeSlug = attributeSlug,
            onAttributeSlug = { attributeSlug = it },
            attributeName = attributeName,
            onAttributeName = { attributeName = it },
            valueSlug = valueSlug,
            onValueSlug = { valueSlug = it },
            valueName = valueName,
            onValueName = { valueName = it },
        )
    }
}

@Composable
private fun TaxonomyRenameForms(
    categorySlug: String,
    onCategorySlug: (String) -> Unit,
    categoryName: String,
    onCategoryName: (String) -> Unit,
    iconContent: String,
    onIconContent: (String) -> Unit,
    attributeSlug: String,
    onAttributeSlug: (String) -> Unit,
    attributeName: String,
    onAttributeName: (String) -> Unit,
    valueSlug: String,
    onValueSlug: (String) -> Unit,
    valueName: String,
    onValueName: (String) -> Unit,
) {
    val categoryVm: CategoryEditViewModel = vitranKoinViewModel(
        parameters = { parametersOf(CategorySlug(categorySlug)) },
    )
    val attributeVm: AttributeNameEditViewModel = vitranKoinViewModel(
        parameters = { parametersOf(AttributeSlug(attributeSlug)) },
    )
    val valueVm: ValueNameEditViewModel = vitranKoinViewModel(
        parameters = { parametersOf(AttributeValueSlug(valueSlug)) },
    )
    AdminFormCard(title = "ویرایش دسته‌بندی") {
        AdminTextField("شناسه دسته‌بندی", categorySlug, onCategorySlug)
        AdminTextField("نام جدید", categoryName, onCategoryName)
        AdminPrimaryButton("ذخیره نام", { if (categorySlug.isNotBlank()) categoryVm.rename(categoryName) })
        AdminMultilineField("محتوای آیکن", iconContent, onIconContent)
        AdminSecondaryButton(
            "بارگذاری آیکن",
            {
                if (categorySlug.isNotBlank() && iconContent.isNotBlank()) {
                    categoryVm.uploadIcon(
                        SelectedFile.fromBytes("category-icon.svg", iconContent.encodeToByteArray(), "image/svg+xml"),
                    )
                }
            },
        )
    }
    AdminFormCard(title = "ویرایش نام ویژگی") {
        AdminTextField("شناسه ویژگی", attributeSlug, onAttributeSlug)
        AdminTextField("نام جدید", attributeName, onAttributeName)
        AdminPrimaryButton("ذخیره", { if (attributeSlug.isNotBlank()) attributeVm.rename(attributeName) })
    }
    AdminFormCard(title = "ویرایش نام مقدار") {
        AdminTextField("شناسه مقدار", valueSlug, onValueSlug)
        AdminTextField("نام جدید", valueName, onValueName)
        AdminPrimaryButton("ذخیره", { if (valueSlug.isNotBlank()) valueVm.rename(valueName) })
    }
}

@Composable
fun AdminStaticPagesScreen(
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminStaticPagesViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdminThinScaffold("صفحه‌های ثابت", onBack, modifier) {
        AdminPrimaryButton("ساخت صفحه", onCreate)
        if (state.loading) CircularProgressIndicator()
        state.pages.forEach { page ->
            AdminFormCard(title = page.title, subtitle = page.slug.value) {
                Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                    AdminSecondaryButton("ویرایش", { onEdit(page.id.value) })
                    if (state.canDeleteStaticPage) {
                        AdminSecondaryButton("حذف", { viewModel.delete(page.id) })
                    }
                }
            }
        }
        state.error?.let { VitranText(it.message ?: "دریافت صفحه‌ها انجام نشد", VitranTextStyle.Body) }
    }
}

@Composable
fun AdminStaticPageEditorScreen(
    pageId: Long?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminStaticPageEditorViewModel = vitranKoinViewModel {
        parametersOf(pageId?.let(::StaticPageId))
    },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val existing = when (val current = state) {
        is AdminStaticPageEditorUiState.Editing -> current.page
        is AdminStaticPageEditorUiState.Saved -> current.page
        else -> null
    }
    var slug by remember(existing?.id) { mutableStateOf(existing?.slug?.value.orEmpty()) }
    var title by remember(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var body by remember(existing?.id) { mutableStateOf(existing?.bodyHtml?.rawHtml.orEmpty()) }
    var active by remember(existing?.id) { mutableStateOf(existing?.active ?: true) }
    var sortOrder by remember(existing?.id) { mutableStateOf(existing?.sortOrder?.toString() ?: "0") }

    AdminThinScaffold(if (pageId == null) "ساخت صفحه" else "ویرایش صفحه", onBack, modifier) {
        if (state is AdminStaticPageEditorUiState.Loading) CircularProgressIndicator()
        AdminFormCard {
            AdminTextField("نشانی", slug, { slug = it }, required = true, ltr = true)
            AdminTextField("عنوان", title, { title = it }, required = true)
            AdminMultilineField("محتوای HTML", body, { body = it }, showToolbar = true)
            AdminTextField("ترتیب", sortOrder, { sortOrder = it.filter(Char::isDigit) })
            AdminToggleRow("فعال", active, { active = it })
            AdminPrimaryButton(
                label = if ((state as? AdminStaticPageEditorUiState.Editing)?.saving == true) {
                    "در حال ذخیره…"
                } else {
                    "ذخیره"
                },
                onClick = {
                    val page = existing
                    if (page == null) {
                        viewModel.create(
                            CreateStaticPageCommand(
                                slug = StaticPageSlug(slug),
                                title = title,
                                bodyHtml = HtmlContent(body),
                                active = active,
                                sortOrder = sortOrder.toIntOrNull() ?: 0,
                            ),
                        )
                    } else {
                        viewModel.update(
                            UpdateStaticPageCommand(
                                id = page.id,
                                slug = StaticPageSlug(slug),
                                title = title,
                                bodyHtml = HtmlContent(body),
                                active = active,
                                sortOrder = sortOrder.toIntOrNull() ?: 0,
                            ),
                        )
                    }
                },
            )
            if (state is AdminStaticPageEditorUiState.Saved) {
                VitranText("صفحه ذخیره شد.", VitranTextStyle.Body)
            }
            (state as? AdminStaticPageEditorUiState.Error)?.let {
                VitranText(it.error.message ?: "ذخیره انجام نشد", VitranTextStyle.Body)
            }
        }
    }
}
