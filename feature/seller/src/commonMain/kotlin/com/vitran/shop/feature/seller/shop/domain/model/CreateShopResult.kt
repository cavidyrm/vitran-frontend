package com.vitran.shop.feature.seller.shop.domain.model

data class CreateShopResult(
    val shop: SellerShopDetails,
    val sessionAccessUpdate: SessionAccessUpdate? = null,
)
