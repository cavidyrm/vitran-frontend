package com.vitran.shop.ui.sections.admin

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.vitran.shop.ui.components.admin.AdminSelectOption

enum class CreateStoreStep {
    Basics,
    Brand,
    Contact,
    Policies,
    Publish,
}

enum class StoreSocialKind {
    Instagram,
    Telegram,
    WhatsApp,
    Website,
    Email,
}

enum class PolicyInputMode {
    Manual,
    Template,
    Auto,
}

enum class AutosaveStatus {
    Idle,
    Saving,
    Saved,
}

@Immutable
data class StoreSocialChannel(
    val id: String,
    val kind: StoreSocialKind,
    val handle: String = "",
)

@Immutable
data class CreateStoreTheme(
    val id: String,
    val name: String,
    val emoji: String,
    val caption: String,
    val primary: Color,
    val secondary: Color,
    val hex: String,
    val pageBackground: Color,
    val cardBackground: Color,
    val fontLabel: String,
) {
    val onPrimary: Color
        get() = if (primary.luminance() < 0.45f) Color.White else Color(0xFF18181B)
}

@Immutable
data class StoreCategoryChip(
    val id: String,
    val label: String,
    val emoji: String,
)

@Immutable
data class PreviewProductTile(
    val id: String,
    val imageUrl: String,
    val title: String,
    val priceLabel: String,
)

@Immutable
data class PolicyTemplate(
    val id: String,
    val label: String,
    val policies: String,
    val shipping: String,
    val returns: String,
)

@Immutable
data class CreateStoreFormState(
    val storeName: String = "",
    val legalName: String = "",
    val slogan: String = "",
    val ownerName: String = "",
    val about: String = "",
    val typeId: String? = null,
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val provinceId: String? = null,
    val cityId: String? = null,
    val policies: String = "",
    val shipping: String = "",
    val returns: String = "",
    val policyMode: PolicyInputMode = PolicyInputMode.Manual,
    val policyTemplateId: String? = null,
    val socialChannels: List<StoreSocialChannel> = CreateStoreMocks.defaultSocialChannels,
    val themeId: String = CreateStoreMocks.DefaultThemeId,
    val themeManuallyEdited: Boolean = false,
    val coverUrl: String? = null,
    val iconUrl: String? = null,
    val coverZoom: Float = 1f,
    val coverOffsetX: Float = 0f,
    val coverOffsetY: Float = 0f,
    val slug: String = "",
    val slugManuallyEdited: Boolean = false,
    val dirty: Boolean = false,
    val published: Boolean = false,
) {
    val shareUrl: String
        get() = if (slug.isBlank()) "" else "vitran.shop/m/$slug"

    val theme: CreateStoreTheme
        get() = CreateStoreMocks.themes.firstOrNull { it.id == themeId }
            ?: CreateStoreMocks.themes.first()

    val colorHex: String
        get() = theme.hex

    val categoryLabel: String?
        get() = CreateStoreMocks.categories.firstOrNull { it.id == typeId }?.label

    val categoryEmoji: String?
        get() = CreateStoreMocks.categories.firstOrNull { it.id == typeId }?.emoji

    fun wizardFraction(current: CreateStoreStep): Float =
        (current.ordinal + 1) / 5f

    fun moveSocial(from: Int, to: Int): CreateStoreFormState {
        if (from == to || from !in socialChannels.indices || to !in socialChannels.indices) return this
        val next = socialChannels.toMutableList()
        val item = next.removeAt(from)
        next.add(to, item)
        return copy(socialChannels = next, dirty = true)
    }

    val canPublish: Boolean
        get() = storeName.isNotBlank() && slug.isNotBlank()

    fun withStoreName(value: String): CreateStoreFormState {
        val nextSlug = if (slugManuallyEdited) slug else slugifyStoreName(value)
        return copy(storeName = value, slug = nextSlug, dirty = true)
    }

    fun withSlug(value: String): CreateStoreFormState =
        copy(
            slug = slugifyStoreName(value).ifBlank { value.trim().lowercase() },
            slugManuallyEdited = true,
            dirty = true,
        )

    fun withProvince(id: String): CreateStoreFormState {
        val cityStillValid = CreateStoreMocks.cities.any { it.id == cityId && it.provinceId == id }
        return copy(provinceId = id, cityId = if (cityStillValid) cityId else null, dirty = true)
    }

    fun withCategory(id: String): CreateStoreFormState {
        val nextTheme = if (themeManuallyEdited) {
            themeId
        } else {
            CreateStoreMocks.suggestedThemeId(id)
        }
        return copy(typeId = id, themeId = nextTheme, dirty = true)
    }

    fun withTheme(id: String): CreateStoreFormState =
        copy(themeId = id, themeManuallyEdited = true, dirty = true)

    fun markedDirty(): CreateStoreFormState = copy(dirty = true)

    fun basicsComplete(): Boolean =
        storeName.isNotBlank() && typeId != null && slug.isNotBlank()

    fun brandComplete(): Boolean =
        coverUrl != null || iconUrl != null || about.isNotBlank()

    fun contactComplete(): Boolean =
        email.isNotBlank() || phone.isNotBlank() || socialChannels.any { it.handle.isNotBlank() }

    fun policiesComplete(): Boolean =
        policies.isNotBlank() || shipping.isNotBlank() || returns.isNotBlank()

    fun publishComplete(): Boolean = published

    fun stepComplete(step: CreateStoreStep): Boolean = when (step) {
        CreateStoreStep.Basics -> basicsComplete()
        CreateStoreStep.Brand -> brandComplete()
        CreateStoreStep.Contact -> contactComplete()
        CreateStoreStep.Policies -> policiesComplete()
        CreateStoreStep.Publish -> publishComplete()
    }

    fun completionFraction(): Float {
        val checks = listOf(
            storeName.isNotBlank(),
            typeId != null,
            slug.isNotBlank(),
            coverUrl != null || iconUrl != null,
            email.isNotBlank() || phone.isNotBlank() || socialChannels.any { it.handle.isNotBlank() },
            policies.isNotBlank() || shipping.isNotBlank() || returns.isNotBlank(),
        )
        return checks.count { it } / checks.size.toFloat()
    }
}

