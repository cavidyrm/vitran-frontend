package com.vitran.shop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import com.vitran.shop.feature.content.domain.model.HtmlContent
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.VitranSpacing
import org.koin.compose.koinInject

@Composable
fun SafeHtml(
    html: HtmlContent,
    modifier: Modifier = Modifier,
    externalUrlLauncher: ExternalUrlLauncher = koinInject(),
) {
    val blocks = parseSafeHtml(html.rawHtml)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        blocks.forEach { block ->
            val style = when (block.kind) {
                HtmlBlockKind.H1 -> MaterialTheme.typography.headlineMedium
                HtmlBlockKind.H2 -> MaterialTheme.typography.headlineSmall
                HtmlBlockKind.H3 -> MaterialTheme.typography.titleLarge
                HtmlBlockKind.Paragraph, HtmlBlockKind.ListItem -> MaterialTheme.typography.bodyLarge
            }.copy(color = MaterialTheme.colorScheme.onSurface)
            ClickableText(
                text = block.text,
                style = style,
                onClick = { offset ->
                    block.text.getStringAnnotations(URL_TAG, offset, offset)
                        .firstOrNull()
                        ?.item
                        ?.takeIf(::isSafeWebUrl)
                        ?.let(externalUrlLauncher::open)
                },
            )
        }
    }
}

private enum class HtmlBlockKind { H1, H2, H3, Paragraph, ListItem }

private data class HtmlBlock(
    val kind: HtmlBlockKind,
    val text: AnnotatedString,
)

private fun parseSafeHtml(html: String): List<HtmlBlock> {
    val blocks = mutableListOf<HtmlBlock>()
    val tokens = HTML_TOKEN.findAll(html).map { it.value }.toList()
    var kind = HtmlBlockKind.Paragraph
    var builder = AnnotatedString.Builder()
    val styleStack = mutableListOf<Pair<String, Int>>()
    var href: Pair<String, Int>? = null

    fun flush() {
        val text = builder.toAnnotatedString()
        if (text.text.isNotBlank()) blocks += HtmlBlock(kind, text)
        builder = AnnotatedString.Builder()
        styleStack.clear()
        href = null
    }

    tokens.forEach { token ->
        if (!token.startsWith("<")) {
            builder.append(decodeEntities(token))
            return@forEach
        }
        val closing = token.startsWith("</")
        val name = TAG_NAME.find(token)?.groupValues?.get(1)?.lowercase() ?: return@forEach
        if (!closing && name in BLOCK_TAGS) {
            flush()
            kind = when (name) {
                "h1" -> HtmlBlockKind.H1
                "h2" -> HtmlBlockKind.H2
                "h3" -> HtmlBlockKind.H3
                "li" -> HtmlBlockKind.ListItem
                else -> HtmlBlockKind.Paragraph
            }
            if (name == "li") builder.append("• ")
        } else if (closing && name in BLOCK_TAGS) {
            flush()
            kind = HtmlBlockKind.Paragraph
        } else if (name == "br" && !closing) {
            builder.append('\n')
        } else if (!closing && name in STYLE_TAGS) {
            styleStack += name to builder.length
        } else if (closing && name in STYLE_TAGS) {
            val entry = styleStack.indexOfLast { it.first == name }
                .takeIf { it >= 0 }
                ?.let(styleStack::removeAt)
            if (entry != null && builder.length > entry.second) {
                builder.addStyle(
                    if (name == "strong" || name == "b") {
                        SpanStyle(fontWeight = FontWeight.Bold)
                    } else {
                        SpanStyle(fontStyle = FontStyle.Italic)
                    },
                    entry.second,
                    builder.length,
                )
            }
        } else if (!closing && name == "a") {
            val url = HREF.find(token)?.groupValues?.getOrNull(1)?.let(::decodeEntities)
            if (url != null && isSafeWebUrl(url)) href = url to builder.length
        } else if (closing && name == "a") {
            href?.let { (url, start) ->
                if (builder.length > start) {
                    builder.addStyle(SpanStyle(color = ShopPurple), start, builder.length)
                    builder.addStringAnnotation(URL_TAG, url, start, builder.length)
                }
            }
            href = null
        }
    }
    flush()
    return blocks
}

private fun isSafeWebUrl(url: String): Boolean {
    val normalized = url.trim().lowercase()
    return normalized.startsWith("https://") || normalized.startsWith("http://")
}

private fun decodeEntities(value: String): String =
    value.replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&amp;", "&")

private const val URL_TAG = "safe-url"
private val BLOCK_TAGS = setOf("p", "h1", "h2", "h3", "li")
private val STYLE_TAGS = setOf("strong", "b", "em", "i")
private val HTML_TOKEN = Regex("""<[^>]+>|[^<]+""")
private val TAG_NAME = Regex("""<\s*/?\s*([a-zA-Z0-9]+)""")
private val HREF = Regex("""\bhref\s*=\s*"([^"]*)"""", RegexOption.IGNORE_CASE)
