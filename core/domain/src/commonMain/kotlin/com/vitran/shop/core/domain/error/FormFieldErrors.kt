package com.vitran.shop.core.domain.error

/**
 * Field-level messages ready for form inputs, plus a leftover banner when some
 * API `reason`s do not match a visible field.
 */
data class FormFieldErrorSplit(
    val fieldErrors: Map<String, String>,
    val generalMessage: String?,
)

/**
 * Groups envelope `errors` by lowercase [FieldError.reason], joining multiple
 * messages for the same reason with a newline.
 */
fun List<FieldError>.toMessageMap(): Map<String, String> {
    if (isEmpty()) return emptyMap()
    return groupBy { it.reason.lowercase() }
        .mapValues { (_, errors) ->
            errors.flatMap { it.messages }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .joinToString("\n")
                .ifBlank { errors.first().reason }
        }
}

/**
 * First joined message whose [FieldError.reason] matches any of [reasons]
 * (case-insensitive).
 */
fun List<FieldError>.messageFor(vararg reasons: String): String? {
    if (reasons.isEmpty()) return null
    val keys = reasons.map { it.lowercase() }.toSet()
    return filter { it.reason.lowercase() in keys }
        .flatMap { it.messages }
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
        .joinToString("\n")
        .ifBlank { null }
}

/**
 * Splits field errors into those that match a visible input and a leftover
 * banner message.
 *
 * [knownReasons] are API `reason` strings that map 1:1 to a UI field key
 * (lowercased). [reasonAliases] maps an API reason onto a different UI key
 * (e.g. `category_slug` → `category`).
 *
 * If every reason maps to a field, [FormFieldErrorSplit.generalMessage] is null
 * even when [fallbackMessage] (envelope `message`) is present.
 */
fun List<FieldError>.splitForForm(
    knownReasons: Set<String>,
    reasonAliases: Map<String, String> = emptyMap(),
    fallbackMessage: String? = null,
): FormFieldErrorSplit {
    val knownLower = knownReasons.map { it.lowercase() }.toSet()
    val aliasesLower = reasonAliases.mapKeys { it.key.lowercase() }
        .mapValues { it.value.lowercase() }
    val matched = linkedMapOf<String, String>()
    val unmatched = mutableListOf<String>()

    for ((reason, message) in toMessageMap()) {
        val uiKey = aliasesLower[reason] ?: reason.takeIf { it in knownLower }
        if (uiKey != null) {
            val existing = matched[uiKey]
            matched[uiKey] = if (existing.isNullOrBlank()) message else "$existing\n$message"
        } else {
            unmatched += message
        }
    }

    val generalMessage = when {
        unmatched.isNotEmpty() -> unmatched.joinToString("\n")
        matched.isEmpty() -> fallbackMessage?.trim()?.takeIf { it.isNotEmpty() }
        else -> null
    }
    return FormFieldErrorSplit(fieldErrors = matched, generalMessage = generalMessage)
}

fun AppError.splitForForm(
    knownReasons: Set<String>,
    reasonAliases: Map<String, String> = emptyMap(),
): FormFieldErrorSplit =
    fieldErrors.splitForForm(
        knownReasons = knownReasons,
        reasonAliases = reasonAliases,
        fallbackMessage = message,
    )