object CreateStoreMocks {
    const val DefaultThemeId = "modern"

    const val MockCoverUrl =
        "https://cdn.shopify.com/shop-assets/shopify_brokers/mywildbird.myshopify.com/1741733573/wildbird_carrier_-510.jpeg?width=800"
    const val MockIconUrl =
        "https://cdn.shopify.com/shop-assets/shopify_brokers/mywildbird.myshopify.com/1761246168/WildBird_Sparrow.png?width=640"

    val defaultSocialChannels: List<StoreSocialChannel> = listOf(
        StoreSocialChannel("instagram", StoreSocialKind.Instagram),
        StoreSocialChannel("telegram", StoreSocialKind.Telegram),
        StoreSocialChannel("whatsapp", StoreSocialKind.WhatsApp),
        StoreSocialChannel("website", StoreSocialKind.Website),
    )

    val categories: List<StoreCategoryChip> = listOf(
        StoreCategoryChip("apparel", "پوشاک", "👕"),
        StoreCategoryChip("electronics", "دیجیتال", "💻"),
        StoreCategoryChip("food", "غذا", "🍔"),
        StoreCategoryChip("beauty", "زیبایی", "💄"),
        StoreCategoryChip("home", "خانه", "🏠"),
        StoreCategoryChip("other", "سایر", "✨"),
    )

    val storeTypes: List<AdminSelectOption> =
        categories.map { AdminSelectOption(it.id, it.label) }

    val provinces: List<AdminSelectOption> = listOf(
        AdminSelectOption("tehran", "تهران"),
        AdminSelectOption("alborz", "البرز"),
        AdminSelectOption("isfahan", "اصفهان"),
        AdminSelectOption("fars", "فارس"),
        AdminSelectOption("razavi", "خراسان رضوی"),
        AdminSelectOption("east-azarbaijan", "آذربایجان شرقی"),
        AdminSelectOption("gilan", "گیلان"),
        AdminSelectOption("mazandaran", "مازندران"),
        AdminSelectOption("khuzestan", "خوزستان"),
        AdminSelectOption("kerman", "کرمان"),
    )

