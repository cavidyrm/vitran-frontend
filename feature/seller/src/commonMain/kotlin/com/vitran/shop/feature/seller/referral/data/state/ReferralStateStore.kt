package com.vitran.shop.feature.seller.referral.data.state

import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.seller.referral.domain.model.ReferralProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** User-scoped referral profile cache. Cleared on logout. */
class ReferralStateStore(
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : SessionInvalidationListener {

    private val _profile = MutableStateFlow<ReferralProfile?>(null)
    val profile: StateFlow<ReferralProfile?> = _profile.asStateFlow()

    init {
        invalidationListeners.add(this)
    }

    fun put(profile: ReferralProfile) {
        _profile.value = profile
    }

    fun clear() {
        _profile.value = null
    }

    override suspend fun onSessionInvalidated() {
        clear()
    }
}
