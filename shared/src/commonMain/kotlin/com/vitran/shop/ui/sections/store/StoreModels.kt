package com.vitran.shop.ui.sections.store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.vitran.shop.ui.sections.categories.allMockCategoriesMerchantShops
import com.vitran.shop.ui.sections.home.HomeShopCard
import com.vitran.shop.ui.sections.home.allMockHomeShopCards
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.store_collection_bestsellers
import vitranshop.shared.generated.resources.store_collection_carrier
import vitranshop.shared.generated.resources.store_collection_eyes
import vitranshop.shared.generated.resources.store_collection_face
import vitranshop.shared.generated.resources.store_collection_lips
import vitranshop.shared.generated.resources.store_collection_new
import vitranshop.shared.generated.resources.store_collection_sling
import vitranshop.shared.generated.resources.store_collection_wrap
import vitranshop.shared.generated.resources.store_nav_accessories
import vitranshop.shared.generated.resources.store_nav_bestsellers
import vitranshop.shared.generated.resources.store_nav_carrier
import vitranshop.shared.generated.resources.store_nav_eyes
import vitranshop.shared.generated.resources.store_nav_face
import vitranshop.shared.generated.resources.store_nav_lips
import vitranshop.shared.generated.resources.store_nav_sling
import vitranshop.shared.generated.resources.store_nav_wrap
import vitranshop.shared.generated.resources.store_shop_all

/**
 * One chip in the store category / nav row (shop.app store navigation menu).
 */
@Immutable
data class StoreNavChip(
    val id: String,
    val label: String,
    /** Optional circular thumb — null for “Shop all”. */
    val thumbUrl: String? = null,
)

/**
 * One collection tile in the store collections grid.
 */
@Immutable
data class StoreCollection(
    val id: String,
    val title: String,
    val imageUrl: String,
)

/**
 * Featured product used in store collections thumbs / menu review teasers.
 */
@Immutable
data class StoreFeaturedProduct(
    val id: String,
    val title: String,
    val imageUrl: String,
)

/**
 * Mock store / merchant page used by [com.vitran.shop.ui.screens.StoreScreen].
 *
 * Mirrors shop.app `store-screen-v2` header fields (cover, brand fill, wordmark, rating).
 *
 * [wordmarkUrl] null → shop.app renders an `h1` store name (e.g. Tushbaby).
 * [avatarUrl] is the circular logo inside the store-menu pill.
 * [featuredProducts] power collection thumbs and menu review product labels.
 */
@Immutable
data class StoreMock(
    val id: String,
    val name: String,
    val coverUrl: String,
    val avatarUrl: String,
    /** Raster wordmark for cover center; null shows [name] as text. */
    val wordmarkUrl: String?,
    val brandColor: Color,
    val useLightText: Boolean,
    /** e.g. "۴٫۸" */
    val ratingLabel: String,
    /** e.g. "۸٫۶ هزار نظر" */
    val reviewCountLabel: String,
    val navChips: List<StoreNavChip>,
    val collections: List<StoreCollection>,
    val featuredProducts: List<StoreFeaturedProduct> = emptyList(),
)

/**
 * Resolve a store mock for [shopId] (Home id, Categories id, or handle).
 * Falls back to WildBird when the id is unknown so deep links still look complete.
 */
