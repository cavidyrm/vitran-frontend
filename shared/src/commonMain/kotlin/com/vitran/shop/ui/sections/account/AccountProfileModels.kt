package com.vitran.shop.ui.sections.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_gender_female
import vitranshop.shared.generated.resources.account_gender_male
import vitranshop.shared.generated.resources.account_gender_other
import vitranshop.shared.generated.resources.account_gender_unspecified

enum class AccountDest {
    Hub,
    Profile,
    Referrals,
    Following,
    Settings,
    Users,
}

enum class AccountGender {
    Unspecified,
    Female,
    Male,
    Other,
}

enum class AccountSizeField {
    Shoe,
    Top,
    Bottom,
}

enum class AccountPrefField {
    SkinType,
    SkinUndertone,
    SkinTone,
    HairType,
    HairColor,
}

@Immutable
data class AccountPrivacyPrefs(
    val publicProfile: Boolean = true,
    val publicLists: Boolean = true,
    val personalization: Boolean = true,
)

@Immutable
data class AccountProfile(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val emailVerified: Boolean,
    val phone: String,
    val roles: List<String>,
    val gender: AccountGender,
    val birthday: String,
    val shoeSize: String?,
    val topSize: String?,
    val bottomSize: String?,
    val skinType: String?,
    val skinUndertone: String?,
    val skinTone: String?,
    val hairType: String?,
    val hairColor: String?,
    val avatarUrl: String?,
) {
    val displayName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
            .ifBlank { username }

    val formattedPhone: String
        get() {
            val digits = phone.filter { it.isDigit() }
            val national = if (digits.startsWith("98") && digits.length > 10) {
                "0${digits.drop(2)}"
            } else if (!digits.startsWith("0") && digits.length == 10) {
                "0$digits"
            } else {
                digits
            }
            return toPersianDigits(national)
        }

    val isMerchant: Boolean
        get() = roles.any { it.equals("merchant", ignoreCase = true) || it.equals("seller", ignoreCase = true) }
}

@Immutable
data class RecentlyViewedItem(
    val id: String,
    val title: String,
    val imageUrl: String,
    val storeName: String,
    val priceLabel: String,
)

@Immutable
data class AccountHubExtras(
    val savedCount: Int,
    val followingCount: Int,
    val savedThumbUrl: String,
    val followingThumbUrl: String,
    val recentlyViewed: List<RecentlyViewedItem>,
    val followedStores: List<FollowedStore>,
)

@Immutable
data class FollowedStore(
    val id: String,
    val name: String,
    val logoUrl: String,
)

@Composable
fun rememberMockAccountProfile(): AccountProfile = remember {
    AccountProfile(
            id = "2",
            username = "javid",
            firstName = "جاوید",
            lastName = "محمدی",
            email = "user@example.com",
            emailVerified = true,
            phone = "9123456789",
            roles = listOf("customer"),
            gender = AccountGender.Unspecified,
            birthday = "",
            shoeSize = null,
            topSize = null,
            bottomSize = null,
            skinType = null,
            skinUndertone = null,
            skinTone = null,
            hairType = null,
            hairColor = null,
            avatarUrl = null,
        )
}

@Composable
fun rememberMockAccountHubExtras(): AccountHubExtras = remember {
    AccountHubExtras(
        savedCount = 24,
        followingCount = 8,
        savedThumbUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
        followingThumbUrl = "https://cdn.shopify.com/s/files/1/0569/4029/8284/files/D_1.png?v=1655843709&width=64",
        recentlyViewed = listOf(
            RecentlyViewedItem(
                id = "home-0",
                title = "Baking Sheet Duo",
                imageUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
                storeName = "Caraway",
                priceLabel = "۴٬۴۳۰٬۰۰۰ تومان",
            ),
            RecentlyViewedItem(
                id = "home-1",
                title = "Ceramic Nonstick Perfect Pot",
                imageUrl = "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
                storeName = "Our Place",
                priceLabel = "۶٬۲۶۰٬۰۰۰ تومان",
            ),
            RecentlyViewedItem(
                id = "home-2",
                title = "Magnolia Gathered Candle",
                imageUrl = "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
                storeName = "Magnolia",
                priceLabel = "۱٬۲۶۰٬۰۰۰ تومان",
            ),
            RecentlyViewedItem(
                id = "home-5",
                title = "Mulberry Silk Pillowcase",
                imageUrl = "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
                storeName = "Brooklinen",
                priceLabel = "۲٬۹۰۰٬۰۰۰ تومان",
            ),
        ),
        followedStores = listOf(
            FollowedStore(
                id = "wildbird",
                name = "WildBird",
                logoUrl = "https://cdn.shopify.com/s/files/1/0569/4029/8284/files/D_1.png?v=1655843709&width=64",
            ),
            FollowedStore(
                id = "caraway",
                name = "Caraway",
                logoUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=64",
            ),
            FollowedStore(
                id = "brooklinen",
                name = "Brooklinen",
                logoUrl = "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=64",
            ),
        ),
    )
}

@Composable
fun AccountGender.label(): String = stringResource(
    when (this) {
        AccountGender.Unspecified -> Res.string.account_gender_unspecified
        AccountGender.Female -> Res.string.account_gender_female
        AccountGender.Male -> Res.string.account_gender_male
        AccountGender.Other -> Res.string.account_gender_other
    },
)

internal val ShoeSizeOptions = listOf(
    "4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5",
    "9", "9.5", "10", "10.5", "11", "11.5", "12", "12.5",
)

internal val ClothingSizeOptions = listOf("XS", "S", "M", "L", "XL", "XXL", "XXXL")

internal val SkinTypeOptions = listOf("خشک", "چرب", "ترکیبی", "حساس", "نرمال")
internal val SkinUndertoneOptions = listOf("سرد", "گرم", "خنثی")
internal val SkinToneOptions = listOf("روشن", "متوسط", "تیره")
internal val HairTypeOptions = listOf("فرفری", "خشک", "نرمال", "صاف", "موج‌دار")
internal val HairColorOptions = listOf("مشکی", "قهوه‌ای", "بلوند", "قرمز", "خاکستری")

internal fun toPersianDigits(value: String): String = buildString {
    value.forEach { ch ->
        append(
            when (ch) {
                '0' -> '۰'
                '1' -> '۱'
                '2' -> '۲'
                '3' -> '۳'
                '4' -> '۴'
                '5' -> '۵'
                '6' -> '۶'
                '7' -> '۷'
                '8' -> '۸'
                '9' -> '۹'
                else -> ch
            },
        )
    }
}

internal fun toPersianDigits(value: Int): String = toPersianDigits(value.toString())

internal fun fromPersianDigits(value: String): String = buildString {
    value.forEach { ch ->
        append(
            when (ch) {
                '۰' -> '0'
                '۱' -> '1'
                '۲' -> '2'
                '۳' -> '3'
                '۴' -> '4'
                '۵' -> '5'
                '۶' -> '6'
                '۷' -> '7'
                '۸' -> '8'
                '۹' -> '9'
                else -> ch
            },
        )
    }
}

internal fun digitsOnly(value: String): String =
    fromPersianDigits(value).filter { it.isDigit() }
