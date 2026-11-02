package com.vitran.shop.feature.admin.home

import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.feature.admin.home.data.AdminHomeSectionDataDto
import com.vitran.shop.feature.admin.home.data.CreateAdminHomeSectionRequestDto
import com.vitran.shop.feature.admin.home.data.UpdateAdminHomeSectionRequestDto
import com.vitran.shop.feature.admin.home.data.toCreateDto
import com.vitran.shop.feature.admin.home.data.toDomain
import com.vitran.shop.feature.admin.home.data.toUpdateDto
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeSectionCommand
import com.vitran.shop.feature.admin.home.domain.resolveHomeSectionCategorySlug
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AdminHomeSectionBodyTest {

    private val json = createNetworkJson()

    @Test
    fun create_sendsCategorySlugAndSubtitle() {
        val categorySlug = resolveHomeSectionCategorySlug(
            source = "latest_products",
            typedSlug = "bt",
            savedSlug = null,
        )
        val body = json.encodeToString(
            CreateAdminHomeSectionRequestDto.serializer(),
            sample(source = "latest_products", categorySlug = categorySlug).toCreateDto(),
        )

        assertEquals("bt", categorySlug)
        assertTrue(body.contains("\"subtitle\":\"تازه‌ترین‌ها\""))
        assertTrue(body.contains("\"category_slug\":\"bt\""))
    }

    @Test
    fun update_sendsEmptyCategorySlugToClear() {
        val categorySlug = resolveHomeSectionCategorySlug(
            source = "popular_products",
            typedSlug = "",
            savedSlug = "aa",
        )
        val body = json.encodeToString(
            UpdateAdminHomeSectionRequestDto.serializer(),
            sample(source = "popular_products", subtitle = "", categorySlug = categorySlug).toUpdateDto(),
        )

        assertEquals("", categorySlug)
        assertTrue(body.contains("\"category_slug\":\"\""))
        assertTrue(body.contains("\"subtitle\":\"\""))
        assertFalse(body.contains("\"slug\""))
    }

    @Test
    fun bannersWithoutSavedCategory_omitsCategorySlug() {
        val categorySlug = resolveHomeSectionCategorySlug(
            source = "banners",
            typedSlug = "",
            savedSlug = null,
        )
        val body = json.encodeToString(
            CreateAdminHomeSectionRequestDto.serializer(),
            sample(source = "banners", categorySlug = categorySlug).toCreateDto(),
        )

        assertNull(categorySlug)
        assertFalse(body.contains("category_slug"))
    }

    @Test
    fun changingAwayFromACategorySource_clearsTheSavedSlug() {
        val categorySlug = resolveHomeSectionCategorySlug(
            source = "banners",
            typedSlug = "bt",
            savedSlug = "bt",
        )

        assertEquals("", categorySlug)
    }

    @Test
    fun response_readsSubtitleAndCategorySlug() {
        val section = json.decodeFromString(
            AdminHomeSectionDataDto.serializer(),
            """
            {
              "home_section": {
                "id": 21,
                "slug": "baby-toddler",
                "source": "latest_products",
                "item_kind": "products",
                "layout": "horizontal_rail",
                "title": "کودک و نوزاد",
                "subtitle": "تازه‌ترین‌ها",
                "item_limit": 12,
                "sort_order": 3,
                "active": true,
                "audience": "all",
                "hide_when_empty": true,
                "category_slug": "bt",
                "platforms": []
              }
            }
            """.trimIndent(),
        ).homeSection.toDomain()

        assertEquals("تازه‌ترین‌ها", section.subtitle)
        assertEquals("bt", section.categorySlug)
    }

    private fun sample(
        source: String,
        subtitle: String = "تازه‌ترین‌ها",
        categorySlug: String? = null,
    ) = SaveAdminHomeSectionCommand(
        slug = "baby-toddler",
        source = source,
        layout = "horizontal_rail",
        title = "کودک و نوزاد",
        subtitle = subtitle,
        categorySlug = categorySlug,
        itemLimit = 12,
        sortOrder = 3,
        active = true,
        audience = "all",
        hideWhenEmpty = true,
        platforms = emptyList(),
        minAppVersion = "",
        startsAt = "",
        endsAt = "",
    )
}
