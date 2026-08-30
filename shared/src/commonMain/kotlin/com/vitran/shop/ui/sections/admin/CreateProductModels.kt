package com.vitran.shop.ui.sections.admin

import androidx.compose.runtime.Immutable
import com.vitran.shop.ui.components.admin.AdminSelectOption

enum class ProductPublishStatus {
    Draft,
    Active,
}

enum class ProductSaveMode {
    Draft,
    Publish,
}

enum class ProductSavePhase {
    Idle,
    Saving,
    Saved,
}

@Immutable
data class ProductMediaItem(
    val id: String,
    val url: String,
    val alt: String = "",
    val uploading: Boolean = false,
    /** Local preview bytes for newly picked images (not yet uploaded). */
    val previewBytes: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ProductMediaItem
        return id == other.id &&
            url == other.url &&
            alt == other.alt &&
            uploading == other.uploading &&
            previewBytes.contentEquals(other.previewBytes)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + alt.hashCode()
        result = 31 * result + uploading.hashCode()
        result = 31 * result + (previewBytes?.contentHashCode() ?: 0)
        return result
    }
}

@Immutable
data class CreateProductOptionDraft(
    val id: String,
    val name: String = "",
    val values: List<String> = emptyList(),
    val valueInput: String = "",
)

@Immutable
data class ProductVariantRow(
    val id: String,
    val combination: List<String>,
    val label: String,
    val price: String = "",
    val compareAt: String = "",
    val quantity: String = "",
    val sku: String = "",
    val imageUrl: String? = null,
    val selected: Boolean = false,
)

@Immutable
data class ProductFieldErrors(
    val title: String? = null,
    val price: String? = null,
    val category: String? = null,
    val compareAt: String? = null,
    val summary: String? = null,
) {
    val hasAny: Boolean
        get() = title != null || price != null || category != null || compareAt != null
}

