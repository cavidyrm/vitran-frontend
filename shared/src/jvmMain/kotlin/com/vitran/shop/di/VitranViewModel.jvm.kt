package com.vitran.shop.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual inline fun <reified T : ViewModel> vitranKoinViewModel(): T = koinViewModel()
