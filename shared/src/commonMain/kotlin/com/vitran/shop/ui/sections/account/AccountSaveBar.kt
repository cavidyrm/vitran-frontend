package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_cancel
import vitranshop.shared.generated.resources.account_save_changes
import vitranshop.shared.generated.resources.ic_save

/**
 * Profile save actions — sits in the content column (above the footer).
 * Cancel + Save always shown; Save carries a diskette icon (profile redesign).
 */
@Composable
internal fun AccountSaveBar(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (isDesktop) Alignment.CenterStart else Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = AccountTokens.ContentMaxWidth)
                .fillMaxWidth()
                .padding(vertical = VitranSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = VitranSpacing.md,
                alignment = if (isDesktop) Alignment.Start else Alignment.CenterHorizontally,
            ),
        ) {
            AccountPrimaryButton(
                label = stringResource(Res.string.account_save_changes),
                onClick = onSave,
                icon = painterResource(Res.drawable.ic_save),
            )
            AccountOutlinedButton(
                label = stringResource(Res.string.account_cancel),
                onClick = onCancel,
            )
        }
    }
}
