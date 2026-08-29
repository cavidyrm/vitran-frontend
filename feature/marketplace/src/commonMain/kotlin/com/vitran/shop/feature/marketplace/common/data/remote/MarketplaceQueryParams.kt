package com.vitran.shop.feature.marketplace.common.data.remote

import com.vitran.shop.feature.marketplace.common.domain.filter.CityFilter
import com.vitran.shop.feature.marketplace.common.domain.filter.ShopFilter
import com.vitran.shop.feature.marketplace.product.domain.catalog.CatalogFilters
import com.vitran.shop.feature.marketplace.product.domain.catalog.CatalogSort
import com.vitran.shop.feature.marketplace.product.domain.catalog.MinimumRating
import com.vitran.shop.feature.marketplace.product.domain.query.ProductBrowseQuery
import com.vitran.shop.feature.marketplace.product.domain.query.ProductSearchQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopBrowseQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopListQuery
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.network.pagination.appendCursorPagination
import io.ktor.http.ParametersBuilder

internal fun ParametersBuilder.appendCityFilter(city: CityFilter?) {
    when (city) {
        is CityFilter.ById -> append("city_id", city.id.value.toString())
        is CityFilter.BySlug -> append("city_slug", city.slug.value)
        null -> Unit
    }
}

internal fun ParametersBuilder.appendShopFilter(shop: ShopFilter?) {
    when (shop) {
        is ShopFilter.ById -> append("shop_id", shop.id.value.toString())
        is ShopFilter.BySlug -> append("shop_slug", shop.slug.value)
        null -> Unit
    }
}

internal fun ParametersBuilder.appendCategorySlug(slug: com.vitran.shop.feature.taxonomy.domain.model.CategorySlug?) {
    slug?.let { append("category_slug", it.value) }
}

internal fun ParametersBuilder.appendShopListQuery(query: ShopListQuery) {
    appendCityFilter(query.city)
    appendCategorySlug(query.categorySlug)
    appendCursorPagination(query.pagination)
}

internal fun ParametersBuilder.appendShopBrowseQuery(query: ShopBrowseQuery) {
    appendCityFilter(query.city)
    appendCategorySlug(query.categorySlug)
    appendCursorPagination(query.pagination)
}

internal fun ParametersBuilder.appendProductBrowseQuery(query: ProductBrowseQuery) {
    appendCityFilter(query.city)
    appendCategorySlug(query.categorySlug)
    appendShopFilter(query.shop)
    appendCursorPagination(query.pagination)
}

internal fun ParametersBuilder.appendProductSearchQuery(query: ProductSearchQuery) {
    append("q", query.query)
    appendCityFilter(query.city)
    appendCategorySlug(query.categorySlug)
    appendShopFilter(query.shop)
    appendCursorPagination(query.pagination)
}

internal fun ParametersBuilder.appendCatalogFilters(filters: CatalogFilters) {
    filters.query?.takeIf { it.isNotBlank() }?.let { append("q", it) }
    appendCityFilter(filters.city)
    if (filters.categories.isNotEmpty()) {
        append("category_slug", filters.categories.joinToString(",") { it.value })
    }
    if (filters.shops.isNotEmpty()) {
        append("shop_id", filters.shops.joinToString(",") { it.value.toString() })
    }
    filters.priceRange?.minimum?.let { append("min_price", it.toString()) }
    filters.priceRange?.maximum?.let { append("max_price", it.toString()) }
    filters.minimumRating?.let { append("min_rating", it.backendValue.toString()) }
    append("sort", filters.sort.toQueryValue())
    appendCursorPagination(filters.pagination)
}

private fun CatalogSort.toQueryValue(): String =
    when (this) {
        CatalogSort.Relevance -> "relevance"
        CatalogSort.Newest -> "newest"
        CatalogSort.PriceAscending -> "price_asc"
        CatalogSort.PriceDescending -> "price_desc"
    }
