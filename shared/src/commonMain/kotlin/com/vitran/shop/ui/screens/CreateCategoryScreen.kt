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
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.admin.CreateCategoryFormState
import com.vitran.shop.ui.sections.admin.CreateCategoryHeaderBar
import com.vitran.shop.ui.sections.admin.CreateCategoryMainColumn
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
) {
    var state by remember { mutableStateOf(CreateCategoryFormState()) }

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
                    CreateCategoryMainColumn(
                        state = state,
                        onStateChange = { state = it },
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
                    CreateCategoryMainColumn(
                        state = state,
                        onStateChange = { state = it },
                    )
                }
            }
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
