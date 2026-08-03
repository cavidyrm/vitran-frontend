package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.DownloadAppBanner
import com.vitran.shop.ui.sections.home.HomeHero

/**
 * Home screen host. Sections are added one at a time.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // shop.app: download strip is absolute over the hero collage.
        Box(modifier = Modifier.fillMaxWidth()) {
            HomeHero(modifier = Modifier.fillMaxWidth())
            DownloadAppBanner(
                onClick = { /* mock — no store deep link yet */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(10f),
            )
        }
    }
}
