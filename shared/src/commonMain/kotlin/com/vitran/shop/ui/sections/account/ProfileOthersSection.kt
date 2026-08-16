package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_others_add
import vitranshop.shared.generated.resources.account_section_others
import vitranshop.shared.generated.resources.account_section_others_hint
import vitranshop.shared.generated.resources.ic_people
import vitranshop.shared.generated.resources.ic_plus

@Composable
internal fun ProfileOthersSection(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    AccountCard(modifier = modifier) {
        if (isDesktop) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VitranSpacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            ) {
                AccountSoftHeader(
                    title = stringResource(Res.string.account_section_others),
                    hint = stringResource(Res.string.account_section_others_hint),
                    icon = painterResource(Res.drawable.ic_people),
                    modifier = Modifier.weight(1f),
                )
                AddPersonButton(onClick = onAddClick)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VitranSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                AccountSoftHeader(
                    title = stringResource(Res.string.account_section_others),
                    hint = stringResource(Res.string.account_section_others_hint),
                    icon = painterResource(Res.drawable.ic_people),
                )
                AddPersonButton(
                    onClick = onAddClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun AddPersonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dashColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    val shape = RoundedCornerShape(VitranRadius.medium)
    Row(
        modifier = modifier
            .widthIn(min = 148.dp)
            .height(56.dp)
            .clip(shape)
            .background(AccountTokens.SoftIconBg, shape)
            .drawBehind {
                val stroke = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
                )
                drawRoundRect(
                    color = dashColor,
                    style = stroke,
                    cornerRadius = CornerRadius(VitranRadius.medium.toPx()),
                )
            }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_plus),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = MaterialTheme.colorScheme.primary,
        )
        VitranText(
            text = stringResource(Res.string.account_others_add),
            style = VitranTextStyle.Title,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = VitranSpacing.sm),
        )
    }
}
