package com.vitran.shop.core.session.domain

sealed interface SessionState {
    data object Restoring : SessionState
    data object Anonymous : SessionState
    data object Authenticated : SessionState
}