    val cities: List<CityOption> = listOf(
        CityOption("tehran-city", "tehran", "تهران"),
        CityOption("rey", "tehran", "ری"),
        CityOption("eslamshahr", "tehran", "اسلامشهر"),
        CityOption("shahriar", "tehran", "شهریار"),
        CityOption("karaj", "alborz", "کرج"),
        CityOption("fardis", "alborz", "فردیس"),
        CityOption("nazarabad", "alborz", "نظرآباد"),
        CityOption("isfahan-city", "isfahan", "اصفهان"),
        CityOption("kashan", "isfahan", "کاشان"),
        CityOption("najafabad", "isfahan", "نجف‌آباد"),
        CityOption("shiraz", "fars", "شیراز"),
        CityOption("marvdasht", "fars", "مرودشت"),
        CityOption("jahrom", "fars", "جهرم"),
        CityOption("mashhad", "razavi", "مشهد"),
        CityOption("neyshabur", "razavi", "نیشابور"),
        CityOption("sabzevar", "razavi", "سبزوار"),
        CityOption("tabriz", "east-azarbaijan", "تبریز"),
        CityOption("maragheh", "east-azarbaijan", "مراغه"),
        CityOption("marand", "east-azarbaijan", "مرند"),
        CityOption("rasht", "gilan", "رشت"),
        CityOption("anzali", "gilan", "انزلی"),
        CityOption("lahijan", "gilan", "لاهیجان"),
        CityOption("sari", "mazandaran", "ساری"),
        CityOption("babol", "mazandaran", "بابل"),
        CityOption("amol", "mazandaran", "آمل"),
        CityOption("ahvaz", "khuzestan", "اهواز"),
        CityOption("abadan", "khuzestan", "آبادان"),
        CityOption("dezful", "khuzestan", "دزفول"),
        CityOption("kerman-city", "kerman", "کرمان"),
        CityOption("rafsanjan", "kerman", "رفسنجان"),
        CityOption("jiroft", "kerman", "جیرفت"),
    )

    val themes: List<CreateStoreTheme> = listOf(
        CreateStoreTheme(
            id = "luxury",
            name = "لوکس",
            emoji = "🖤",
            caption = "مشکی طلایی",
            primary = Color(0xFF1A1A1A),
            secondary = Color(0xFFC6A15B),
            hex = "#1A1A1A",
            pageBackground = Color(0xFFF7F4EE),
            cardBackground = Color(0xFFFFFFFF),
            fontLabel = "سریف",
        ),
        CreateStoreTheme(
            id = "modern",
            name = "مدرن",
            emoji = "🟣",
            caption = "بنفش",
            primary = Color(0xFF5A31F4),
            secondary = Color(0xFFEEEAFF),
            hex = "#5A31F4",
            pageBackground = Color(0xFFF7F5FF),
            cardBackground = Color(0xFFFFFFFF),
            fontLabel = "مدرن",
        ),
        CreateStoreTheme(
            id = "natural",
            name = "طبیعی",
            emoji = "🟢",
            caption = "سبز",
            primary = Color(0xFF15803D),
            secondary = Color(0xFFDCFCE7),
            hex = "#15803D",
            pageBackground = Color(0xFFF3FBF6),
            cardBackground = Color(0xFFFFFFFF),
            fontLabel = "طبیعی",
        ),
        CreateStoreTheme(
            id = "tech",
            name = "تکنولوژی",
            emoji = "🔵",
            caption = "آبی",
            primary = Color(0xFF2563EB),
            secondary = Color(0xFFDBEAFE),
            hex = "#2563EB",
            pageBackground = Color(0xFFF3F7FF),
            cardBackground = Color(0xFFFFFFFF),
            fontLabel = "هندسی",
        ),
        CreateStoreTheme(
            id = "fashion",
            name = "فشن",
            emoji = "🌸",
            caption = "صورتی",
            primary = Color(0xFFDB2777),
            secondary = Color(0xFFFCE7F3),
            hex = "#DB2777",
            pageBackground = Color(0xFFFFF6FA),
            cardBackground = Color(0xFFFFFFFF),
            fontLabel = "فشن",
        ),
    )

    val policyTemplates: List<PolicyTemplate> = listOf(
        PolicyTemplate(
            id = "no-returns",
            label = "شرایط بازگشت کالا ندارم",
            policies = "خرید از این فروشگاه به معنای پذیرش قوانین است. لطفاً پیش از پرداخت، مشخصات کالا را کامل بررسی کنید.",
            shipping = "سفارش‌ها در روزهای کاری بسته‌بندی و ارسال می‌شوند. هزینه ارسال هنگام ثبت سفارش اعلام می‌گردد.",
            returns = "این فروشگاه شرایط بازگشت کالا ندارد و مرجوعی پذیرفته نمی‌شود.",
        ),
        PolicyTemplate(
            id = "seven-day",
            label = "۷ روز مهلت مرجوعی",
            policies = "خرید از این فروشگاه تابع قوانین نمایش‌داده‌شده است. کالا باید با همان بسته‌بندی اولیه بازگردانده شود.",
            shipping = "ارسال از انبار فروشنده؛ زمان تحویل بسته به شهر مقصد بین ۲ تا ۵ روز کاری است.",
            returns = "تا ۷ روز پس از تحویل، در صورت سالم بودن کالا و پلمپ، امکان مرجوعی وجود دارد.",
        ),
        PolicyTemplate(
            id = "standard",
            label = "قوانین استاندارد فروشگاه",
            policies = "مسئولیت انتخاب سایز، رنگ و مدل با خریدار است. تخلف از قوانین ممکن است به لغو سفارش منجر شود.",
            shipping = "ارسال با پست پیشتاز یا پیک. هزینه بر اساس وزن و مسافت محاسبه می‌شود.",
            returns = "مرجوعی فقط برای کالای معیوب یا مغایر با توضیحات، تا ۳ روز پس از دریافت، پذیرفته می‌شود.",
        ),
    )

