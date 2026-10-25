package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.admin.AdminTaxonomyNode
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing

/**
 * Shopify-admin product form chrome shared by create and read-only detail.
 * From [VitranSize.desktopBreakpoint] the main column and rail sit side by side.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ProductAdminFormScaffold(
    state: CreateProductFormState,
    onBack: () -> Unit,
    titleAnchor: BringIntoViewRequester,
    priceAnchor: BringIntoViewRequester,
    categoryAnchor: BringIntoViewRequester,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    storeName: String = state.storeName,
    headerTitle: String? = null,
    headerSubtitle: String? = null,
    headerBreadcrumb: String? = null,
    headerTrailing: @Composable (() -> Unit)? = null,
    taxonomyRoots: List<AdminTaxonomyNode> = emptyList(),
    taxonomyLoading: Boolean = false,
    taxonomyError: String? = null,
    onTaxonomyRetry: () -> Unit = {},
    onStateChange: (CreateProductFormState) -> Unit = {},
    onUploadImages: (() -> Unit)? = null,
    onSelectExistingImages: (() -> Unit)? = null,
    replacement: @Composable (() -> Unit)? = null,
    bottomBar: @Composable ((compact: Boolean) -> Unit)? = null,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AdminTokens.PageBackground),
    ) {
        val twoColumn = maxWidth >= VitranSize.desktopBreakpoint
        val horizontalPad = if (twoColumn) VitranSpacing.xxl else VitranSpacing.lg
        Column(modifier = Modifier.fillMaxSize()) {
            CreateProductHeaderBar(
                storeName = storeName,
                onBack = onBack,
                title = headerTitle,
                subtitle = headerSubtitle,
                breadcrumb = headerBreadcrumb,
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
            if (replacement != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    replacement()
                }
            } else if (twoColumn) {
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
                    CreateProductMainColumn(
                        state = state,
                        onStateChange = onStateChange,
                        titleAnchor = titleAnchor,
                        priceAnchor = priceAnchor,
                        categoryAnchor = categoryAnchor,
                        taxonomyRoots = taxonomyRoots,
                        taxonomyLoading = taxonomyLoading,
                        taxonomyError = taxonomyError,
                        onTaxonomyRetry = onTaxonomyRetry,
                        onUploadImages = onUploadImages,
                        onSelectExistingImages = onSelectExistingImages,
                        readOnly = readOnly,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .widthIn(max = AdminTokens.ProductFormMaxWidth)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = AdminTokens.CardGap),
                    )
                    ProductFormRail(
                        state = state,
                        onStateChange = onStateChange,
                        readOnly = readOnly,
                        modifier = Modifier.width(AdminTokens.RailWidth).fillMaxHeight(),
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
                    CreateProductStatusCard(
                        state = state,
                        onStateChange = onStateChange,
                        readOnly = readOnly,
                    )
                    CreateProductMainColumn(
                        state = state,
                        onStateChange = onStateChange,
                        titleAnchor = titleAnchor,
                        priceAnchor = priceAnchor,
                        categoryAnchor = categoryAnchor,
                        taxonomyRoots = taxonomyRoots,
                        taxonomyLoading = taxonomyLoading,
                        taxonomyError = taxonomyError,
                        onTaxonomyRetry = onTaxonomyRetry,
                        onUploadImages = onUploadImages,
                        onSelectExistingImages = onSelectExistingImages,
                        readOnly = readOnly,
                    )
                    if (!readOnly) {
                        CreateProductOrganizationCard(
                            state = state,
                            onStateChange = onStateChange,
                        )
                    }
                    CreateProductPreviewCard(state = state)
                }
            }
            bottomBar?.invoke(!twoColumn)
        }
    }
}

@Composable
private fun ProductFormRail(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
    ) {
        CreateProductStatusCard(
            state = state,
            onStateChange = onStateChange,
            readOnly = readOnly,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
        ) {
            if (!readOnly) {
                CreateProductOrganizationCard(
                    state = state,
                    onStateChange = onStateChange,
                )
            }
            CreateProductPreviewCard(state = state)
        }
    }
}
