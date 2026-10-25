package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.presentation.CreateStoreLocationUiState
import com.vitran.shop.feature.location.presentation.CreateStoreLocationViewModel
import com.vitran.shop.feature.seller.shop.presentation.CreateShopUiEffect
import com.vitran.shop.feature.seller.shop.presentation.CreateShopViewModel
import com.vitran.shop.feature.seller.shop.presentation.SlugCheckUiStatus
import com.vitran.shop.feature.seller.shop.presentation.TitleCheckUiStatus
import com.vitran.shop.feature.seller.shop.presentation.buildCreateShopCommand
import com.vitran.shop.feature.seller.shop.presentation.toRules
import com.vitran.shop.feature.seller.shop.presentation.toShippingMethods
import com.vitran.shop.feature.seller.shop.presentation.toWorkingHours
import com.vitran.shop.ui.sections.admin.AutosaveStatus
import com.vitran.shop.ui.sections.admin.CreateStoreFormState
import com.vitran.shop.ui.sections.admin.CreateStoreStep
import com.vitran.shop.ui.sections.admin.CreateStoreStepOrder
import com.vitran.shop.ui.sections.admin.CreateStoreWizardFrame
import com.vitran.shop.ui.sections.admin.StoreSocialKind
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerUiState
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerViewModel
import com.vitran.shop.ui.sections.reference.toAdminTaxonomyNodes
import com.vitran.shop.ui.sections.reference.toAdminSelectOptions
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_field_city_required
import vitranshop.shared.generated.resources.admin_profile_webhook_required
import vitranshop.shared.generated.resources.admin_last_saved_just_now
import vitranshop.shared.generated.resources.admin_last_saved_minutes

/**
 * Merchant admin — create a store. Route `/admin/stores/new`.
 *
 * Wizard + live phone preview. Wired to [CreateShopViewModel] for Phase 7 create.
 */
