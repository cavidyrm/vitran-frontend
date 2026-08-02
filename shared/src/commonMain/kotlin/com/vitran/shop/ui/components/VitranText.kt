package com.vitran.shop.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

enum class VitranTextStyle {
    Headline,
    Title,
    Body,
    Label,
}

@Composable
fun VitranText(
    text: String,
    style: VitranTextStyle,
    modifier: Modifier = Modifier,
    color: Color? = null,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color ?: MaterialTheme.colorScheme.onSurface,
        style = style.toTextStyle(),
        maxLines = maxLines,
        softWrap = softWrap,
    )
}

@Composable
private fun VitranTextStyle.toTextStyle(): TextStyle {
    val typography = MaterialTheme.typography
    return when (this) {
        VitranTextStyle.Headline -> typography.headlineMedium
        VitranTextStyle.Title -> typography.titleMedium
        VitranTextStyle.Body -> typography.bodyMedium
        VitranTextStyle.Label -> typography.labelSmall
    }
}
