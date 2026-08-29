package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.location.presentation.CreateStoreLocationUiState
import com.vitran.shop.feature.location.presentation.CreateStoreLocationViewModel
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.admin.AutosaveStatus
import com.vitran.shop.ui.sections.admin.CreateStoreBottomBar
import com.vitran.shop.ui.sections.admin.CreateStoreFormState
import com.vitran.shop.ui.sections.admin.CreateStoreHeaderBar
import com.vitran.shop.ui.sections.admin.CreateStorePhonePreview
import com.vitran.shop.ui.sections.admin.CreateStoreStep
import com.vitran.shop.ui.sections.admin.CreateStoreStepBody
import com.vitran.shop.ui.sections.admin.CreateStoreStepOrder
import com.vitran.shop.ui.sections.admin.CreateStoreStepper
import com.vitran.shop.ui.sections.reference.toAdminSelectOptions
import com.vitran.shop.ui.sections.admin.CreateStoreSummaryCard
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_last_saved_just_now
import vitranshop.shared.generated.resources.admin_last_saved_minutes

/**
 * Merchant admin — create a store. Route `/admin/stores/new`.
 *
 * Wizard + live phone preview. Mock form state only.
 */
@Composable
fun CreateStoreScreen(
    onBack: () -> Unit,
    onViewStore: (String) -> Unit = {},
    onAddProduct: () -> Unit = {},
    modifier: Modifier = Modifier,
    locationViewModel: CreateStoreLocationViewModel = vitranKoinViewModel(),
) {
    var state by remember { mutableStateOf(CreateStoreFormState()) }
    var step by remember { mutableStateOf(CreateStoreStep.Basics) }
    var autosaveStatus by remember { mutableStateOf(AutosaveStatus.Idle) }
    var saveTick by remember { mutableIntStateOf(0) }
    val justNow = stringResource(Res.string.admin_last_saved_just_now)
    val minutesAgo = stringResource(Res.string.admin_last_saved_minutes)
    var lastSavedLabel by remember { mutableStateOf<String?>(null) }
    val locationState by locationViewModel.uiState.collectAsStateWithLifecycle()
    val cityOptions = when (val current = locationState) {
        is CreateStoreLocationUiState.Content -> current.cities.toAdminSelectOptions()
        else -> emptyList()
    }
    val citiesLoading = locationState is CreateStoreLocationUiState.Loading
    val citiesError = (locationState as? CreateStoreLocationUiState.Error)?.message

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
        if (!state.canPublish) return
        state = state.copy(dirty = false, published = true)
        autosaveStatus = AutosaveStatus.Saved
        lastSavedLabel = justNow
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AdminTokens.PageBackground),
    ) {
        CreateStoreHeaderBar(
            autosaveStatus = autosaveStatus,
            onBack = onBack,
            modifier = Modifier.drawBehind {
                drawLine(
                    color = AdminTokens.CardBorder,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            },
        )
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val twoColumn = maxWidth >= VitranSize.mdBreakpoint
            val horizontalPad = if (twoColumn) VitranSpacing.xxl else VitranSpacing.lg
            Column(modifier = Modifier.fillMaxSize()) {
                CreateStoreStepper(
                    current = step,
                    state = state,
                    onStepClick = { goTo(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = horizontalPad),
                )
                if (twoColumn) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPad)
                            .padding(top = AdminTokens.CardGap),
                        horizontalArrangement = Arrangement.spacedBy(
                            AdminTokens.CardGap,
                            Alignment.CenterHorizontally,
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .widthIn(max = AdminTokens.FormMaxWidth)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = VitranSpacing.xxl),
                        ) {
                            CreateStoreStepBody(
                                step = step,
                                state = state,
                                onStateChange = { state = it },
                                cityOptions = cityOptions,
                                citiesLoading = citiesLoading,
                                citiesError = citiesError,
                                onCitiesRetry = locationViewModel::retry,
                                onViewStore = {
                                    if (state.slug.isNotBlank()) onViewStore(state.slug)
                                },
                                onPublish = { publish() },
                                onAddProduct = onAddProduct,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .width(AdminTokens.RailWidth)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = VitranSpacing.xxl),
                            verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            CreateStorePhonePreview(
                                state = state,
                                onVisit = {
                                    if (state.slug.isNotBlank()) onViewStore(state.slug)
                                },
                            )
                            CreateStoreSummaryCard(
                                state = state,
                                currentStep = step,
                                lastSavedLabel = lastSavedLabel,
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = horizontalPad)
                            .padding(top = VitranSpacing.md, bottom = VitranSpacing.xxl),
                        verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CreateStorePhonePreview(
                            state = state,
                            onVisit = {
                                if (state.slug.isNotBlank()) onViewStore(state.slug)
                            },
                        )
                        CreateStoreStepBody(
                            step = step,
                            state = state,
                            onStateChange = { state = it },
                            cityOptions = cityOptions,
                            citiesLoading = citiesLoading,
                            citiesError = citiesError,
                            onCitiesRetry = locationViewModel::retry,
                            onViewStore = {
                                if (state.slug.isNotBlank()) onViewStore(state.slug)
                            },
                            onPublish = { publish() },
                            onAddProduct = onAddProduct,
                        )
                        CreateStoreSummaryCard(
                            state = state,
                            currentStep = step,
                            lastSavedLabel = lastSavedLabel,
                        )
                    }
                }
            }
        }
        CreateStoreBottomBar(
            step = step,
            brandColor = state.theme.primary,
            onBrandColor = state.theme.onPrimary,
            lastSavedLabel = lastSavedLabel,
            canPublish = state.canPublish,
            published = state.published,
            onBack = { goPrev() },
            onNext = { goNext() },
            onSaveDraft = { saveDraft() },
            onPublish = { publish() },
        )
    }
}

@Preview
@Composable
private fun CreateStoreScreenPreview() {
    VitranTheme {
        CreateStoreScreen(onBack = {})
    }
}
