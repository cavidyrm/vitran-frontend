package com.vitran.shop.ui.sections.admin

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.feature.seller.shop.presentation.SlugCheckUiStatus
import com.vitran.shop.feature.seller.shop.presentation.TitleCheckUiStatus
import com.vitran.shop.ui.components.admin.AdminSelectOption
import com.vitran.shop.ui.components.admin.AdminTaxonomyNode
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing

/**
 * Create-store wizard chrome: header, stepper, form, phone preview.
 * Create stays editable. Admin review passes [readOnly] and hides the bottom bar.
 * Seller edit keeps the bar and swaps the last action for save.
 */
@Composable
fun CreateStoreWizardFrame(
    state: CreateStoreFormState,
    step: CreateStoreStep,
    onStepClick: (CreateStoreStep) -> Unit,
    onStateChange: (CreateStoreFormState) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    headerTitle: String? = null,
    headerSubtitle: String? = null,
    headerTrailing: @Composable () -> Unit = {},
    autosaveStatus: AutosaveStatus = AutosaveStatus.Idle,
    cityOptions: List<AdminSelectOption> = emptyList(),
    citiesLoading: Boolean = false,
    citiesError: String? = null,
    onCitiesRetry: () -> Unit = {},
    onViewStore: () -> Unit = {},
    onPublish: () -> Unit = {},
    onAddProduct: () -> Unit = {},
    fieldErrors: Map<String, String> = emptyMap(),
    generalError: String? = null,
    banner: String? = null,
    onClearFieldError: (String) -> Unit = {},
    slugCheck: SlugCheckUiStatus = SlugCheckUiStatus.Idle,
    titleCheck: TitleCheckUiStatus = TitleCheckUiStatus.Idle,
    taxonomyRoots: List<AdminTaxonomyNode> = emptyList(),
    taxonomyLoading: Boolean = false,
    taxonomyError: String? = null,
    onTaxonomyRetry: () -> Unit = {},
    lastSavedLabel: String? = null,
    showBottomBar: Boolean = !readOnly,
    canPublish: Boolean = false,
    published: Boolean = false,
    onBottomBack: () -> Unit = {},
    onBottomNext: () -> Unit = {},
    onSaveDraft: () -> Unit = {},
    lastStepLabel: String? = null,
    lastStepEnabled: Boolean = canPublish && !published,
    belowForm: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AdminTokens.PageBackground),
    ) {
        CreateStoreHeaderBar(
            autosaveStatus = autosaveStatus,
            onBack = onBack,
            title = headerTitle,
            subtitle = headerSubtitle,
            trailing = headerTrailing,
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
                    onStepClick = onStepClick,
                    readOnly = readOnly,
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
                            verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
                        ) {
                            WizardBanner(banner)
                            CreateStoreStepBody(
                                step = step,
                                state = state,
                                onStateChange = onStateChange,
                                cityOptions = cityOptions,
                                citiesLoading = citiesLoading,
                                citiesError = citiesError,
                                onCitiesRetry = onCitiesRetry,
                                onViewStore = onViewStore,
                                onPublish = onPublish,
                                onAddProduct = onAddProduct,
                                fieldErrors = fieldErrors,
                                generalError = generalError,
                                onClearFieldError = onClearFieldError,
                                slugCheck = slugCheck,
                                titleCheck = titleCheck,
                                taxonomyRoots = taxonomyRoots,
                                taxonomyLoading = taxonomyLoading,
                                taxonomyError = taxonomyError,
                                onTaxonomyRetry = onTaxonomyRetry,
                                readOnly = readOnly,
                            )
                            belowForm()
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
                                onVisit = onViewStore,
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
                            onVisit = onViewStore,
                        )
                        WizardBanner(banner)
                        CreateStoreStepBody(
                            step = step,
                            state = state,
                            onStateChange = onStateChange,
                            cityOptions = cityOptions,
                            citiesLoading = citiesLoading,
                            citiesError = citiesError,
                            onCitiesRetry = onCitiesRetry,
                            onViewStore = onViewStore,
                            onPublish = onPublish,
                            onAddProduct = onAddProduct,
                            fieldErrors = fieldErrors,
                            generalError = generalError,
                            onClearFieldError = onClearFieldError,
                            slugCheck = slugCheck,
                            titleCheck = titleCheck,
                            taxonomyRoots = taxonomyRoots,
                            taxonomyLoading = taxonomyLoading,
                            taxonomyError = taxonomyError,
                            onTaxonomyRetry = onTaxonomyRetry,
                            readOnly = readOnly,
                        )
                        belowForm()
                        CreateStoreSummaryCard(
                            state = state,
                            currentStep = step,
                            lastSavedLabel = lastSavedLabel,
                        )
                    }
                }
            }
        }
        if (showBottomBar) {
            CreateStoreBottomBar(
                step = step,
                brandColor = state.theme.primary,
                onBrandColor = state.theme.onPrimary,
                lastSavedLabel = lastSavedLabel,
                canPublish = canPublish,
                published = published,
                onBack = onBottomBack,
                onNext = onBottomNext,
                onSaveDraft = onSaveDraft,
                onPublish = onPublish,
                lastStepLabel = lastStepLabel,
                lastStepEnabled = lastStepEnabled,
            )
        }
    }
}

@Composable
private fun WizardBanner(banner: String?) {
    if (banner.isNullOrBlank()) return
    Text(
        text = banner,
        color = AdminTokens.Success,
        fontSize = 13.sp,
        modifier = Modifier.fillMaxWidth(),
    )
}
