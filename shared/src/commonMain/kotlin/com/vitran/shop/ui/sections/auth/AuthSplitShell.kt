package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/**
 * Auth layout: on md+ a two-column split (form Start / brand End in RTL → form right, brand left).
 * Form card is vertically centered in the viewport; scrolls only when content overflows.
 */
@Composable
fun AuthSplitShell(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AuthTokens.PageCanvas),
    ) {
        val viewportHeight = this.maxHeight
        val showBrand = this.maxWidth >= VitranSize.mdBreakpoint
        if (showBrand) {
            Row(modifier = Modifier.fillMaxSize()) {
                AuthFormPane(
                    minHeight = viewportHeight,
                    horizontalPad = VitranSpacing.xxxl,
                    verticalPad = VitranSpacing.xxl,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    content = content,
                )
                AuthBrandPanel(
                    modifier = Modifier
                        .weight(1.05f)
                        .fillMaxHeight(),
                )
            }
        } else {
            AuthFormPane(
                minHeight = viewportHeight,
                horizontalPad = VitranSpacing.lg,
                verticalPad = VitranSpacing.xl,
                modifier = Modifier.fillMaxSize(),
                content = content,
            )
        }
    }
}

@Composable
private fun AuthFormPane(
    minHeight: Dp,
    horizontalPad: Dp,
    verticalPad: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPad, vertical = verticalPad),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .heightIn(min = (minHeight - verticalPad * 2).coerceAtLeast(0.dp))
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = AuthTokens.FormCardMaxWidth)
                    .fillMaxWidth(),
            ) {
                content()
            }
        }
    }
}

private fun Dp.coerceAtLeast(minimumValue: Dp): Dp =
    if (this < minimumValue) minimumValue else this

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AuthSplitShellCompactPreview() {
    VitranTheme {
        AuthSplitShell {
            AuthLoginForm()
        }
    }
}

@Preview(showBackground = true, widthDp = 1100, heightDp = 800)
@Composable
private fun AuthSplitShellDesktopPreview() {
    VitranTheme {
        AuthSplitShell {
            AuthLoginForm()
        }
    }
}
