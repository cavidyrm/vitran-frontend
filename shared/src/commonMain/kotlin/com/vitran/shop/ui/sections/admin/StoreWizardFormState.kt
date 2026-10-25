package com.vitran.shop.ui.sections.admin

import com.vitran.shop.feature.admin.moderation.domain.AdminShopSummary
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.seller.shop.presentation.EditShopFormState
import com.vitran.shop.feature.seller.shop.presentation.ShopHoursDraft
import com.vitran.shop.feature.seller.shop.presentation.ShopProfileDraft
import com.vitran.shop.feature.seller.shop.presentation.ShopRuleDraft
import com.vitran.shop.feature.seller.shop.presentation.ShopShippingDraft

fun EditShopFormState.toCreateStoreFormState(): CreateStoreFormState =
    CreateStoreFormState(
        storeName = title,
        about = description,
        typeId = type.takeIf { it.isNotBlank() },
        phone = phoneNumber,
        supportTimes = supportTimes,
        address = address,
        cityId = cityId?.value?.toString(),
        categorySlugs = categorySlugs,
        avatarUrl = avatarUrl,
        slug = slug,
        slugManuallyEdited = true,
        profile = profile,
        socialChannels = storeSocialChannels(
            instagram = instagram,
            telegram = telegram,
            whatsapp = whatsapp,
            website = website,
            bale = bale,
            rubika = rubika,
            tiktok = tiktok,
            includeEmptyDefaults = true,
        ),
    )

/**
 * Copies wizard edits back onto the seller form.
 * Bale, Rubika, and TikTok stay in [EditShopFormState.editedFields] so PATCH still omits untouched values.
 * [ShopProfileDraft.touched] is whatever the profile editor already recorded.
 */
fun CreateStoreFormState.appliedTo(previous: EditShopFormState): EditShopFormState {
    val handles = socialChannels.groupBy { it.kind }.mapValues { (_, rows) ->
        rows.lastOrNull()?.handle.orEmpty()
    }
    fun handle(kind: StoreSocialKind) = handles[kind].orEmpty()
    val nextBale = handle(StoreSocialKind.Bale)
    val nextRubika = handle(StoreSocialKind.Rubika)
    val nextTiktok = handle(StoreSocialKind.TikTok)
    val edited = previous.editedFields.toMutableSet()
    if (nextBale != previous.bale) edited += "bale"
    if (nextRubika != previous.rubika) edited += "rubika"
    if (nextTiktok != previous.tiktok) edited += "tiktok"
    return previous.copy(
        title = storeName,
        slug = slug,
        description = about,
        address = address,
        phoneNumber = phone,
        supportTimes = supportTimes,
        type = typeId?.takeIf { it.isNotBlank() } ?: previous.type,
        cityId = cityId?.toLongOrNull()?.let(::CityId),
        categorySlugs = categorySlugs,
        avatarUrl = avatarUrl,
        whatsapp = handle(StoreSocialKind.WhatsApp),
        telegram = handle(StoreSocialKind.Telegram),
        instagram = handle(StoreSocialKind.Instagram),
        website = handle(StoreSocialKind.Website),
        bale = nextBale,
        rubika = nextRubika,
        tiktok = nextTiktok,
        profile = profile,
        editedFields = edited,
    )
}

fun AdminShopSummary.toCreateStoreFormState(): CreateStoreFormState =
    CreateStoreFormState(
        storeName = title.orEmpty(),
        about = description.orEmpty(),
        typeId = type?.takeIf { it.isNotBlank() },
        phone = phoneNumber.orEmpty(),
        supportTimes = supportTimes.orEmpty(),
        address = address.orEmpty(),
        cityId = cityId?.toString(),
        categorySlugs = categorySlugs,
        avatarUrl = avatarUrl.orEmpty(),
        slug = slug,
        slugManuallyEdited = true,
        profile = ShopProfileDraft(
            seoTitle = seoTitle.orEmpty(),
            seoDescription = seoDescription.orEmpty(),
            noindex = noindex,
            rules = rules.map { ShopRuleDraft(title = it.title, body = it.body) },
            shippingMethods = shippingMethods.map { method ->
                ShopShippingDraft(
                    code = method.code,
                    title = method.title,
                    cost = method.cost?.toString().orEmpty(),
                    minDays = method.minDays?.toString().orEmpty(),
                    maxDays = method.maxDays?.toString().orEmpty(),
                    active = method.active != false,
                )
            },
            workingHours = workingHours.map { hours ->
                ShopHoursDraft(
                    day = hours.day,
                    opensAt = hours.opensAt ?: "09:00",
                    closesAt = hours.closesAt ?: "18:00",
                    closed = hours.closed,
                )
            },
            fulfillmentType = fulfillmentType.orEmpty(),
            webhookUrl = webhookUrl.orEmpty(),
        ),
        socialChannels = storeSocialChannels(
            instagram = instagram.orEmpty(),
            telegram = telegram.orEmpty(),
            whatsapp = whatsapp.orEmpty(),
            website = website.orEmpty(),
            bale = bale.orEmpty(),
            rubika = rubika.orEmpty(),
            tiktok = tiktok.orEmpty(),
            includeEmptyDefaults = false,
        ),
    )

private fun storeSocialChannels(
    instagram: String,
    telegram: String,
    whatsapp: String,
    website: String,
    bale: String,
    rubika: String,
    tiktok: String,
    includeEmptyDefaults: Boolean,
): List<StoreSocialChannel> {
    val handles = mapOf(
        StoreSocialKind.Instagram to instagram,
        StoreSocialKind.Telegram to telegram,
        StoreSocialKind.WhatsApp to whatsapp,
        StoreSocialKind.Website to website,
        StoreSocialKind.Bale to bale,
        StoreSocialKind.Rubika to rubika,
        StoreSocialKind.TikTok to tiktok,
    )
    val defaults = CreateStoreMocks.defaultSocialChannels.map { channel ->
        channel.copy(handle = handles[channel.kind].orEmpty())
    }
    val extras = listOf(
        StoreSocialKind.Bale,
        StoreSocialKind.Rubika,
        StoreSocialKind.TikTok,
    ).mapNotNull { kind ->
        val handle = handles[kind].orEmpty()
        if (handle.isBlank()) {
            null
        } else {
            StoreSocialChannel(id = kind.name.lowercase(), kind = kind, handle = handle)
        }
    }
    val channels = defaults + extras
    return if (includeEmptyDefaults) channels else channels.filter { it.handle.isNotBlank() }
}
