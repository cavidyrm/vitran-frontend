package com.vitran.shop.feature.seller.product.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ProductPublicationStateTest {

    @Test
    fun falseFalse_isPendingApproval() {
        assertEquals(
            ProductPublicationState.PendingApproval,
            productPublicationState(active = false, confirmed = false),
        )
    }

    @Test
    fun trueTrue_isLive() {
        assertEquals(
            ProductPublicationState.Live,
            productPublicationState(active = true, confirmed = true),
        )
    }

    @Test
    fun falseTrue_isApprovedHidden() {
        assertEquals(
            ProductPublicationState.ApprovedHidden,
            productPublicationState(active = false, confirmed = true),
        )
    }

    @Test
    fun trueFalse_isInconsistent_doesNotCrash() {
        assertIs<ProductPublicationState.Inconsistent>(
            productPublicationState(active = true, confirmed = false),
        )
    }
}