@Composable
fun CreateStoreScreen(
    onBack: () -> Unit,
    onViewStore: (String) -> Unit = {},
    onAddProduct: () -> Unit = {},
    onShopCreated: () -> Unit = onBack,
    modifier: Modifier = Modifier,
    locationViewModel: CreateStoreLocationViewModel = vitranKoinViewModel(),
    createShopViewModel: CreateShopViewModel = vitranKoinViewModel(),
    taxonomyViewModel: TaxonomyPickerViewModel = vitranKoinViewModel(),
) {
    var state by remember { mutableStateOf(CreateStoreFormState()) }
    var step by remember { mutableStateOf(CreateStoreStep.Basics) }
    var autosaveStatus by remember { mutableStateOf(AutosaveStatus.Idle) }
    var saveTick by remember { mutableIntStateOf(0) }
    val justNow = stringResource(Res.string.admin_last_saved_just_now)
    val minutesAgo = stringResource(Res.string.admin_last_saved_minutes)
    var lastSavedLabel by remember { mutableStateOf<String?>(null) }
    val locationState by locationViewModel.uiState.collectAsStateWithLifecycle()
    val createState by createShopViewModel.uiState.collectAsStateWithLifecycle()
    val taxonomyState by taxonomyViewModel.uiState.collectAsStateWithLifecycle()
    val taxonomyRoots = when (val current = taxonomyState) {
        is TaxonomyPickerUiState.Content -> current.roots.toAdminTaxonomyNodes()
        else -> emptyList()
    }
    val taxonomyLoading = taxonomyState is TaxonomyPickerUiState.Loading
    val taxonomyError = (taxonomyState as? TaxonomyPickerUiState.Error)?.message
    val cityOptions = when (val current = locationState) {
        is CreateStoreLocationUiState.Content -> current.cities.toAdminSelectOptions()
        else -> emptyList()
    }
    val citiesLoading = locationState is CreateStoreLocationUiState.Loading
    val citiesError = (locationState as? CreateStoreLocationUiState.Error)?.message
    val cityRequired = stringResource(Res.string.admin_field_city_required)
    val webhookRequired = stringResource(Res.string.admin_profile_webhook_required)

    LaunchedEffect(state.slug) {
        createShopViewModel.onSlugInputChanged(state.slug)
    }

    LaunchedEffect(state.storeName) {
        createShopViewModel.onTitleInputChanged(state.storeName)
    }

    LaunchedEffect(createShopViewModel) {
        createShopViewModel.effects.collect { effect ->
            when (effect) {
                is CreateShopUiEffect.ShopCreated -> onShopCreated()
            }
        }
    }

    LaunchedEffect(createState.createdShop) {
        val shop = createState.createdShop ?: return@LaunchedEffect
        state = state.copy(published = true, dirty = false)
        if (shop.slug.value.isNotBlank()) {
            state = state.copy(slug = shop.slug.value)
        }
        autosaveStatus = AutosaveStatus.Saved
        lastSavedLabel = justNow
    }

    LaunchedEffect(state) {
        if (!state.dirty) return@LaunchedEffect
        autosaveStatus = AutosaveStatus.Saving
        delay(800)
        state = state.copy(dirty = false)
        autosaveStatus = AutosaveStatus.Saved
        lastSavedLabel = justNow
        saveTick += 1
    }

    LaunchedEffect(saveTick) {
        if (saveTick == 0) return@LaunchedEffect
        delay(120_000)
        lastSavedLabel = minutesAgo
    }

    fun goTo(target: CreateStoreStep) {
        step = target
    }

    fun goNext() {
        val index = CreateStoreStepOrder.indexOf(step)
        if (index < CreateStoreStepOrder.lastIndex) {
            step = CreateStoreStepOrder[index + 1]
        }
    }

    fun goPrev() {
        val index = CreateStoreStepOrder.indexOf(step)
        if (index > 0) {
            step = CreateStoreStepOrder[index - 1]
        } else {
            onBack()
        }
    }

    fun saveDraft() {
        state = state.copy(dirty = false)
        autosaveStatus = AutosaveStatus.Saved
        lastSavedLabel = justNow
        saveTick += 1
    }

    fun publish() {
        if (!state.canPublish || createState.isSubmitting) return
        if (
            createState.slugCheck is SlugCheckUiStatus.Taken ||
            createState.titleCheck is TitleCheckUiStatus.Taken
        ) {
            step = CreateStoreStep.Basics
            return
        }
        val cityIdValue = state.cityId?.toLongOrNull()
        if (cityIdValue == null) {
            createShopViewModel.reportLocalFieldError("city_id", cityRequired)
            step = CreateStoreStep.Contact
            return
        }
        if (state.profile.fulfillmentType == "api" && state.profile.webhookUrl.isBlank()) {
            createShopViewModel.reportLocalFieldError("webhook_url", webhookRequired)
            step = CreateStoreStep.Policies
            return
        }
        val social = state.socialChannels.associate { it.kind to it.handle }
        val rules = state.profile.toRules()
        val shippingMethods = state.profile.toShippingMethods()
        val command =
            buildCreateShopCommand(
                title = state.storeName,
                slug = state.slug,
                omitSlug = false,
                description = state.about,
                address = state.address,
                phoneNumber = state.phone,
                cityId = CityId(cityIdValue),
                whatsapp = social[StoreSocialKind.WhatsApp],
                telegram = social[StoreSocialKind.Telegram],
                instagram = social[StoreSocialKind.Instagram],
                website = social[StoreSocialKind.Website],
                bale = social[StoreSocialKind.Bale],
                rubika = social[StoreSocialKind.Rubika],
                tiktok = social[StoreSocialKind.TikTok],
                supportTimes = state.supportTimes,
                categorySlugs = state.categorySlugs,
                avatarUrl = state.avatarUrl,
                seoTitle = state.profile.seoTitle,
                seoDescription = state.profile.seoDescription,
                noindex = state.profile.noindex.takeIf { it },
                rules = rules,
                shippingMethods = shippingMethods,
                workingHours = state.profile.toWorkingHours(),
                fulfillmentType = state.profile.fulfillmentType,
                webhookUrl = state.profile.webhookUrl,
            )
        createShopViewModel.submit(command)
    }

    val canPublish =
        state.canPublish &&
            state.cityId?.toLongOrNull() != null &&
            !createState.isSubmitting &&
            createState.slugCheck !is SlugCheckUiStatus.Taken &&
            createState.titleCheck !is TitleCheckUiStatus.Taken

    CreateStoreWizardFrame(
        state = state,
        step = step,
        onStepClick = { goTo(it) },
        onStateChange = { state = it },
        onBack = onBack,
        modifier = modifier,
        autosaveStatus = autosaveStatus,
        cityOptions = cityOptions,
        citiesLoading = citiesLoading,
        citiesError = citiesError,
        onCitiesRetry = locationViewModel::retry,
        onViewStore = {
            if (state.slug.isNotBlank()) onViewStore(state.slug)
        },
        onPublish = { publish() },
        onAddProduct = onAddProduct,
        fieldErrors = createState.fieldErrors,
        generalError = createState.generalError?.message,
        onClearFieldError = createShopViewModel::clearFieldError,
        slugCheck = createState.slugCheck,
        titleCheck = createState.titleCheck,
        taxonomyRoots = taxonomyRoots,
        taxonomyLoading = taxonomyLoading,
        taxonomyError = taxonomyError,
        onTaxonomyRetry = taxonomyViewModel::retry,
        lastSavedLabel = lastSavedLabel,
        canPublish = canPublish,
        published = state.published || createState.createdShop != null,
        onBottomBack = { goPrev() },
        onBottomNext = { goNext() },
        onSaveDraft = { saveDraft() },
    )
}

@Preview
@Composable
private fun CreateStoreScreenPreview() {
    VitranTheme {
        // Preview uses local form state only when Koin is unavailable;
        // production entry points inject ViewModels via vitranKoinViewModel.
        CreateStoreScreen(onBack = {})
    }
}
