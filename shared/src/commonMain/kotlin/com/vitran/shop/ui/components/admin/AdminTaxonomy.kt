package com.vitran.shop.ui.components.admin

import androidx.compose.runtime.Immutable

@Immutable
data class AdminTaxonomyNode(
    val id: String,
    val label: String,
    val children: List<AdminTaxonomyNode> = emptyList(),
) {
    val hasChildren: Boolean get() = children.isNotEmpty()
}

@Immutable
data class AdminTaxonomyHit(
    val node: AdminTaxonomyNode,
    val breadcrumb: String,
)

fun List<AdminTaxonomyNode>.findNode(id: String): AdminTaxonomyNode? {
    for (node in this) {
        if (node.id == id) return node
        node.children.findNode(id)?.let { return it }
    }
    return null
}

fun List<AdminTaxonomyNode>.pathTo(id: String): List<AdminTaxonomyNode> {
    fun walk(
        nodes: List<AdminTaxonomyNode>,
        acc: List<AdminTaxonomyNode>,
    ): List<AdminTaxonomyNode>? {
        for (node in nodes) {
            val next = acc + node
            if (node.id == id) return next
            walk(node.children, next)?.let { return it }
        }
        return null
    }
    return walk(this, emptyList()).orEmpty()
}

fun List<AdminTaxonomyNode>.breadcrumbLabel(
    id: String,
    separator: String = " › ",
): String? {
    val path = pathTo(id)
    if (path.isEmpty()) return null
    return path.joinToString(separator) { it.label }
}

fun List<AdminTaxonomyNode>.searchHits(query: String): List<AdminTaxonomyHit> {
    val needle = query.trim()
    if (needle.isEmpty()) return emptyList()
    val hits = mutableListOf<AdminTaxonomyHit>()
    fun walk(nodes: List<AdminTaxonomyNode>, ancestors: List<String>) {
        for (node in nodes) {
            val path = ancestors + node.label
            if (node.label.contains(needle, ignoreCase = true)) {
                hits += AdminTaxonomyHit(
                    node = node,
                    breadcrumb = ancestors.joinToString(" › "),
                )
            }
            walk(node.children, path)
        }
    }
    walk(this, emptyList())
    return hits
}