@Immutable
data class CreateProductFormState(
    val title: String = "",
    val description: String = "",
    val media: List<ProductMediaItem> = emptyList(),
    val removedMedia: ProductMediaItem? = null,
    val categoryId: String? = null,
    val price: String = "",
    val compareAtEnabled: Boolean = false,
    val compareAtPrice: String = "",
    val inventoryTracked: Boolean = true,
    val quantity: String = "",
    val skuEnabled: Boolean = false,
    val sku: String = "",
    val barcode: String = "",
    val continueSelling: Boolean = false,
    val needsShipping: Boolean = true,
    val weight: String = "",
    val variantsEnabled: Boolean = false,
    val options: List<CreateProductOptionDraft> = emptyList(),
    val variants: List<ProductVariantRow> = emptyList(),
    val variantSeedNotice: Boolean = false,
    val pendingRemoveOptionId: String? = null,
    val status: ProductPublishStatus = ProductPublishStatus.Draft,
    val type: String = "",
    val brand: String = "",
    val collections: List<String> = emptyList(),
    val collectionInput: String = "",
    val tags: List<String> = emptyList(),
    val tagInput: String = "",
    val slug: String = "",
    val slugManuallyEdited: Boolean = false,
    val seoTitle: String = "",
    val seoDescription: String = "",
    val seoExpanded: Boolean = false,
    val dirty: Boolean = false,
    val savePhase: ProductSavePhase = ProductSavePhase.Idle,
    val errors: ProductFieldErrors = ProductFieldErrors(),
    val storeName: String = CreateProductMocks.StoreName,
) {
    val mediaUrls: List<String>
        get() = media.map { it.url }

    fun withTitle(value: String): CreateProductFormState {
        val clipped = value.take(CreateProductMocks.TitleMaxLength)
        val nextSlug = if (slugManuallyEdited) slug else slugifyStoreName(clipped)
        return copy(title = clipped, slug = nextSlug, dirty = true, savePhase = ProductSavePhase.Idle)
            .clearedErrors()
    }

    fun markedDirty(): CreateProductFormState =
        copy(dirty = true, savePhase = ProductSavePhase.Idle)

    fun clearedErrors(): CreateProductFormState = copy(errors = ProductFieldErrors())

    fun addMedia(item: ProductMediaItem): CreateProductFormState =
        if (media.any { it.id == item.id }) this
        else copy(media = media + item, dirty = true, savePhase = ProductSavePhase.Idle)

    fun finishUpload(id: String): CreateProductFormState =
        copy(media = media.map { if (it.id == id) it.copy(uploading = false) else it })

    fun removeMedia(id: String): CreateProductFormState {
        val item = media.firstOrNull { it.id == id } ?: return this
        return copy(
            media = media.filterNot { it.id == id },
            removedMedia = item.copy(uploading = false),
            dirty = true,
            savePhase = ProductSavePhase.Idle,
        )
    }

    fun undoRemoveMedia(): CreateProductFormState {
        val item = removedMedia ?: return this
        return copy(media = media + item, removedMedia = null, dirty = true)
    }

    fun setPrimaryMedia(id: String): CreateProductFormState {
        val item = media.firstOrNull { it.id == id } ?: return this
        return copy(
            media = listOf(item) + media.filterNot { it.id == id },
            dirty = true,
            savePhase = ProductSavePhase.Idle,
        )
    }

    fun moveMedia(id: String, towardStart: Boolean): CreateProductFormState {
        val index = media.indexOfFirst { it.id == id }
        if (index < 0) return this
        val target = if (towardStart) index - 1 else index + 1
        if (target !in media.indices) return this
        val next = media.toMutableList()
        val item = next.removeAt(index)
        next.add(target, item)
        return copy(media = next, dirty = true, savePhase = ProductSavePhase.Idle)
    }

    fun addCollection(): CreateProductFormState {
        val next = collectionInput.trim()
        if (next.isEmpty() || next in collections) return this
        return copy(collections = collections + next, collectionInput = "", dirty = true, savePhase = ProductSavePhase.Idle)
    }

    fun addTag(): CreateProductFormState {
        val next = tagInput.trim()
        if (next.isEmpty() || next in tags) return this
        return copy(tags = tags + next, tagInput = "", dirty = true, savePhase = ProductSavePhase.Idle)
    }

    fun enableVariants(): CreateProductFormState {
        val nextOptions = options.ifEmpty { listOf(CreateProductOptionDraft(id = "opt-1")) }
        return copy(variantsEnabled = true, options = nextOptions, dirty = true, savePhase = ProductSavePhase.Idle)
    }

    fun addOption(): CreateProductFormState {
        if (options.size >= CreateProductMocks.MaxOptions) return this
        val committed = commitPendingValues()
        if (committed.options.size >= CreateProductMocks.MaxOptions) return committed
        val id = "opt-${committed.options.size + 1}"
        return committed.copy(
            options = committed.options + CreateProductOptionDraft(id = id),
            dirty = true,
            savePhase = ProductSavePhase.Idle,
        ).rebuildVariants()
    }

    fun commitPendingValues(): CreateProductFormState {
        var next = this
        options.forEach { option ->
            next = next.commitOptionValue(option.id)
        }
        return next
    }

    fun moveOption(id: String, towardStart: Boolean): CreateProductFormState {
        val index = options.indexOfFirst { it.id == id }
        if (index < 0) return this
        val target = if (towardStart) index - 1 else index + 1
        if (target !in options.indices) return this
        val next = options.toMutableList()
        val item = next.removeAt(index)
        next.add(target, item)
        return copy(options = next, dirty = true, savePhase = ProductSavePhase.Idle).rebuildVariants()
    }

    fun moveOptionValue(id: String, value: String, towardStart: Boolean): CreateProductFormState =
        updateOption(id) { option ->
            val index = option.values.indexOf(value)
            if (index < 0) return@updateOption option
            val target = if (towardStart) index - 1 else index + 1
            if (target !in option.values.indices) return@updateOption option
            val next = option.values.toMutableList()
            val item = next.removeAt(index)
            next.add(target, item)
            option.copy(values = next)
        }

    fun updateOption(
        id: String,
        transform: (CreateProductOptionDraft) -> CreateProductOptionDraft,
    ): CreateProductFormState =
        copy(
            options = options.map { if (it.id == id) transform(it) else it },
            dirty = true,
            savePhase = ProductSavePhase.Idle,
        ).rebuildVariants()

    fun requestRemoveOption(id: String): CreateProductFormState =
        if (variants.isNotEmpty()) copy(pendingRemoveOptionId = id)
        else removeOption(id)

    fun removeOption(id: String): CreateProductFormState =
        copy(
            options = options.filterNot { it.id == id },
            pendingRemoveOptionId = null,
            dirty = true,
            savePhase = ProductSavePhase.Idle,
        ).rebuildVariants()

    fun commitOptionValue(id: String): CreateProductFormState =
        updateOption(id) { option ->
            val next = option.valueInput.trim()
            if (next.isEmpty() || next in option.values) option
            else option.copy(values = option.values + next, valueInput = "")
        }

    fun rebuildVariants(): CreateProductFormState {
        val combos = cartesianCombinations(options)
        if (combos.isEmpty()) {
            return copy(variants = emptyList(), variantSeedNotice = false)
        }
        val existing = variants.associateBy { it.id }
        val seeded = combos.map { combo ->
            val id = combo.joinToString("|")
            existing[id] ?: ProductVariantRow(
                id = id,
                combination = combo,
                label = combo.joinToString(" / "),
                price = price,
                compareAt = compareAtPrice,
                quantity = quantity,
                sku = sku,
            )
        }
        val createdNew = combos.any { existing[it.joinToString("|")] == null }
        return copy(variants = seeded, variantSeedNotice = createdNew && (price.isNotBlank() || quantity.isNotBlank()))
    }

    fun updateVariant(id: String, transform: (ProductVariantRow) -> ProductVariantRow): CreateProductFormState =
        copy(
            variants = variants.map { if (it.id == id) transform(it) else it },
            dirty = true,
            savePhase = ProductSavePhase.Idle,
        )

    fun toggleSelectAllVariants(selected: Boolean): CreateProductFormState =
        copy(variants = variants.map { it.copy(selected = selected) })

    fun applyPriceToSelected(): CreateProductFormState =
        copy(
            variants = variants.map { if (it.selected) it.copy(price = price, compareAt = compareAtPrice) else it },
            dirty = true,
            savePhase = ProductSavePhase.Idle,
        )

    fun applyQuantityToSelected(): CreateProductFormState =
        copy(
            variants = variants.map { if (it.selected) it.copy(quantity = quantity) else it },
            dirty = true,
            savePhase = ProductSavePhase.Idle,
        )

    fun assignImageToSelected(url: String): CreateProductFormState =
        copy(
            variants = variants.map { if (it.selected) it.copy(imageUrl = url) else it },
            dirty = true,
            savePhase = ProductSavePhase.Idle,
        )

    fun validate(mode: ProductSaveMode): ProductFieldErrors {
        val titleError = if (title.isBlank()) "نام محصول را وارد کنید." else null
        val parsedPrice = parseMoney(price)
        val priceError = when {
            mode == ProductSaveMode.Publish && price.isBlank() -> "قیمت فروش را وارد کنید."
            price.isNotBlank() && parsedPrice == null -> "قیمت محصول معتبر نیست."
            parsedPrice != null && parsedPrice <= 0 -> "قیمت باید بیشتر از صفر باشد."
            else -> null
        }
        val compareError = if (compareAtEnabled) {
            val compare = parseMoney(compareAtPrice)
            val sell = parsedPrice
            when {
                compareAtPrice.isNotBlank() && compare == null -> "قیمت قبل از تخفیف معتبر نیست."
                compare != null && sell != null && compare <= sell ->
                    "قیمت قبل از تخفیف باید بیشتر از قیمت فروش باشد."
                else -> null
            }
        } else {
            null
        }
        val categoryError = if (mode == ProductSaveMode.Publish && categoryId == null && type.isBlank()) {
            "دسته‌بندی یا نوع محصول را انتخاب کنید."
        } else {
            null
        }
        val summary = if (titleError != null || priceError != null || categoryError != null || compareError != null) {
            "بعضی فیلدها نیاز به اصلاح دارند."
        } else {
            null
        }
        return ProductFieldErrors(
            title = titleError,
            price = priceError,
            category = categoryError,
            compareAt = compareError,
            summary = summary,
        )
    }

    fun liveCompareAtError(): String? {
        if (!compareAtEnabled) return null
        val compare = parseMoney(compareAtPrice)
        val sell = parseMoney(price)
        return when {
            compareAtPrice.isNotBlank() && compare == null -> "قیمت قبل از تخفیف معتبر نیست."
            compare != null && sell != null && compare <= sell ->
                "قیمت قبل از تخفیف باید بیشتر از قیمت فروش باشد."
            else -> null
        }
    }
}

