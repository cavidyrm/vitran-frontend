package com.vitran.shop.feature.account.data.mapper

import com.vitran.shop.feature.account.data.remote.dto.ProductMatchDto
import com.vitran.shop.feature.account.data.remote.dto.ProductMatchProductDto
import com.vitran.shop.feature.account.domain.model.PersonId
import com.vitran.shop.feature.account.domain.model.ProductMatch
import com.vitran.shop.feature.account.domain.model.ProductMatchProduct
import com.vitran.shop.feature.account.domain.model.ProductMatchReason
import kotlinx.datetime.Instant

internal fun ProductMatchDto.toDomain(): ProductMatch =
    ProductMatch(
        id = id,
        productId = productId,
        product = product?.toDomain(),
        personId = personId?.let(::PersonId),
        slot = slot,
        reason = reason.toProductMatchReason(),
        createdAt = createdAt?.let { runCatching { Instant.parse(it) }.getOrNull() },
    )

internal fun ProductMatchProductDto.toDomain(): ProductMatchProduct =
    ProductMatchProduct(
        id = id,
        title = title,
        priceAmount = price,
        compareAtPriceAmount = compareAtPrice,
    )

internal fun String?.toProductMatchReason(): ProductMatchReason =
    when (this?.lowercase()) {
        "new" -> ProductMatchReason.New
        "discount" -> ProductMatchReason.Discount
        null, "" -> ProductMatchReason.Unknown("")
        else -> ProductMatchReason.Unknown(this)
    }
