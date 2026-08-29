package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerUiState
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerViewModel
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.admin.CreateCategoryFormState
import com.vitran.shop.ui.sections.admin.CreateCategoryHeaderBar
import com.vitran.shop.ui.sections.admin.CreateCategoryMainColumn
import com.vitran.shop.ui.sections.admin.rememberProductTaxonomy
import com.vitran.shop.ui.sections.reference.ReferenceDataEmpty
import com.vitran.shop.ui.sections.reference.ReferenceDataError
import com.vitran.shop.ui.sections.reference.ReferenceDataLoading
import com.vitran.shop.ui.sections.reference.toAdminTaxonomyNodes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/**
 * Merchant admin — pick a Standard Product Taxonomy node.
 * Route `/admin/categories/new`.
 *
 * First slice: header + category picker card. No metafields, Magic
 * suggestion, sidebar, or bottom bar yet.
 */
@Composable
fun CreateCategoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaxonomyPickerViewModel = vitranKoinViewModel(),
) {
    var state by remember { mutableStateOf(CreateCategoryFormState()) }
    val taxonomyState by viewModel.uiState.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AdminTokens.PageBackground),
    ) {
        val twoColumn = maxWidth >= VitranSize.desktopBreakpoint
        val horizontalPad = if (twoColumn) VitranSpacing.xxl else VitranSpacing.lg
        Column(modifier = Modifier.fillMaxSize()) {
            CreateCategoryHeaderBar(
                storeName = state.storeName,
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
                    verticalAlignment = Alignment.Top,
                ) {
                    CreateCategoryTaxonomyBody(
                        state = state,
                        onStateChange = { state = it },
                        taxonomyState = taxonomyState,
                        onRetry = viewModel::retry,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .widthIn(max = AdminTokens.ProductFormMaxWidth)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = AdminTokens.CardGap),
                    )
                    Spacer(
                        modifier = Modifier
                            .width(AdminTokens.RailWidth)
                            .fillMaxHeight(),
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = horizontalPad)
                        .padding(top = VitranSpacing.md, bottom = AdminTokens.CardGap),
                    verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
                ) {
                    CreateCategoryTaxonomyBody(
                        state = state,
                        onStateChange = { state = it },
                        taxonomyState = taxonomyState,
                        onRetry = viewModel::retry,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateCategoryTaxonomyBody(
    state: CreateCategoryFormState,
    onStateChange: (CreateCategoryFormState) -> Unit,
    taxonomyState: TaxonomyPickerUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (taxonomyState) {
        TaxonomyPickerUiState.Loading -> {
            AdminFormCard(modifier = modifier) {
                ReferenceDataLoading(message = "در حال بارگذاری دسته‌بندی‌ها…")
            }
        }
        is TaxonomyPickerUiState.Error -> {
            AdminFormCard(modifier = modifier) {
                ReferenceDataError(message = taxonomyState.message, onRetry = onRetry)
            }
        }
        TaxonomyPickerUiState.Empty -> {
            AdminFormCard(modifier = modifier) {
                ReferenceDataEmpty(message = "دسته‌بندی‌ای یافت نشد.")
            }
        }
        is TaxonomyPickerUiState.Content -> {
            CreateCategoryMainColumn(
                state = state,
                onStateChange = onStateChange,
                taxonomyRoots = taxonomyState.roots.toAdminTaxonomyNodes(),
                modifier = modifier,
            )
        }
    }
}

@Preview
@Composable
private fun CreateCategoryScreenPreview() {
    VitranTheme {
        CreateCategoryScreen(onBack = {})
    }
}

@Preview
@Composable
private fun CreateCategoryMainColumnPreview() {
    VitranTheme {
        var state by remember { mutableStateOf(CreateCategoryFormState()) }
        CreateCategoryMainColumn(
            state = state,
            onStateChange = { state = it },
            taxonomyRoots = rememberProductTaxonomy(),
        )
    }
}
