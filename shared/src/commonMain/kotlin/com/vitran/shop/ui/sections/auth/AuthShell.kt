package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_language
import vitranshop.shared.generated.resources.auth_language_a11y
import vitranshop.shared.generated.resources.ic_chevron_down

/** shop.app language control inset from viewport bottom. */
private val AuthLanguageBottomInset = 18.dp

/** shop.app shell horizontal pad `px-24`. */
private val AuthShellHorizontalPad = VitranSpacing.xxl

/** shop.app compact vertical pad `py-16`. */
private val AuthShellCompactVerticalPad = VitranSpacing.lg

/**
 * shop.app `/accounts/login` fullscreen body — white canvas, centered content,
 * language control pinned near the bottom Start edge.
 *
 * Hosted as [com.vitran.shop.ui.screens.LoginScreen] (route `/account/login`).
 */
@Composable
fun AuthShell(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        val isCompact = maxWidth < VitranSize.mdBreakpoint
        val topPad = if (isCompact) {
            AuthShellCompactVerticalPad
        } else {
            // shop.app `sm:pt-[10vh]`
            maxHeight * 0.10f
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(
                        start = AuthShellHorizontalPad,
                        end = AuthShellHorizontalPad,
                        top = topPad,
                        bottom = AuthShellCompactVerticalPad,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    content()
                }
            }

            AuthLanguageRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = AuthShellHorizontalPad,
                        end = AuthShellHorizontalPad,
                        bottom = AuthLanguageBottomInset,
                    ),
            )
        }
    }
}

@Composable
private fun AuthLanguageRow(
    modifier: Modifier = Modifier,
) {
    val label = stringResource(Res.string.auth_language)
    val a11y = stringResource(Res.string.auth_language_a11y)
    Row(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                // Language menu out of scope — app is Persian-only.
            },
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 21.sp,
            ),
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_down),
            contentDescription = a11y,
            tint = MaterialTheme.colorScheme.primary,
            size = VitranSize.iconSmall,
            modifier = Modifier.padding(start = VitranSpacing.xs),
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AuthShellPreview() {
    VitranTheme {
        AuthShell {
            AuthLoginForm()
        }
    }
}
