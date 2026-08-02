package com.vitran.shop.ui.navigation

sealed interface AppDestination {
    data object Home : AppDestination
    data object Categories : AppDestination
    data object Offers : AppDestination
    data object Saved : AppDestination
    data object Account : AppDestination
}
