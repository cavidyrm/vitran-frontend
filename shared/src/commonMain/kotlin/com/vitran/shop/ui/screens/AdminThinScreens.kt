package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.TaxonomyBrowseUiState
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.TaxonomyBrowseViewModel
import com.vitran.shop.feature.admin.catalog.taxonomy.presentation.TaxonomyImportViewModel
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
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.components.admin.AdminMultilineField
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.components.admin.AdminToggleRow
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.sections.admin.plan.StorePlanTokens
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.di.vitranKoinViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_chevron_right

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
    browseViewModel: TaxonomyBrowseViewModel = vitranKoinViewModel(),
    importViewModel: TaxonomyImportViewModel = vitranKoinViewModel(),
    imagePicker: ImagePicker = koinInject(),
) {
    val browseState by browseViewModel.uiState.collectAsStateWithLifecycle()
    val importState by importViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(importState.imported) {
        if (importState.imported) browseViewModel.retry()
    }
    var categoriesJson by remember { mutableStateOf("") }
    var attributesJson by remember { mutableStateOf("") }
    var importOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AdminThinScaffold("مدیریت طبقه‌بندی", onBack, modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AdminTokens.ProductFormMaxWidth)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            TaxonomyCategoryBrowser(
                state = browseState,
                onRetry = browseViewModel::retry,
                onOpenCrumb = browseViewModel::openCrumb,
                onOpen = browseViewModel::open,
                onBeginEdit = browseViewModel::beginEdit,
                onEditName = browseViewModel::setEditName,
                onSaveName = browseViewModel::saveName,
                onPickIcon = {
                    scope.launch {
                        imagePicker.pickImages(1).firstOrNull()?.let(browseViewModel::uploadIcon)
                    }
                },
            )
            AdminFormCard(
                title = "ورود طبقه‌بندی",
                subtitle = "فایل JSON شاپی‌فای را فقط وقتی لازم است باز کنید.",
                trailing = {
                    AdminSecondaryButton(
                        label = if (importOpen) "بستن" else "باز کردن",
                        onClick = { importOpen = !importOpen },
                    )
                },
            ) {
                if (importOpen) {
                    AdminMultilineField("دسته‌بندی‌ها", categoriesJson, { categoriesJson = it })
                    AdminMultilineField("ویژگی‌ها", attributesJson, { attributesJson = it })
                    AdminToggleRow("اطلاعات را بررسی کرده‌ام", importState.isConfirmed, importViewModel::setConfirmed)
                    AdminPrimaryButton(
                        label = if (importState.isSubmitting) "در حال ورود…" else "ورود اطلاعات",
                        onClick = {
                            importViewModel.setCategoriesFile(
                                SelectedFile.fromBytes(
                                    "categories.json",
                                    categoriesJson.encodeToByteArray(),
                                    "application/json",
                                ),
                            )
                            importViewModel.setAttributesFile(
                                SelectedFile.fromBytes(
                                    "attributes.json",
                                    attributesJson.encodeToByteArray(),
                                    "application/json",
                                ),
                            )
                            importViewModel.import()
                        },
                    )
                }
                if (importState.imported) {
                    VitranText("طبقه‌بندی وارد شد.", VitranTextStyle.Body, color = AdminTokens.Success)
                }
                importState.error?.let { VitranText(it.message ?: "ورود انجام نشد", VitranTextStyle.Body) }
            }
        }
    }
}

