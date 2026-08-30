package com.vitran.shop.feature.content.domain.html

import com.vitran.shop.feature.content.domain.model.HtmlContent

/**
 * Small allowlist-based sanitizer suitable for KMP common code.
 * It deliberately discards every tag and attribute that is not explicitly allowed.
 */
class AllowlistHtmlSanitizer : HtmlSanitizer {

    override fun sanitize(html: HtmlContent): HtmlContent {
        val withoutDangerousBlocks = DANGEROUS_BLOCK.replace(html.rawHtml, "")
        val sanitized = TAG.replace(withoutDangerousBlocks) { match ->
            sanitizeTag(match.value)
        }
        return HtmlContent(sanitized)
    }

    private fun sanitizeTag(tag: String): String {
        val parsed = TAG_NAME.find(tag) ?: return ""
        val name = parsed.groupValues[1].lowercase()
        if (name !in ALLOWED_TAGS) return ""

        val closing = tag.startsWith("</")
        if (closing) return if (name == "br") "" else "</$name>"
        if (name == "br") return "<br>"
        if (name != "a") return "<$name>"

        val href = HREF.find(tag)?.groupValues?.drop(1)?.firstOrNull { it.isNotEmpty() }
        return if (href != null && isSafeHttpUrl(href)) {
            """<a href="${escapeAttribute(href)}">"""
        } else {
            "<a>"
        }
    }

    private fun isSafeHttpUrl(value: String): Boolean {
        val normalized = value.trim().lowercase()
        return normalized.startsWith("http://") || normalized.startsWith("https://")
    }

    private fun escapeAttribute(value: String): String =
        value.replace("&", "&amp;").replace("\"", "&quot;")

    private companion object {
        val ALLOWED_TAGS = setOf(
            "p", "h1", "h2", "h3", "ul", "ol", "li",
            "strong", "em", "b", "i", "a", "br", "span",
        )
        val DANGEROUS_BLOCK =
            Regex(
                """<\s*(script|style|iframe|object|embed)\b[^>]*>.*?<\s*/\s*\1\s*>|<\s*(script|style|iframe|object|embed)\b[^>]*/?\s*>""",
                setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
            )
        val TAG = Regex("""<[^>]*>""")
        val TAG_NAME = Regex("""<\s*/?\s*([a-zA-Z0-9]+)""")
        val HREF = Regex(
            """\bhref\s*=\s*(?:"([^"]*)"|'([^']*)'|([^\s>]+))""",
            RegexOption.IGNORE_CASE,
        )
    }
}
