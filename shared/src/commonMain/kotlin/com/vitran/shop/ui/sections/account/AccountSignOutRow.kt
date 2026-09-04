package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.ErrorRed
import com.vitran.shop.ui.theme.VitranOpacity
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_sign_out
import vitranshop.shared.generated.resources.account_sign_out_in_progress
import vitranshop.shared.generated.resources.ic_logout

/**
 * Hub / settings sign-out control — shop.app uses neutral text + exit icon.
 */
@Composable
internal fun AccountSignOutRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSigningOut: Boolean = false,
    errorMessage: String? = null,
) {
    val label = if (isSigningOut) {
        stringResource(Res.string.account_sign_out_in_progress)
    } else {
        stringResource(Res.string.account_sign_out)
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = !isSigningOut,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            if (isSigningOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(VitranSize.iconSmall),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            } else {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_logout),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            VitranText(
                text = label,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (isSigningOut) VitranOpacity.INACTIVE else 1f,
                ),
            )
        }
        if (!errorMessage.isNullOrBlank()) {
            VitranText(
                text = errorMessage,
                style = VitranTextStyle.Body,
                color = ErrorRed,
                modifier = Modifier.padding(
                    start = VitranSpacing.lg,
                    end = VitranSpacing.lg,
                    bottom = VitranSpacing.lg,
                ),
            )
        }
    }
}
