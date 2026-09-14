package com.vitran.shop.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.vinceglb.filekit.FileKit

@Composable
actual fun BindFileKit() {
    remember {
        FileKit.init(appId = "com.vitran.shop")
        true
    }
}