@Composable
fun rememberMockStore(shopId: String): StoreMock {
    val shopAll = stringResource(Res.string.store_shop_all)
    val babyCollections = listOf(
        stringResource(Res.string.store_collection_new),
        stringResource(Res.string.store_collection_wrap),
        stringResource(Res.string.store_collection_carrier),
        stringResource(Res.string.store_collection_sling),
    )
    val babyNav = listOf(
        stringResource(Res.string.store_nav_wrap),
        stringResource(Res.string.store_nav_carrier),
        stringResource(Res.string.store_nav_sling),
        stringResource(Res.string.store_nav_accessories),
    )
    val beautyCollections = listOf(
        stringResource(Res.string.store_collection_lips),
        stringResource(Res.string.store_collection_eyes),
        stringResource(Res.string.store_collection_face),
        stringResource(Res.string.store_collection_bestsellers),
    )
    val beautyNav = listOf(
        stringResource(Res.string.store_nav_lips),
        stringResource(Res.string.store_nav_eyes),
        stringResource(Res.string.store_nav_face),
        stringResource(Res.string.store_nav_bestsellers),
    )

    return remember(
        shopId,
        shopAll,
        babyCollections,
        babyNav,
        beautyCollections,
        beautyNav,
    ) {
        MockStoreCatalog.resolve(
            shopId = shopId,
            shopAll = shopAll,
            babyCollectionTitles = babyCollections,
            babyNavTitles = babyNav,
            beautyCollectionTitles = beautyCollections,
            beautyNavTitles = beautyNav,
        )
    }
}

internal object MockStoreCatalog {
    /** shop.app beauty stores use Lips / Eyes / Face / Bestsellers collections. */
    private val BeautyShopIds = setOf(
        "sacheu",
        "b-sacheu",
        "patrickta",
        "patternbeauty",
        "rhodeskin",
        "rhode",
    )

    fun resolve(
        shopId: String,
        shopAll: String,
        babyCollectionTitles: List<String>,
        babyNavTitles: List<String>,
        beautyCollectionTitles: List<String>,
        beautyNavTitles: List<String>,
    ): StoreMock {
        val normalized = shopId.trim().lowercase()
        fun labelsFor(id: String): Labels {
            val beauty = id in BeautyShopIds || id.contains("beauty")
            return Labels(
                shopAll = shopAll,
                collectionTitles = if (beauty) beautyCollectionTitles else babyCollectionTitles,
                navTitles = if (beauty) beautyNavTitles else babyNavTitles,
            )
        }

        allMockHomeShopCards().firstOrNull { it.id.equals(normalized, ignoreCase = true) }
            ?.let { return fromHomeShop(it, labelsFor(it.id)) }

        allMockCategoriesMerchantShops().firstOrNull { it.id.equals(normalized, ignoreCase = true) }
            ?.let { shop ->
                val home = allMockHomeShopCards().firstOrNull {
                    it.name.equals(shop.name, ignoreCase = true) ||
                        it.id.equals(shop.id.removePrefix("b-"), ignoreCase = true)
                }
                if (home != null) return fromHomeShop(home, labelsFor(home.id))
                return fromCategoriesShop(shop, labelsFor(shop.id))
            }

        when (normalized) {
            "rhode" ->
                allMockHomeShopCards().firstOrNull { it.id == "rhodeskin" }
                    ?.let { return fromHomeShop(it, labelsFor(it.id)) }
            "mywildbird", "wild-bird" ->
                allMockHomeShopCards().firstOrNull { it.id == "wildbird" }
                    ?.let { return fromHomeShop(it, labelsFor(it.id)) }
        }

        return fromHomeShop(
            allMockHomeShopCards().first { it.id == "wildbird" },
            labelsFor("wildbird"),
        )
    }

    private data class Labels(
        val shopAll: String,
        val collectionTitles: List<String>,
        val navTitles: List<String>,
    )

    private fun fromHomeShop(shop: HomeShopCard, labels: Labels): StoreMock {
        val (rating, count) = splitRating(shop.ratingLabel)
        val featured = shop.products.map {
            StoreFeaturedProduct(id = it.id, title = it.title, imageUrl = it.imageUrl)
        }
        val peekImages = featured.map { it.imageUrl }
        val collectionImages = (peekImages + peekImages).take(4)
        val collections = labels.collectionTitles.mapIndexed { index, title ->
            StoreCollection(
                id = "${shop.id}-coll-$index",
                title = title,
                imageUrl = collectionImages.getOrElse(index) { shop.coverUrl },
            )
        }
        val navChips = buildList {
            add(
                StoreNavChip(
                    id = "${shop.id}-all",
                    label = labels.shopAll,
                    thumbUrl = peekImages.firstOrNull(),
                ),
            )
            labels.navTitles.forEachIndexed { index, title ->
                add(
                    StoreNavChip(
                        id = "${shop.id}-nav-$index",
                        label = title,
                        // Cycle peeks so every chip keeps the same 42dp thumb + label layout.
                        thumbUrl = peekImages.takeIf { it.isNotEmpty() }?.let { it[index % it.size] },
                    ),
                )
            }
        }
        val avatar = avatarUrlFor(shop)
        return StoreMock(
            id = shop.id,
            name = shop.name,
            coverUrl = shop.coverUrl,
            avatarUrl = avatar,
            wordmarkUrl = wordmarkUrlFor(shop),
            brandColor = shop.brandColor,
            useLightText = shop.useLightText,
            ratingLabel = rating,
            reviewCountLabel = count,
            navChips = navChips,
            collections = collections,
            featuredProducts = featured,
        )
    }

