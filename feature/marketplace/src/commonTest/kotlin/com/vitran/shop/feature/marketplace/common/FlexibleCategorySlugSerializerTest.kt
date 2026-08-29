package com.vitran.shop.feature.marketplace.common

import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugSerializer
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductDetailsDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopDataDto
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class FlexibleCategorySlugSerializerTest {

    private val json: Json = createNetworkJson()

    @Test
    fun decodeProductDetail_acceptsNumericCategorySlug() {
        val dto = json.decodeFromString<ProductDetailsDto>(
            """
            {
              "id": 1,
              "shop_id": 1,
              "category_slug": 1,
              "title": "Widget",
              "description": "Desc",
              "price": 100,
              "active": true,
              "confirmed": true,
              "images": [],
              "created_at": "2026-06-09T12:00:00Z",
              "updated_at": "2026-06-09T12:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("1", dto.categorySlug)
    }

    @Test
    fun decodeShopDetail_acceptsNumericCategorySlugsList() {
        val wrapper = json.decodeFromString<ShopDataDto>(
            """
            {
              "shop": {
                "id": 1,
                "owner_id": 2,
                "city_id": 1,
                "title": "Shop",
                "slug": "shop",
                "type": "retailer",
                "share_url": "https://vitran.ir/shop",
                "active": true,
                "confirmed": true,
                "category_slugs": [1, "aa-1-2-3-4"],
                "created_at": "2026-06-09T12:00:00Z",
                "updated_at": "2026-06-09T12:00:00Z"
              }
            }
            """.trimIndent(),
        )

        assertEquals(listOf("1", "aa-1-2-3-4"), wrapper.shop.categorySlugs)
    }

    @Test
    fun decodeElement_stringAndInt() {
        assertEquals("aa-1", FlexibleCategorySlugSerializer.decodeElement(json.parseToJsonElement("\"aa-1\"")))
        assertEquals("42", FlexibleCategorySlugSerializer.decodeElement(json.parseToJsonElement("42")))
    }
}