@Composable
private fun TaxonomyCategoryBrowser(
    state: TaxonomyBrowseUiState,
    onRetry: () -> Unit,
    onOpenCrumb: (Int) -> Unit,
    onOpen: (CategorySlug) -> Unit,
    onBeginEdit: (CategorySlug) -> Unit,
    onEditName: (String) -> Unit,
    onSaveName: () -> Unit,
    onPickIcon: () -> Unit,
) {
    val levelCount = state.levelNodes.size
    AdminFormCard(
        title = "دسته‌بندی‌ها",
        subtitle = if (state.loading && state.roots.isEmpty()) {
            "در حال دریافت…"
        } else {
            "${toPersianDigits(levelCount)} دسته در این سطح"
        },
    ) {
        TaxonomyCrumbBar(
            pathNodes = state.pathNodes,
            onOpenCrumb = onOpenCrumb,
        )
        val loadError = state.loadError
        val listShape = RoundedCornerShape(AdminTokens.FieldRadius)
        when {
            state.loading && state.roots.isEmpty() ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = VitranSpacing.xxl),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AdminTokens.Brand)
                }
            loadError != null && state.roots.isEmpty() ->
                Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                    VitranText(loadError.message ?: "دریافت دسته‌ها انجام نشد", VitranTextStyle.Body)
                    AdminSecondaryButton("تلاش دوباره", onRetry)
                }
            state.unlistedChildren ->
                TaxonomyNotice("زیر‌دسته‌های این سطح در پاسخ فهرست نیامده‌اند.")
            state.levelNodes.isEmpty() ->
                TaxonomyNotice("دسته‌ای ثبت نشده است.")
            else ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(listShape)
                        .border(1.dp, AdminTokens.CardBorder, listShape),
                ) {
                    state.levelNodes.forEachIndexed { index, node ->
                        if (index > 0) {
                            HorizontalDivider(thickness = VitranSize.borderHairline, color = AdminTokens.CardBorder)
                        }
                        val canOpen = node.children.isNotEmpty() || !node.isLeaf
                        val editing = state.editingSlug == node.slug
                        TaxonomyLevelRow(
                            name = node.displayName,
                            sourceTitle = node.sourceTitle,
                            slug = node.slug.value,
                            leaf = node.isLeaf,
                            canOpen = canOpen,
                            selected = editing,
                            onOpen = { onOpen(node.slug) },
                            onEdit = { onBeginEdit(node.slug) },
                        )
                        if (editing) {
                            TaxonomyEditPanel(
                                state = state,
                                onEditName = onEditName,
                                onSaveName = onSaveName,
                                onPickIcon = onPickIcon,
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun TaxonomyCrumbBar(
    pathNodes: List<CategoryNode>,
    onOpenCrumb: (Int) -> Unit,
) {
    val shape = RoundedCornerShape(AdminTokens.FieldRadius)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AdminTokens.NestedPanel)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TaxonomyCrumb(
            label = "همه دسته‌ها",
            current = pathNodes.isEmpty(),
            onClick = { onOpenCrumb(0) },
        )
        pathNodes.forEachIndexed { index, node ->
            VitranText("‹", VitranTextStyle.Label, color = AdminTokens.Placeholder)
            TaxonomyCrumb(
                label = node.displayName,
                current = index == pathNodes.lastIndex,
                onClick = { onOpenCrumb(index + 1) },
            )
        }
    }
}

@Composable
private fun TaxonomyCrumb(
    label: String,
    current: Boolean,
    onClick: () -> Unit,
) {
    VitranText(
        text = label,
        style = VitranTextStyle.Label,
        color = if (current) MaterialTheme.colorScheme.onSurface else AdminTokens.Brand,
        modifier = Modifier.clickable(role = Role.Button, enabled = !current, onClick = onClick),
    )
}

@Composable
private fun TaxonomyLevelRow(
    name: String,
    sourceTitle: String,
    slug: String,
    leaf: Boolean,
    canOpen: Boolean,
    selected: Boolean,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) AdminTokens.DropdownHover else MaterialTheme.colorScheme.surface)
            .clickable(enabled = canOpen, role = Role.Button, onClick = onOpen)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(name, VitranTextStyle.Title, maxLines = 1)
            VitranText(
                text = if (sourceTitle == name) slug else "$sourceTitle  ·  $slug",
                style = VitranTextStyle.Label,
                color = AdminTokens.Helper,
                maxLines = 1,
            )
        }
        TaxonomyKindBadge(leaf = leaf)
        VitranText(
            text = "ویرایش",
            style = VitranTextStyle.Label,
            color = AdminTokens.Brand,
            modifier = Modifier.clickable(role = Role.Button, onClick = onEdit),
        )
        if (canOpen) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = "زیر‌دسته‌ها",
                size = VitranSize.iconSmall,
                tint = AdminTokens.Helper,
                modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            )
        }
    }
}

@Composable
private fun TaxonomyKindBadge(leaf: Boolean) {
    val color = if (leaf) AdminTokens.Success else AdminTokens.Brand
    val shape = RoundedCornerShape(999.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = VitranSpacing.sm, vertical = VitranSpacing.xs),
    ) {
        VitranText(
            text = if (leaf) "برگ" else "شاخه",
            style = VitranTextStyle.Label,
            color = color,
        )
    }
}

@Composable
private fun TaxonomyEditPanel(
    state: TaxonomyBrowseUiState,
    onEditName: (String) -> Unit,
    onSaveName: () -> Unit,
    onPickIcon: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AdminTokens.NestedPanel)
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        VitranText("ویرایش نام و آیکن", VitranTextStyle.Title)
        AdminTextField(
            label = "نام فارسی",
            value = state.editName,
            onValueChange = onEditName,
            helper = state.editTitle.takeIf { it.isNotBlank() },
        )
        state.editIconUrl?.let {
            VitranText(it, VitranTextStyle.Label, color = AdminTokens.Helper, maxLines = 1)
        }
        if (state.editLoading) {
            VitranText("در حال دریافت جزئیات…", VitranTextStyle.Label, color = AdminTokens.Helper)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            AdminPrimaryButton(
                label = if (state.saving) "در حال ذخیره…" else "ذخیره نام",
                onClick = onSaveName,
                enabled = !state.saving && !state.editLoading,
            )
            AdminSecondaryButton(
                label = "انتخاب آیکن",
                onClick = onPickIcon,
                enabled = !state.saving && !state.editLoading,
            )
        }
        if (state.nameSaved) {
            VitranText("نام ذخیره شد.", VitranTextStyle.Label, color = AdminTokens.Success)
        }
        if (state.iconSaved) {
            VitranText("آیکن ذخیره شد.", VitranTextStyle.Label, color = AdminTokens.Success)
        }
        state.editError?.let { VitranText(it.message ?: "ذخیره انجام نشد", VitranTextStyle.Body) }
    }
}

@Composable
private fun TaxonomyNotice(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = VitranSpacing.xxl),
        contentAlignment = Alignment.Center,
    ) {
        VitranText(message, VitranTextStyle.Body, color = AdminTokens.Helper)
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