    private fun fromCategoriesShop(
        shop: com.vitran.shop.ui.sections.categories.CategoriesMerchantShop,
        labels: Labels,
    ): StoreMock {
        val images = shop.imageUrls
        val collections = labels.collectionTitles.mapIndexed { index, title ->
            StoreCollection(
                id = "${shop.id}-coll-$index",
                title = title,
                imageUrl = images.getOrElse(index) { images.firstOrNull().orEmpty() },
            )
        }
        val navChips = buildList {
            add(
                StoreNavChip(
                    id = "${shop.id}-all",
                    label = labels.shopAll,
                    thumbUrl = images.firstOrNull(),
                ),
            )
            labels.navTitles.forEachIndexed { index, title ->
                add(
                    StoreNavChip(
                        id = "${shop.id}-nav-$index",
                        label = title,
                        thumbUrl = images.takeIf { it.isNotEmpty() }?.let { it[index % it.size] },
                    ),
                )
            }
        }
        return StoreMock(
            id = shop.id,
            name = shop.name,
            coverUrl = images.firstOrNull().orEmpty(),
            avatarUrl = shop.logoUrl,
            wordmarkUrl = null,
            brandColor = Color(0xFFA5896B),
            useLightText = true,
            ratingLabel = shop.ratingLabel,
            reviewCountLabel = "۱٫۲ هزار نظر",
            navChips = navChips,
            collections = collections,
            featuredProducts = images.mapIndexed { index, url ->
                StoreFeaturedProduct(
                    id = "${shop.id}-feat-$index",
                    title = "محصول منتخب ${index + 1}",
                    imageUrl = url,
                )
            },
        )
    }

    /**
     * shop.app uses `store-data-wordmark` when a raster wordmark exists; otherwise an `h1`.
     * Tushbaby has no wordmark image (text name only); logo is menu avatar only.
     */
    private fun wordmarkUrlFor(shop: HomeShopCard): String? {
        val url = shop.logoUrl.trim()
        if (url.isEmpty()) return null
        if (shop.id == "tushbaby") return null
        if (url.contains("ChatGPT", ignoreCase = true)) return null
        return url
    }

    /** Menu circle avatar — prefer logo; fall back to first product peek. */
    private fun avatarUrlFor(shop: HomeShopCard): String {
        val logo = shop.logoUrl.trim()
        if (logo.isNotEmpty()) return logo
        return shop.products.firstOrNull()?.imageUrl.orEmpty()
    }

    /**
     * Parses Home rating labels like `۴٫۵ (۱۲٫۷ هزار)` or `4.5 (12.7K)`.
     * Persian digits must be matched explicitly — `\d` only covers ASCII.
     */
    private fun splitRating(label: String): Pair<String, String> {
        val match = Regex("""([۰-۹0-9٫.]+)\s*\((.+)\)""").find(label.trim())
        val rating = match?.groupValues?.getOrNull(1) ?: "۴٫۸"
        val countRaw = match?.groupValues?.getOrNull(2)?.trim() ?: "۱۰ هزار"
        val count = if (countRaw.contains("نظر")) countRaw else "$countRaw نظر"
        return rating to count
    }
}