    fun citiesFor(provinceId: String?): List<AdminSelectOption> =
        cities
            .filter { it.provinceId == provinceId }
            .map { AdminSelectOption(it.id, it.label) }

    fun suggestedThemeId(typeId: String): String = when (typeId) {
        "apparel", "beauty" -> "fashion"
        "electronics" -> "tech"
        "food", "home" -> "natural"
        else -> "modern"
    }

    fun previewProductsFor(typeId: String?): List<PreviewProductTile> {
        val items = when (typeId) {
            "electronics" -> listOf(
                Triple("هدفون بی‌سیم", "۲٬۴۵۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384"),
                Triple("ساعت هوشمند", "۳٬۱۵۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384"),
            )
            "food" -> listOf(
                Triple("ست پخت شیرینی", "۸۹۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384"),
                Triple("ظرف سرو", "۴۲۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384"),
            )
            "beauty" -> listOf(
                Triple("ست مراقبت پوست", "۱٬۲۸۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384"),
                Triple("رژ لب", "۳۶۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384"),
            )
            "apparel" -> listOf(
                Triple("کت لینن", "۲٬۹۰۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384"),
                Triple("شال ابریشم", "۷۴۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384"),
            )
            "home" -> listOf(
                Triple("ست روتختی", "۱٬۸۵۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384"),
                Triple("گلدان سرامیک", "۵۲۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384"),
            )
            else -> listOf(
                Triple("محصول ویژه", "۱٬۲۰۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384"),
                Triple("پرفروش هفته", "۹۸۰٬۰۰۰ تومان", "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384"),
            )
        }
        return items.mapIndexed { index, (title, price, url) ->
            PreviewProductTile(id = "preview-$index", imageUrl = url, title = title, priceLabel = price)
        }
    }

    fun autoPolicyCopy(storeName: String): PolicyTemplate {
        val name = storeName.ifBlank { "این فروشگاه" }
        return PolicyTemplate(
            id = "auto",
            label = "تولید خودکار",
            policies = "خرید از «$name» به معنای پذیرش قوانین فروشگاه است. اطلاعات کالا، قیمت و موجودی قبل از پرداخت بررسی شود.",
            shipping = "«$name» سفارش‌ها را در روزهای کاری ارسال می‌کند. زمان رسیدن بسته بسته به شهر مقصد متفاوت است.",
            returns = "مرجوعی «$name» تا ۷ روز پس از تحویل، در صورت سالم بودن کالا و حفظ بسته‌بندی، امکان‌پذیر است.",
        )
    }
}

@Immutable
data class CityOption(
    val id: String,
    val provinceId: String,
    val label: String,
)

internal fun slugifyStoreName(name: String): String {
    val transliterated = buildString {
        name.trim().lowercase().forEach { ch ->
            val mapped = PersianToLatin[ch]
            when {
                mapped != null -> append(mapped)
                ch.isLetterOrDigit() && ch.code < 128 -> append(ch)
                ch.isWhitespace() || ch == '-' || ch == '_' -> append('-')
            }
        }
    }
    return transliterated.replace(Regex("-+"), "-").trim('-')
}

private val PersianToLatin: Map<Char, String> = mapOf(
    'ا' to "a", 'آ' to "a", 'ب' to "b", 'پ' to "p", 'ت' to "t", 'ث' to "s",
    'ج' to "j", 'چ' to "ch", 'ح' to "h", 'خ' to "kh", 'د' to "d", 'ذ' to "z",
    'ر' to "r", 'ز' to "z", 'ژ' to "zh", 'س' to "s", 'ش' to "sh", 'ص' to "s",
    'ض' to "z", 'ط' to "t", 'ظ' to "z", 'ع' to "a", 'غ' to "gh", 'ف' to "f",
    'ق' to "q", 'ک' to "k", 'گ' to "g", 'ل' to "l", 'م' to "m", 'ن' to "n",
    'و' to "v", 'ه' to "h", 'ی' to "y", 'ئ' to "y", 'ء' to "", 'ة' to "h",
    'ك' to "k", 'ي' to "y", 'ۀ' to "h",
)
