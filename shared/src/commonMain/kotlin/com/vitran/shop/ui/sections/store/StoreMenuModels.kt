package com.vitran.shop.ui.sections.store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.vitran.shop.ui.sections.product.ProductReviewHistogram
import com.vitran.shop.ui.sections.product.ProductReviewItem
import com.vitran.shop.ui.sections.product.ProductReviewsMock
import com.vitran.shop.ui.sections.product.defaultProductReviews
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_policy_about
import vitranshop.shared.generated.resources.ic_policy_dollar
import vitranshop.shared.generated.resources.ic_policy_order
import vitranshop.shared.generated.resources.ic_social_email
import vitranshop.shared.generated.resources.ic_social_facebook
import vitranshop.shared.generated.resources.ic_social_instagram
import vitranshop.shared.generated.resources.ic_social_website
import vitranshop.shared.generated.resources.ic_social_youtube
import vitranshop.shared.generated.resources.store_menu_contact_instagram
import vitranshop.shared.generated.resources.store_menu_policy_privacy
import vitranshop.shared.generated.resources.store_menu_policy_refund
import vitranshop.shared.generated.resources.store_menu_policy_shipping
import vitranshop.shared.generated.resources.store_menu_ratings_count

/**
 * One policy row in the store menu sheet (shop.app Policies card).
 */
@Immutable
data class StoreMenuPolicy(
    val id: String,
    val kind: StoreMenuPolicyKind,
    val title: String,
)

enum class StoreMenuPolicyKind {
    Privacy,
    Refund,
    Shipping,
}

enum class StoreMenuContactKind {
    Facebook,
    Instagram,
    YouTube,
    Website,
    Email,
}

/**
 * One contact / social link in the store menu Contact card (shop.app icon + label).
 */
@Immutable
data class StoreMenuContactLink(
    val id: String,
    val kind: StoreMenuContactKind,
    val label: String,
)

/**
 * Teaser review card shown inside the store menu (before opening full reviews sheet).
 */
@Immutable
data class StoreMenuReviewTeaser(
    val rating: Int,
    val title: String,
    val productName: String,
    val body: String,
    val authorName: String,
    val authorInitial: String,
    val avatarColorArgb: Long,
    val dateLabel: String,
    val productThumbUrl: String,
)

/**
 * Mock payload for [StoreMenuSheet] — policies, contact, reviews teaser + full sheet.
 */
@Immutable
data class StoreMenuMock(
    val policies: List<StoreMenuPolicy>,
    val contactLinks: List<StoreMenuContactLink>,
    val address: String,
    val ratingsCountLabel: String,
    val mediaUrls: List<String>,
    val teaserReviews: List<StoreMenuReviewTeaser>,
    val fullReviews: ProductReviewsMock,
)

fun StoreMenuPolicyKind.icon(): DrawableResource = when (this) {
    StoreMenuPolicyKind.Privacy -> Res.drawable.ic_policy_about
    StoreMenuPolicyKind.Refund -> Res.drawable.ic_policy_dollar
    StoreMenuPolicyKind.Shipping -> Res.drawable.ic_policy_order
}

fun StoreMenuContactKind.icon(): DrawableResource = when (this) {
    StoreMenuContactKind.Facebook -> Res.drawable.ic_social_facebook
    StoreMenuContactKind.Instagram -> Res.drawable.ic_social_instagram
    StoreMenuContactKind.YouTube -> Res.drawable.ic_social_youtube
    StoreMenuContactKind.Website -> Res.drawable.ic_social_website
    StoreMenuContactKind.Email -> Res.drawable.ic_social_email
}

@Composable
fun rememberMockStoreMenu(store: StoreMock): StoreMenuMock {
    val privacy = stringResource(Res.string.store_menu_policy_privacy)
    val refund = stringResource(Res.string.store_menu_policy_refund)
    val shipping = stringResource(Res.string.store_menu_policy_shipping)
    val instagram = stringResource(Res.string.store_menu_contact_instagram)
    val ratingsCount = stringResource(
        Res.string.store_menu_ratings_count,
        store.reviewCountLabel,
    )

    return remember(
        store.id,
        store.name,
        store.featuredProducts,
        privacy,
        refund,
        shipping,
        instagram,
        ratingsCount,
        store.ratingLabel,
    ) {
        mockStoreMenu(
            store = store,
            privacy = privacy,
            refund = refund,
            shipping = shipping,
            instagram = instagram,
            ratingsCountLabel = ratingsCount,
        )
    }
}

