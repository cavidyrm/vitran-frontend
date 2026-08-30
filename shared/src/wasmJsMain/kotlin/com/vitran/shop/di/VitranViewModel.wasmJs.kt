package com.vitran.shop.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import org.koin.compose.getKoin
import org.koin.core.parameter.ParametersHolder

@Composable
actual inline fun <reified T : ViewModel> vitranKoinViewModel(
    noinline parameters: (() -> ParametersHolder)?,
): T {
    val koin = getKoin()
    return remember {
        if (parameters != null) koin.get(parameters = parameters) else koin.get()
    }
}