object CreateProductMocks {
    const val StoreName = "گالری نور"
    const val TitleMaxLength = 150
    const val MaxOptions = 3
    const val UrlPrefix = "vitran.shop/products/"

    val sampleMediaUrls: List<String> = listOf(
        "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=640",
        "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=640",
        "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=640",
        "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=640",
        "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=640",
        "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=640",
    )

    val productTypes: List<String> = listOf(
        "تیشرت",
        "کفش",
        "کیف",
        "شلوار",
        "اکسسوری",
        "مراقبت پوست",
        "دکور خانه",
        "خوراکی",
    )

    val brands: List<String> = listOf("نور", "ویتران", "محلی", "سایر")

    fun nextMediaUrl(existing: List<String>): String {
        val unused = sampleMediaUrls.firstOrNull { it !in existing }
        return unused ?: sampleMediaUrls[existing.size % sampleMediaUrls.size]
    }

    fun existingMediaUrl(existing: List<String>): String {
        val unused = sampleMediaUrls.asReversed().firstOrNull { it !in existing }
        return unused ?: nextMediaUrl(existing)
    }

    fun newMedia(url: String, uploading: Boolean): ProductMediaItem =
        ProductMediaItem(
            id = "media-${url.hashCode()}-${uploading}",
            url = url,
            uploading = uploading,
        )
}

