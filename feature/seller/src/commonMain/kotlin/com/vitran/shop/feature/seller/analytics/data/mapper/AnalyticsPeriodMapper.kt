package com.vitran.shop.feature.seller.analytics.data.mapper

import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsPeriod

internal fun AnalyticsPeriod.toQueryValue(): String =
    when (this) {
        AnalyticsPeriod.SevenDays -> "7d"
        AnalyticsPeriod.ThirtyDays -> "30d"
    }
