package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.nav_categories

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = stringResource(Res.string.nav_categories),
        modifier = modifier,
    )
}
