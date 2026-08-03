package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.DownloadAppBanner

/**
 * Home screen host. Sections are added one at a time.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        DownloadAppBanner(
            onClick = { /* mock — no store deep link yet */ },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
