package com.vitran.shop.feature.marketplace.common.domain.filter

import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug

sealed interface CityFilter {
    data class ById(val id: CityId) : CityFilter
    data class BySlug(val slug: CitySlug) : CityFilter
}

sealed interface ShopFilter {
    data class ById(val id: com.vitran.shop.feature.marketplace.shop.domain.model.ShopId) : ShopFilter
    data class BySlug(val slug: com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug) : ShopFilter
}
