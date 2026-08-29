package com.vitran.shop.feature.marketplace.common.data.serializer

import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

/**
 * Accepts string taxonomy slugs or legacy numeric values at the DTO boundary only.
 */
object FlexibleCategorySlugSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleCategorySlug", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        if (decoder is JsonDecoder) {
            return decodeElement(decoder.decodeJsonElement())
        }
        return decoder.decodeString()
    }

    override fun serialize(encoder: Encoder, value: String) {
        if (encoder is JsonEncoder) {
            encoder.encodeJsonElement(JsonPrimitive(value))
        } else {
            encoder.encodeString(value)
        }
    }

    fun decodeElement(element: JsonElement): String =
        when (element) {
            is JsonPrimitive -> {
                element.content.takeIf { it.isNotBlank() }
                    ?: element.longOrNull?.toString()
                    ?: element.intOrNull?.toString()
                    ?: error("Invalid category slug element")
            }
            else -> error("Expected primitive category slug, was $element")
        }

    fun toDomain(value: String): CategorySlug = CategorySlug(value)
}

object FlexibleCategorySlugListSerializer : KSerializer<List<String>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleCategorySlugList", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        if (decoder !is JsonDecoder) error("FlexibleCategorySlugListSerializer requires JSON")
        val array = decoder.decodeJsonElement()
        if (array !is kotlinx.serialization.json.JsonArray) return emptyList()
        return array.map { FlexibleCategorySlugSerializer.decodeElement(it) }
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        if (encoder !is JsonEncoder) error("FlexibleCategorySlugListSerializer requires JSON")
        encoder.encodeJsonElement(
            kotlinx.serialization.json.JsonArray(value.map { JsonPrimitive(it) }),
        )
    }
}
