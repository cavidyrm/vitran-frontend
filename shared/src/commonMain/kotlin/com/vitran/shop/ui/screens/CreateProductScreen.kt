package com.vitran.shop.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.admin.CreateProductBottomBar
import com.vitran.shop.ui.sections.admin.CreateProductFormState
import com.vitran.shop.ui.sections.admin.CreateProductHeaderBar
import com.vitran.shop.ui.sections.admin.CreateProductMainColumn
import com.vitran.shop.ui.sections.admin.CreateProductOrganizationCard
import com.vitran.shop.ui.sections.admin.CreateProductPreviewCard
import com.vitran.shop.ui.sections.admin.CreateProductStatusCard
import com.vitran.shop.ui.sections.admin.ProductPublishStatus
import com.vitran.shop.ui.sections.admin.ProductSaveMode
import com.vitran.shop.ui.sections.admin.ProductSavePhase
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_create_product_leave_exit
import vitranshop.shared.generated.resources.admin_create_product_leave_stay
import vitranshop.shared.generated.resources.admin_create_product_leave_title

/**
 * Merchant admin — add a product. Route `/admin/products/new`.
 *
 * Shopify-admin two-column form. Mock state only.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateProductScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf(CreateProductFormState()) }
    var leaveDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val titleAnchor = remember { BringIntoViewRequester() }
    val priceAnchor = remember { BringIntoViewRequester() }
    val categoryAnchor = remember { BringIntoViewRequester() }

    val uploadingIds = state.media.filter { it.uploading }.map { it.id }
    LaunchedEffect(uploadingIds) {
        uploadingIds.forEach { id ->
            delay(800)
            state = state.finishUpload(id)
        }
    }

    LaunchedEffect(state.savePhase) {
        if (state.savePhase != ProductSavePhase.Saving) return@LaunchedEffect
        delay(700)
        state = state.copy(savePhase = ProductSavePhase.Saved, dirty = false)
    }

    fun requestLeave() {
        if (state.dirty) {
            leaveDialog = true
        } else {
            onBack()
        }
    }

    fun attemptSave(mode: ProductSaveMode) {
        val errors = state.validate(mode)
        if (errors.hasAny) {
            state = state.copy(errors = errors)
            scope.launch {
                when {
                    errors.title != null -> titleAnchor.bringIntoView()
                    errors.price != null || errors.compareAt != null -> priceAnchor.bringIntoView()
                    errors.category != null -> categoryAnchor.bringIntoView()
                }
            }
            return
        }
        val nextStatus = if (mode == ProductSaveMode.Publish) {
            ProductPublishStatus.Active
        } else {
            ProductPublishStatus.Draft
        }
        state = state.copy(
            status = nextStatus,
            savePhase = ProductSavePhase.Saving,
            errors = errors,
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AdminTokens.PageBackground),
    ) {
        val twoColumn = maxWidth >= VitranSize.desktopBreakpoint
        val horizontalPad = if (twoColumn) VitranSpacing.xxl else VitranSpacing.lg
        Column(modifier = Modifier.fillMaxSize()) {
            CreateProductHeaderBar(
                storeName = state.storeName,
                onBack = { requestLeave() },
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
                    CreateProductMainColumn(
                        state = state,
                        onStateChange = { state = it },
                        titleAnchor = titleAnchor,
                        priceAnchor = priceAnchor,
                        categoryAnchor = categoryAnchor,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .widthIn(max = AdminTokens.ProductFormMaxWidth)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = AdminTokens.CardGap),
                    )
                    Column(
                        modifier = Modifier
                            .width(AdminTokens.RailWidth)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
                    ) {
                        CreateProductStatusCard(
                            state = state,
                            onStateChange = { state = it },
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
                        ) {
                            CreateProductOrganizationCard(
                                state = state,
                                onStateChange = { state = it },
                            )
                            CreateProductPreviewCard(state = state)
                        }
                    }
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
                        onStateChange = { state = it },
                    )
                    CreateProductMainColumn(
                        state = state,
                        onStateChange = { state = it },
                        titleAnchor = titleAnchor,
                        priceAnchor = priceAnchor,
                        categoryAnchor = categoryAnchor,
                    )
                    CreateProductOrganizationCard(
                        state = state,
                        onStateChange = { state = it },
                    )
                    CreateProductPreviewCard(state = state)
                }
            }
            CreateProductBottomBar(
                state = state,
                compact = !twoColumn,
                onCancel = { requestLeave() },
                onSaveDraft = { attemptSave(ProductSaveMode.Draft) },
                onSavePublish = { attemptSave(ProductSaveMode.Publish) },
            )
        }
    }

    if (leaveDialog) {
        Dialog(onDismissRequest = { leaveDialog = false }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(VitranRadius.large))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(AdminTokens.CardPadding),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                Text(
                    text = stringResource(Res.string.admin_create_product_leave_title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                    AdminSecondaryButton(
                        label = stringResource(Res.string.admin_create_product_leave_stay),
                        onClick = { leaveDialog = false },
                    )
                    AdminPrimaryButton(
                        label = stringResource(Res.string.admin_create_product_leave_exit),
                        onClick = onBack,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateProductScreenPreview() {
    VitranTheme {
        CreateProductScreen(onBack = {})
    }
}