private fun mockStoreMenu(
    store: StoreMock,
    privacy: String,
    refund: String,
    shipping: String,
    instagram: String,
    ratingsCountLabel: String,
): StoreMenuMock {
    val products = store.featuredProducts.ifEmpty {
        store.collections.map {
            StoreFeaturedProduct(id = it.id, title = it.title, imageUrl = it.imageUrl)
        }
    }
    // Review media + teaser thumbs are product images (shop.app), not collection tiles.
    val media = (products.map { it.imageUrl } + store.coverUrl + store.avatarUrl)
        .filter { it.isNotBlank() }
        .distinct()
        .take(8)

    val productA = products.getOrNull(0)
    val productB = products.getOrNull(1)
    val productC = products.getOrNull(2)

    val teasers = listOf(
        StoreMenuReviewTeaser(
            rating = 5,
            title = "فوق‌العاده بود",
            productName = productA?.title ?: "محصول پرفروش",
            body = "رنگ و بافت محصول عالی است؛ روی پوست یکدست می‌نشیند و ماندگاری خوبی دارد.",
            authorName = "مریم",
            authorInitial = "م",
            avatarColorArgb = 0xFFE8A0BFL,
            dateLabel = "۳ روز پیش",
            productThumbUrl = productA?.imageUrl ?: store.avatarUrl,
        ),
        StoreMenuReviewTeaser(
            rating = 5,
            title = "عاشق این محصول شدم",
            productName = productB?.title ?: productA?.title ?: "محصول محبوب",
            body = "فرمولاسیون سبک و رنگ دقیق. دقیقاً همان‌طور که دوست دارم نگه می‌دارد و تمام روز دوام می‌آورد.",
            authorName = "تالی",
            authorInitial = "ت",
            avatarColorArgb = 0xFF7EB6D9L,
            dateLabel = "۵ مرداد ۱۴۰۴",
            productThumbUrl = productB?.imageUrl ?: productA?.imageUrl ?: store.avatarUrl,
        ),
    )

    val handle = store.name.lowercase().replace(" ", "").replace("beauty", "")
    val website = when {
        store.id.contains("sacheu", ignoreCase = true) -> "sacheu.com"
        else -> "$handle.com"
    }
    val email = "support@$website"

    val baseReviews = defaultProductReviews(
        averageLabel = store.ratingLabel,
        ratingsCountLabel = ratingsCountLabel,
    )
    val full = baseReviews.copy(
        reviews = listOf(
            ProductReviewItem(
                rating = 5,
                title = "فوق‌العاده بود",
                body = "رنگ و بافت محصول عالی است؛ روی پوست یکدست می‌نشیند و ماندگاری خوبی دارد. حتماً دوباره می‌خرم.",
                authorName = "مریم",
                authorInitial = "م",
                avatarColorArgb = 0xFFE8A0BFL,
                dateLabel = "۳ روز پیش",
                variantLabel = productA?.title,
            ),
            ProductReviewItem(
                rating = 5,
                title = "عاشق این محصول شدم",
                body = "فرمولاسیون سبک و رنگ دقیق. دقیقاً همان‌طور که دوست دارم نگه می‌دارد و تمام روز دوام می‌آورد.",
                authorName = "تالی",
                authorInitial = "ت",
                avatarColorArgb = 0xFF7EB6D9L,
                dateLabel = "۵ مرداد ۱۴۰۴",
                variantLabel = productB?.title,
            ),
            ProductReviewItem(
                rating = 5,
                title = "بهترین انتخاب",
                body = "چند تا بک‌آپ دارم؛ هر وقت برای آرایش شک دارم سراغ این برند می‌روم و همیشه جواب می‌دهد.",
                authorName = "الیزابت",
                authorInitial = "ا",
                avatarColorArgb = 0xFFB5A0E8L,
                dateLabel = "۲ مرداد ۱۴۰۴",
                variantLabel = productC?.title ?: productA?.title,
            ),
            ProductReviewItem(
                rating = 5,
                title = "عالی!",
                body = "کیفیت بسته‌بندی و خود محصول عالی بود. رنگ‌ها دقیق و ماندگارند.",
                authorName = "یاسمن",
                authorInitial = "ی",
                avatarColorArgb = 0xFF6BC4A6L,
                dateLabel = "۲۲ تیر ۱۴۰۴",
            ),
        ) + baseReviews.reviews.drop(4),
        histogram = ProductReviewHistogram(
            five = 0.78f,
            four = 0.14f,
            three = 0.05f,
            two = 0.02f,
            one = 0.01f,
        ),
    )

    return StoreMenuMock(
        policies = listOf(
            StoreMenuPolicy(id = "privacy", kind = StoreMenuPolicyKind.Privacy, title = privacy),
            StoreMenuPolicy(id = "refund", kind = StoreMenuPolicyKind.Refund, title = refund),
            StoreMenuPolicy(id = "shipping", kind = StoreMenuPolicyKind.Shipping, title = shipping),
        ),
        contactLinks = listOf(
            StoreMenuContactLink(id = "instagram", kind = StoreMenuContactKind.Instagram, label = instagram),
            StoreMenuContactLink(id = "email", kind = StoreMenuContactKind.Email, label = email),
        ),
        address = "تهران، خیابان ولیعصر، پلاک ۱۲۰، واحد ۳",
        ratingsCountLabel = ratingsCountLabel,
        mediaUrls = media,
        teaserReviews = teasers,
        fullReviews = full,
    )
}
