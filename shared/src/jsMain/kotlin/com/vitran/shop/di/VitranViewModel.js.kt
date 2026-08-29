package com.vitran.shop.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import org.koin.compose.getKoin

@Composable
actual inline fun <reified T : ViewModel> vitranKoinViewModel(): T {
    val koin = getKoin()
    return remember { koin.get<T>() }
}
