package com.vitran.shop.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.ParametersHolder

@Composable
actual inline fun <reified T : ViewModel> vitranKoinViewModel(
    noinline parameters: (() -> ParametersHolder)?,
): T =
    if (parameters != null) {
        koinViewModel(parameters = parameters)
    } else {
        koinViewModel()
    }
