package com.vitran.shop.ui.sections.reference

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranSpacing

@Composable
fun ReferenceDataLoading(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(VitranSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        CircularProgressIndicator(color = AdminTokens.Brand)
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ReferenceDataError(
    message: String,
    onRetry: () -> Unit,
    retryLabel: String = "تلاش مجدد",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(VitranSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = AdminTokens.Destructive,
        )
        AdminPrimaryButton(label = retryLabel, onClick = onRetry)
    }
}

@Composable
fun ReferenceDataEmpty(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        modifier = modifier
            .fillMaxWidth()
            .padding(VitranSpacing.lg),
        style = MaterialTheme.typography.bodyMedium,
    )
}
