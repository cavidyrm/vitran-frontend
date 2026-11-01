package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.admin.home.domain.AdminHomeBanner
import com.vitran.shop.feature.admin.home.domain.AdminHomeSection
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeBannerCommand
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeSectionCommand
import com.vitran.shop.feature.admin.home.presentation.AdminHomeAccess
import com.vitran.shop.feature.admin.home.presentation.AdminHomeSectionEditorViewModel
import com.vitran.shop.feature.admin.home.presentation.AdminHomeSectionsViewModel
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.components.admin.AdminSelectField
import com.vitran.shop.ui.components.admin.AdminSelectOption
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.components.admin.AdminToggleRow
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.ui.theme.VitranSpacing
import org.koin.core.parameter.parametersOf

@Composable
fun AdminHomeSectionsScreen(
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminHomeSectionsViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AdminThinScaffold(title = "چیدمان صفحه اصلی", onBack = onBack, modifier = modifier) {
        when (state.access) {
            AdminHomeAccess.Pending -> CircularProgressIndicator()
            AdminHomeAccess.Denied -> VitranText(
                "دسترسی به این صفحه ندارید.",
                VitranTextStyle.Body,
            )
            AdminHomeAccess.Allowed -> {
                Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                    AdminPrimaryButton(label = "بخش جدید", onClick = onCreate)
                    AdminSecondaryButton(label = "به‌روزرسانی", onClick = viewModel::refresh)
                }
                state.error?.message?.let { VitranText(it, VitranTextStyle.Body) }
                if (state.loading && state.sections.isEmpty()) {
                    CircularProgressIndicator()
                }
                state.sections.forEach { section ->
                    AdminFormCard(
                        title = section.title.ifBlank { section.slug },
                        subtitle = "${section.slug} · ${section.source}",
                    ) {
                        VitranText(
                            if (section.active) "فعال" else "غیرفعال",
                            VitranTextStyle.Label,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                            AdminSecondaryButton(
                                label = "ویرایش",
                                onClick = { onEdit(section.id) },
                            )
                            if (state.canDeleteSection) {
                                AdminSecondaryButton(
                                    label = if (section.id in state.deletingIds) "در حال حذف" else "حذف",
                                    enabled = section.id !in state.deletingIds,
                                    onClick = { viewModel.delete(section.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminHomeSectionEditorScreen(
    sectionId: Long?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminHomeSectionEditorViewModel = vitranKoinViewModel { parametersOf(sectionId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var form by remember { mutableStateOf(HomeSectionForm()) }
    var boundSectionId by remember { mutableStateOf<Long?>(null) }
    var bannerForm by remember { mutableStateOf(HomeBannerForm()) }
    var boundBannerId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(state.section?.id) {
        val section = state.section ?: return@LaunchedEffect
        if (boundSectionId != section.id) {
            form = section.toForm()
            boundSectionId = section.id
        }
    }
    LaunchedEffect(state.editingBanner?.id) {
        val banner = state.editingBanner
        if (banner == null) {
            if (boundBannerId != null) {
                bannerForm = HomeBannerForm()
                boundBannerId = null
            }
        } else if (boundBannerId != banner.id) {
            bannerForm = banner.toForm()
            boundBannerId = banner.id
        }
    }
    AdminThinScaffold(
        title = if (sectionId == null && state.section == null) "بخش جدید" else "ویرایش بخش",
        onBack = onBack,
        modifier = modifier,
    ) {
        when (state.access) {
            AdminHomeAccess.Pending -> CircularProgressIndicator()
            AdminHomeAccess.Denied -> VitranText(
                "دسترسی به این صفحه ندارید.",
                VitranTextStyle.Body,
            )
            AdminHomeAccess.Allowed -> {
                if (sectionId != null && state.section == null) {
                    if (state.loading) CircularProgressIndicator()
                    state.error?.message?.let { VitranText(it, VitranTextStyle.Body) }
                    if (!state.loading && state.error != null) {
                        AdminSecondaryButton(label = "تلاش دوباره", onClick = viewModel::load)
                    }
                    return@AdminThinScaffold
                }
                state.error?.message?.let { VitranText(it, VitranTextStyle.Body) }
                state.message?.let { VitranText(it, VitranTextStyle.Body) }
                HomeSectionFormCard(
                    form = form,
                    slugLocked = state.section != null,
                    saving = state.saving,
                    fieldErrors = state.fieldErrors,
                    onChange = { form = it },
                    onSave = { viewModel.saveSection(form.toCommand()) },
                )
                if (state.section?.source == "banners") {
                    HomeBannersCard(
                        banners = state.banners,
                        form = bannerForm,
                        editingId = state.editingBanner?.id,
                        saving = state.saving,
                        deletingIds = state.deletingBannerIds,
                        fieldErrors = state.bannerFieldErrors,
                        onChange = { bannerForm = it },
                        onEdit = viewModel::openBanner,
                        onNew = {
                            viewModel.clearBanner()
                            bannerForm = HomeBannerForm()
                            boundBannerId = null
                        },
                        onSave = {
                            viewModel.saveBanner(state.editingBanner?.id, bannerForm.toCommand())
                        },
                        onDelete = viewModel::deleteBanner,
                    )
                } else if (form.source == "banners" && state.section?.source != "banners") {
                    VitranText(
                        "ابتدا بخش را ذخیره کنید تا بنرها را اضافه کنید.",
                        VitranTextStyle.Body,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSectionFormCard(
    form: HomeSectionForm,
    slugLocked: Boolean,
    saving: Boolean,
    fieldErrors: Map<String, String>,
    onChange: (HomeSectionForm) -> Unit,
    onSave: () -> Unit,
) {
    AdminFormCard(title = "بخش", subtitle = "نامک بعد از ساخت ثابت می‌ماند.") {
        AdminTextField(
            label = "نامک",
            value = form.slug,
            onValueChange = { onChange(form.copy(slug = it)) },
            readOnly = slugLocked,
            ltr = true,
            error = fieldErrors["slug"],
            helper = "فقط هنگام ساخت ارسال می‌شود.",
        )
        AdminSelectField(
            label = "منبع",
            valueId = form.source,
            options = sourceOptions,
            onSelect = { onChange(form.copy(source = it.id)) },
            error = fieldErrors["source"],
        )
        AdminSelectField(
            label = "چیدمان",
            valueId = form.layout,
            options = layoutOptions,
            onSelect = { onChange(form.copy(layout = it.id)) },
            error = fieldErrors["layout"],
        )
        AdminTextField(
            label = "عنوان",
            value = form.title,
            onValueChange = { onChange(form.copy(title = it)) },
            error = fieldErrors["title"],
        )
        AdminTextField(
            label = "تعداد آیتم",
            value = form.itemLimit,
            onValueChange = { onChange(form.copy(itemLimit = it)) },
            ltr = true,
            error = fieldErrors["item_limit"],
        )
        AdminTextField(
            label = "ترتیب",
            value = form.sortOrder,
            onValueChange = { onChange(form.copy(sortOrder = it)) },
            ltr = true,
            error = fieldErrors["sort_order"],
        )
        AdminSelectField(
            label = "مخاطب",
            valueId = form.audience,
            options = audienceOptions,
            onSelect = { onChange(form.copy(audience = it.id)) },
            error = fieldErrors["audience"],
        )
        AdminToggleRow(
            label = "فعال",
            checked = form.active,
            onCheckedChange = { onChange(form.copy(active = it)) },
        )
        AdminToggleRow(
            label = "پنهان وقتی خالی است",
            checked = form.hideWhenEmpty,
            onCheckedChange = { onChange(form.copy(hideWhenEmpty = it)) },
        )
        AdminToggleRow(
            label = "iOS",
            checked = form.ios,
            onCheckedChange = { onChange(form.copy(ios = it)) },
        )
        AdminToggleRow(
            label = "Android",
            checked = form.android,
            onCheckedChange = { onChange(form.copy(android = it)) },
        )
        AdminToggleRow(
            label = "وب",
            checked = form.web,
            onCheckedChange = { onChange(form.copy(web = it)) },
        )
        VitranText(
            "اگر هیچ پلتفرمی انتخاب نشود، بخش روی همه پلتفرم‌ها دیده می‌شود.",
            VitranTextStyle.Label,
        )
        AdminTextField(
            label = "حداقل نسخه اپ",
            value = form.minAppVersion,
            onValueChange = { onChange(form.copy(minAppVersion = it)) },
            ltr = true,
            error = fieldErrors["min_app_version"],
            helper = "خالی یعنی بدون محدودیت.",
        )
        AdminTextField(
            label = "شروع",
            value = form.startsAt,
            onValueChange = { onChange(form.copy(startsAt = it)) },
            ltr = true,
            error = fieldErrors["starts_at"],
            helper = "RFC 3339. خالی یعنی باز.",
        )
        AdminTextField(
            label = "پایان",
            value = form.endsAt,
            onValueChange = { onChange(form.copy(endsAt = it)) },
            ltr = true,
            error = fieldErrors["ends_at"],
            helper = "پایان بازه شامل نمی‌شود.",
        )
        AdminPrimaryButton(
            label = if (saving) "در حال ذخیره" else "ذخیره بخش",
            enabled = !saving,
            onClick = onSave,
        )
    }
}

@Composable
private fun HomeBannersCard(
    banners: List<AdminHomeBanner>,
    form: HomeBannerForm,
    editingId: Long?,
    saving: Boolean,
    deletingIds: Set<Long>,
    fieldErrors: Map<String, String>,
    onChange: (HomeBannerForm) -> Unit,
    onEdit: (Long) -> Unit,
    onNew: () -> Unit,
    onSave: () -> Unit,
    onDelete: (Long) -> Unit,
) {
    AdminFormCard(title = "بنرها", subtitle = "آدرس تصویر باید http یا https باشد.") {
        banners.forEach { banner ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    VitranText(banner.title ?: banner.imageUrl, VitranTextStyle.Body)
                    VitranText(banner.actionType, VitranTextStyle.Label)
                }
                AdminSecondaryButton(label = "ویرایش", onClick = { onEdit(banner.id) })
                AdminSecondaryButton(
                    label = if (banner.id in deletingIds) "در حال حذف" else "حذف",
                    enabled = banner.id !in deletingIds,
                    onClick = { onDelete(banner.id) },
                )
            }
        }
        AdminSecondaryButton(
            label = if (editingId == null) "بنر جدید" else "انصراف از ویرایش",
            onClick = onNew,
        )
        AdminTextField(
            label = "آدرس تصویر",
            value = form.imageUrl,
            onValueChange = { onChange(form.copy(imageUrl = it)) },
            ltr = true,
            error = fieldErrors["image_url"],
        )
        AdminTextField(
            label = "عنوان",
            value = form.title,
            onValueChange = { onChange(form.copy(title = it)) },
            error = fieldErrors["title"],
        )
        AdminSelectField(
            label = "اکشن",
            valueId = form.actionType,
            options = actionOptions,
            onSelect = { onChange(form.copy(actionType = it.id)) },
            error = fieldErrors["action_type"],
        )
        if (form.actionType != "none") {
            AdminTextField(
                label = "مقدار اکشن",
                value = form.actionValue,
                onValueChange = { onChange(form.copy(actionValue = it)) },
                ltr = true,
                error = fieldErrors["action_value"],
            )
        }
        AdminTextField(
            label = "ترتیب",
            value = form.sortOrder,
            onValueChange = { onChange(form.copy(sortOrder = it)) },
            ltr = true,
            error = fieldErrors["sort_order"],
        )
        AdminToggleRow(
            label = "فعال",
            checked = form.active,
            onCheckedChange = { onChange(form.copy(active = it)) },
        )
        AdminTextField(
            label = "شروع",
            value = form.startsAt,
            onValueChange = { onChange(form.copy(startsAt = it)) },
            ltr = true,
            error = fieldErrors["starts_at"],
        )
        AdminTextField(
            label = "پایان",
            value = form.endsAt,
            onValueChange = { onChange(form.copy(endsAt = it)) },
            ltr = true,
            error = fieldErrors["ends_at"],
        )
        AdminPrimaryButton(
            label = if (saving) "در حال ذخیره" else if (editingId == null) "افزودن بنر" else "ذخیره بنر",
            enabled = !saving,
            onClick = onSave,
        )
    }
}

private data class HomeSectionForm(
    val slug: String = "",
    val source: String = "featured_products",
    val layout: String = "horizontal_rail",
    val title: String = "",
    val itemLimit: String = "12",
    val sortOrder: String = "0",
    val active: Boolean = true,
    val audience: String = "all",
    val hideWhenEmpty: Boolean = false,
    val ios: Boolean = false,
    val android: Boolean = false,
    val web: Boolean = false,
    val minAppVersion: String = "",
    val startsAt: String = "",
    val endsAt: String = "",
)

private data class HomeBannerForm(
    val imageUrl: String = "",
    val title: String = "",
    val actionType: String = "none",
    val actionValue: String = "",
    val sortOrder: String = "0",
    val active: Boolean = true,
    val startsAt: String = "",
    val endsAt: String = "",
)

private fun AdminHomeSection.toForm() = HomeSectionForm(
    slug = slug,
    source = source,
    layout = layout,
    title = title,
    itemLimit = itemLimit.toString(),
    sortOrder = sortOrder.toString(),
    active = active,
    audience = audience,
    hideWhenEmpty = hideWhenEmpty,
    ios = "ios" in platforms,
    android = "android" in platforms,
    web = "web" in platforms,
    minAppVersion = minAppVersion.orEmpty(),
    startsAt = startsAt.orEmpty(),
    endsAt = endsAt.orEmpty(),
)

private fun AdminHomeBanner.toForm() = HomeBannerForm(
    imageUrl = imageUrl,
    title = title.orEmpty(),
    actionType = actionType,
    actionValue = actionValue.orEmpty(),
    sortOrder = sortOrder.toString(),
    active = active,
    startsAt = startsAt.orEmpty(),
    endsAt = endsAt.orEmpty(),
)

private fun HomeSectionForm.toCommand() = SaveAdminHomeSectionCommand(
    slug = slug.trim(),
    source = source,
    layout = layout,
    title = title.trim(),
    itemLimit = itemLimit.toIntOrNull() ?: 0,
    sortOrder = sortOrder.toIntOrNull() ?: 0,
    active = active,
    audience = audience,
    hideWhenEmpty = hideWhenEmpty,
    platforms = buildList {
        if (ios) add("ios")
        if (android) add("android")
        if (web) add("web")
    },
    minAppVersion = minAppVersion.trim(),
    startsAt = startsAt.trim(),
    endsAt = endsAt.trim(),
)

private fun HomeBannerForm.toCommand() = SaveAdminHomeBannerCommand(
    imageUrl = imageUrl.trim(),
    title = title.trim(),
    actionType = actionType,
    actionValue = actionValue.trim(),
    sortOrder = sortOrder.toIntOrNull() ?: 0,
    active = active,
    startsAt = startsAt.trim(),
    endsAt = endsAt.trim(),
)

private val sourceOptions = listOf(
    AdminSelectOption("featured_products", "محصولات ویژه"),
    AdminSelectOption("personal_products", "برای شما"),
    AdminSelectOption("following_products", "محصولات دنبال‌شده"),
    AdminSelectOption("popular_products", "پربازدید"),
    AdminSelectOption("latest_products", "تازه‌ها"),
    AdminSelectOption("favorite_products", "علاقه‌مندی‌ها"),
    AdminSelectOption("product_matches", "پیشنهاد اندازه"),
    AdminSelectOption("latest_shops", "فروشگاه‌های تازه"),
    AdminSelectOption("following_shops", "فروشگاه‌های دنبال‌شده"),
    AdminSelectOption("favorite_shops", "فروشگاه‌های برتر"),
    AdminSelectOption("categories", "دسته‌بندی‌ها"),
    AdminSelectOption("banners", "بنر"),
)

private val layoutOptions = listOf(
    AdminSelectOption("horizontal_rail", "ریل افقی"),
    AdminSelectOption("grid", "شبکه"),
    AdminSelectOption("chips", "چیپ"),
    AdminSelectOption("hero", "هیرو"),
)

private val audienceOptions = listOf(
    AdminSelectOption("all", "همه"),
    AdminSelectOption("authenticated", "کاربران واردشده"),
    AdminSelectOption("guest", "مهمان"),
)

private val actionOptions = listOf(
    AdminSelectOption("none", "بدون اکشن"),
    AdminSelectOption("open_product", "باز کردن محصول"),
    AdminSelectOption("open_shop", "باز کردن فروشگاه"),
    AdminSelectOption("open_category", "باز کردن دسته‌بندی"),
    AdminSelectOption("open_section", "باز کردن بخش"),
    AdminSelectOption("open_url", "باز کردن پیوند"),
)