fun productStatusOptions(
    draftLabel: String,
    draftDesc: String,
    activeLabel: String,
    activeDesc: String,
): List<AdminSelectOption> = listOf(
    AdminSelectOption(ProductPublishStatus.Draft.name, draftLabel, draftDesc),
    AdminSelectOption(ProductPublishStatus.Active.name, activeLabel, activeDesc),
)

internal fun cartesianCombinations(options: List<CreateProductOptionDraft>): List<List<String>> {
    val named = options.filter { it.name.isNotBlank() && it.values.isNotEmpty() }
    if (named.isEmpty()) return emptyList()
    var combos = named.first().values.map { listOf(it) }
    named.drop(1).forEach { option ->
        combos = combos.flatMap { prefix -> option.values.map { prefix + it } }
    }
    return combos
}

internal fun parseMoney(value: String): Long? {
    if (value.isBlank()) return null
    val digits = buildString {
        value.forEach { ch ->
            when (ch) {
                in '0'..'9' -> append(ch)
                in '۰'..'۹' -> append('0' + (ch - '۰'))
                ',', '٬', ' ', '،' -> Unit
                else -> return@forEach
            }
        }
    }
    if (digits.isEmpty()) return null
    return digits.toLongOrNull()
}

internal fun formatMoneyInput(raw: String): String {
    val digits = buildString {
        raw.forEach { ch ->
            when (ch) {
                in '0'..'9' -> append(ch)
                in '۰'..'۹' -> append('0' + (ch - '۰'))
            }
        }
    }.trimStart('0').ifEmpty { if (raw.any { it in '0'..'9' || it in '۰'..'۹' }) "0" else "" }
    if (digits.isEmpty()) return ""
    val grouped = digits.reversed().chunked(3).joinToString("٬").reversed()
    return grouped.map { ch ->
        if (ch in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[ch - '0'] else ch
    }.joinToString("")
}
