package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_create_product_title
import vitranshop.shared.generated.resources.admin_create_store_title
import vitranshop.shared.generated.resources.nav_account

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    onCreateStore: () -> Unit = {},
    onCreateProduct: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xl),
            modifier = Modifier.padding(VitranSpacing.xxl),
        ) {
            VitranText(
                text = stringResource(Res.string.nav_account),
                style = VitranTextStyle.Headline,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Box(
                modifier = Modifier
                    .widthIn(min = 180.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .background(MaterialTheme.colorScheme.onSurface)
                    .clickable(role = Role.Button, onClick = onCreateStore)
                    .padding(horizontal = VitranSpacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                VitranText(
                    text = stringResource(Res.string.admin_create_store_title),
                    style = VitranTextStyle.Label,
                    color = MaterialTheme.colorScheme.surface,
                )
            }
            Box(
                modifier = Modifier
                    .widthIn(min = 180.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .background(MaterialTheme.colorScheme.onSurface)
                    .clickable(role = Role.Button, onClick = onCreateProduct)
                    .padding(horizontal = VitranSpacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                VitranText(
                    text = stringResource(Res.string.admin_create_product_title),
                    style = VitranTextStyle.Label,
                    color = MaterialTheme.colorScheme.surface,
                )
            }
        }
    }
}
