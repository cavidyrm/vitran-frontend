package com.vitran.shop.feature.seller.shop.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ShopPublicationStateTest {

    @Test
    fun falseFalse_isPendingApproval() {
        assertEquals(
            ShopPublicationState.PendingApproval,
            shopPublicationState(active = false, confirmed = false),
        )
    }

    @Test
    fun trueTrue_isLive() {
        assertEquals(
            ShopPublicationState.Live,
            shopPublicationState(active = true, confirmed = true),
        )
    }

    @Test
    fun falseTrue_isApprovedHidden() {
        assertEquals(
            ShopPublicationState.ApprovedHidden,
            shopPublicationState(active = false, confirmed = true),
        )
    }

    @Test
    fun trueFalse_isInconsistent_doesNotCrash() {
        assertIs<ShopPublicationState.Inconsistent>(
            shopPublicationState(active = true, confirmed = false),
        )
    }
}
