package com.vitran.shop.feature.taxonomy.domain.model

fun List<CategoryNode>.findBySlug(slug: CategorySlug): CategoryNode? {
    for (node in this) {
        if (node.slug == slug) return node
        node.children.findBySlug(slug)?.let { return it }
    }
    return null
}

fun List<CategoryNode>.collectLeafCategories(): List<CategoryNode> {
    val leaves = mutableListOf<CategoryNode>()
    fun walk(nodes: List<CategoryNode>) {
        for (node in nodes) {
            if (node.isLeaf) {
                leaves += node
            } else {
                walk(node.children)
            }
        }
    }
    walk(this)
    return leaves
}
