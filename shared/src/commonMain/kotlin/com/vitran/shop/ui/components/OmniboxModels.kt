package com.vitran.shop.ui.components

import androidx.compose.runtime.Immutable

/**
 * shop.app typeahead rows when the query is empty: plain suggested searches.
 * When typing: [Shop] matches first, then [Keyword] autocomplete completions.
 */
@Immutable
sealed interface OmniboxResult {
    val id: String

    /** Merchant / store row: circular logo (or letter), name, optional rating. */
    @Immutable
    data class Shop(
        override val id: String,
        val name: String,
        val rating: Float?,
        val logoUrl: String?,
    ) : OmniboxResult

    /**
     * Keyword autocomplete: [matchedPrefix] is the typed portion (regular weight),
     * [completion] is the bold rest (shop.app `highlighted-text-container`).
     */
    @Immutable
    data class Keyword(
        override val id: String,
        val matchedPrefix: String,
        val completion: String,
    ) : OmniboxResult {
        val fullText: String get() = matchedPrefix + completion
    }
}

/** Idle “Suggested searches” list (empty query + focused). */
val MockOmniboxSuggestedKeywords: List<OmniboxResult.Keyword> = listOf(
    OmniboxResult.Keyword("s1", "", "پودر پروتئین گیاهی"),
    OmniboxResult.Keyword("s2", "", "کیف چرم وگان"),
    OmniboxResult.Keyword("s3", "", "دکوراسیون اتاق خواب"),
    OmniboxResult.Keyword("s4", "", "کاپشن ضدآب"),
    OmniboxResult.Keyword("s5", "", "هودی"),
    OmniboxResult.Keyword("s6", "", "کفش ورزشی"),
    OmniboxResult.Keyword("s7", "", "عطر زنانه"),
    OmniboxResult.Keyword("s8", "", "شلوار جین"),
)

/** shop.app cloud assets used for mock merchant logos. */
private const val ShopifyCloudAssets =
    "https://cdn.shopify.com/shopifycloud/shop-web-assets"

/**
 * Mock merchants for typed queries.
 * Mix of logo / letter-fallback and rating / no-rating so every shop-row variant is visible.
 */
private val MockOmniboxShops: List<OmniboxResult.Shop> = listOf(
    OmniboxResult.Shop(
        id = "shop1",
        name = "ویتران بِست",
        rating = 4.7f,
        logoUrl = "https://cdn.shopify.com/s/files/1/2080/8521/files/wj14-logo-k-200px_d4bc59d5-3d88-4135-8a47-e718d7b2e75c.png?v=1496952728&width=64",
    ),
    OmniboxResult.Shop(
        id = "shop2",
        name = "مد زنانه آوا",
        rating = 4.9f,
        logoUrl = "$ShopifyCloudAssets/baggu-logo-B94g3XXc.webp",
    ),
    OmniboxResult.Shop(
        id = "shop3",
        name = "پوشاک وومن استایل",
        rating = 4.6f,
        logoUrl = "https://cdn.shopify.com/s/files/1/0562/6809/5671/files/Puls_logo_nega_8x8cm.jpg?v=1671449433&width=64",
    ),
    OmniboxResult.Shop(
        id = "shop4",
        name = "گالری زیبا",
        rating = null,
        logoUrl = null,
    ),
    OmniboxResult.Shop(
        id = "shop5",
        name = "فشن لند تهران",
        rating = 4.8f,
        logoUrl = "https://cdn.shopify.com/s/files/1/0668/2161/9961/files/wwtlogo200x200.png?v=1662579255&width=64",
    ),
    OmniboxResult.Shop(
        id = "shop6",
        name = "بوتیک ساحل",
        rating = 4.5f,
        logoUrl = "$ShopifyCloudAssets/stevemadden-logo-BOIPC7s1.webp",
    ),
    OmniboxResult.Shop(
        id = "shop7",
        name = "خانه مد پارسه",
        rating = 4.7f,
        logoUrl = null,
    ),
    OmniboxResult.Shop(
        id = "shop8",
        name = "استور مینیمال",
        rating = 4.4f,
        logoUrl = "https://cdn.shopify.com/s/files/1/0746/7301/files/Undertow_Logo-text-2025-update-BLACK.jpg?v=1745948545&width=64",
    ),
    OmniboxResult.Shop(
        id = "shop9",
        name = "سالت‌استون بیوتی",
        rating = 4.8f,
        logoUrl = "$ShopifyCloudAssets/saltstone-logo-p1evxBed.webp",
    ),
    OmniboxResult.Shop(
        id = "shop10",
        name = "فنتی بیوتی",
        rating = 4.9f,
        logoUrl = "$ShopifyCloudAssets/fenty-logo-DCifHRWr.webp",
    ),
    OmniboxResult.Shop(
        id = "shop11",
        name = "اُوالا استور",
        rating = 4.6f,
        logoUrl = "$ShopifyCloudAssets/owala-logo-C4QoZeFE.webp",
    ),
    OmniboxResult.Shop(
        id = "shop12",
        name = "کیف و کفش روژا",
        rating = 4.3f,
        logoUrl = null,
    ),
    OmniboxResult.Shop(
        id = "shop13",
        name = "عطرخانه شمیم",
        rating = 4.7f,
        logoUrl = null,
    ),
    OmniboxResult.Shop(
        id = "shop14",
        name = "ورزشی نایس‌فیت",
        rating = null,
        logoUrl = "https://cdn.shopify.com/s/files/1/2080/8521/files/wj14-logo-k-200px_d4bc59d5-3d88-4135-8a47-e718d7b2e75c.png?v=1496952728&width=64",
    ),
    OmniboxResult.Shop(
        id = "shop15",
        name = "دکوراسیون خانه نو",
        rating = 4.5f,
        logoUrl = null,
    ),
    OmniboxResult.Shop(
        id = "shop16",
        name = "جین بار تهران",
        rating = 4.2f,
        logoUrl = null,
    ),
)

/** Autocomplete suffixes appended after the typed query (shop.app keyword rows). */
private val MockKeywordCompletions: List<String> = listOf(
    " جین",
    " لباس",
    " کفش",
    " عطر",
    " کیف",
    " بوت",
    " شال",
    " مانتو",
    " شلوار",
    " کت",
    " پیراهن",
    " اکسسوری",
)

/**
 * Builds typeahead rows like shop.app:
 * - blank query → suggested keywords
 * - non-blank → matching shops first, then keyword completions (prefix + bold suffix)
 */
fun mockOmniboxResults(query: String): List<OmniboxResult> {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) {
        return MockOmniboxSuggestedKeywords
    }

    val matchedShops = MockOmniboxShops.filter {
        it.name.contains(trimmed, ignoreCase = true)
    }
    // Keep shop rows first (shop.app), then pad with other merchants so the list scrolls.
    val shops = (matchedShops + MockOmniboxShops)
        .distinctBy { it.id }

    val keywords = MockKeywordCompletions.mapIndexed { index, suffix ->
        OmniboxResult.Keyword(
            id = "kw-$index-$trimmed",
            matchedPrefix = trimmed,
            completion = suffix,
        )
    }

    return shops + keywords
}
