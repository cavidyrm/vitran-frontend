package com.vitran.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranShapes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.hero_omnibox_a11y
import vitranshop.shared.generated.resources.hero_omnibox_clear_a11y
import vitranshop.shared.generated.resources.hero_omnibox_placeholder
import vitranshop.shared.generated.resources.hero_omnibox_submit_a11y
import vitranshop.shared.generated.resources.ic_arrow_right
import vitranshop.shared.generated.resources.ic_close

/** shop.app omnibox max content width. */
private val OmniboxMaxWidth = 584.dp
private val OmniboxActionSize = 40.dp

/**
 * Home hero search pill matching shop.app Omnibox.
 * Mock-only: [onSubmit] does not navigate yet.
 */
@Composable
fun HeroOmnibox(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholder = stringResource(Res.string.hero_omnibox_placeholder)
    val a11y = stringResource(Res.string.hero_omnibox_a11y)
    val submitA11y = stringResource(Res.string.hero_omnibox_submit_a11y)
    val clearA11y = stringResource(Res.string.hero_omnibox_clear_a11y)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .widthIn(max = OmniboxMaxWidth)
            .fillMaxWidth()
            .height(VitranSize.touchTarget)
            .shadow(
                elevation = VitranElevation.medium,
                shape = VitranShapes.pill,
                clip = false,
            )
            .clip(VitranShapes.pill)
            .background(MaterialTheme.colorScheme.surface)
            .semantics { contentDescription = a11y },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = VitranSpacing.lg, end = VitranSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = VitranSpacing.sm),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                }

                if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(VitranSize.touchTarget)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onQueryChange("") },
                            )
                            .semantics { contentDescription = clearA11y },
                        contentAlignment = Alignment.Center,
                    ) {
                        VitranIcon(
                            painter = painterResource(Res.drawable.ic_close),
                            contentDescription = null,
                            size = VitranSize.iconSmall,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(OmniboxActionSize)
                        .shadow(
                            elevation = VitranElevation.small,
                            shape = CircleShape,
                            clip = false,
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onSubmit,
                        )
                        .semantics { contentDescription = submitA11y },
                    contentAlignment = Alignment.Center,
                ) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_arrow_right),
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
                        size = VitranSize.iconMedium,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB, widthDp = 640)
@Composable
private fun HeroOmniboxEmptyPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            HeroOmnibox(
                query = "",
                onQueryChange = {},
                onSubmit = {},
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB, widthDp = 640)
@Composable
private fun HeroOmniboxFilledPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            HeroOmnibox(
                query = "کفش ورزشی",
                onQueryChange = {},
                onSubmit = {},
            )
        }
    }
}
