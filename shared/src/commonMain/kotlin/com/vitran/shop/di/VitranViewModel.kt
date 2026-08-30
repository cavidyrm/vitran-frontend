package com.vitran.shop.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.core.parameter.ParametersHolder

/**
 * Resolves a Koin [ViewModel] in a way that works across all Compose Multiplatform targets.
 * On Wasm/JS (and iOS), [org.koin.compose.viewmodel.koinViewModel] triggers IrLinkageError
 * against lifecycle-viewmodel-compose private fields.
 */
@Composable
expect inline fun <reified T : ViewModel> vitranKoinViewModel(
    noinline parameters: (() -> ParametersHolder)? = null,
): T
