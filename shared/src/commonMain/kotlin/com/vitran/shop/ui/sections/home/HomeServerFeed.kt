package com.vitran.shop.ui.sections.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.domain.model.UserEventMetadata
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.home.domain.model.HomeBannerAction
import com.vitran.shop.feature.home.domain.model.HomeRail
import com.vitran.shop.feature.home.domain.model.HomeRailItem
import com.vitran.shop.feature.home.domain.model.HomeRailLayout
import com.vitran.shop.feature.home.domain.model.HomeRailType
import com.vitran.shop.feature.home.domain.model.canLoadMore
import com.vitran.shop.feature.home.domain.model.shouldRender
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.ui.components.CategoriesProductCard
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.sections.categories.CategoriesProduct
import com.vitran.shop.ui.sections.categories.CategoriesProductRow
import com.vitran.shop.ui.sections.categories.CategoriesProductRowSection
import com.vitran.shop.ui.sections.reference.formatMarketplacePrice
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.util.toPersianDigits
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.home_banner_a11y
import vitranshop.shared.generated.resources.home_feed_load_more

private const val PlaceholderImage =
    "https://cdn.shopify.com/s/files/1/0533/2089/files/placeholder-images-image_large.png?width=800"

/**
 * Renders `GET /home/screen` rails in order. Unknown type/layout pairs are skipped.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeServerFeed(
    sections: List<HomeRail>,
    loadingMoreKeys: Set<String>,
    onProductOpen: (id: String, title: String, imageUrl: String, priceLabel: String) -> Unit,
    onShopOpen: (shopId: String) -> Unit,
    onCategoryOpen: () -> Unit,
    onLoadMore: (sectionKey: String) -> Unit,
    modifier: Modifier = Modifier,
    externalUrlLauncher: ExternalUrlLauncher = koinInject(),
    analyticsTracker: MarketplaceAnalyticsTracker = koinInject(),
) {
    val visible = sections.filter { it.shouldRender() }
    if (visible.isEmpty()) return
    val scope = rememberCoroutineScope()
    val requesters = remember(visible.map { it.key }) {
        visible.associate { it.key to BringIntoViewRequester() }
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xxxl),
    ) {
        visible.forEach { rail ->
            val requester = requesters.getValue(rail.key)
            HomeRailBlock(
                rail = rail,
                loadingMore = rail.key in loadingMoreKeys,
                onProductOpen = { id, title, imageUrl, priceLabel ->
                    trackHomeProduct(analyticsTracker, rail, id)
                    onProductOpen(id, title, imageUrl, priceLabel)
                },
                onShopOpen = onShopOpen,
                onCategoryOpen = { category ->
                    if (category.id.isNotBlank()) {
                        analyticsTracker.track(
                            UserPersonalizationEvent.ClickCategory(CategorySlug(category.id)),
                        )
                    }
                    onCategoryOpen()
                },
                onLoadMore = { onLoadMore(rail.key) },
                onBannerAction = { action ->
                    when (action) {
                        is HomeBannerAction.OpenProduct -> {
                            trackHomeBannerProduct(analyticsTracker, rail, action)
                            onProductOpen(action.productId, "", "", "")
                        }
                        is HomeBannerAction.OpenShop -> onShopOpen(action.shopId)
                        is HomeBannerAction.OpenCategory -> onCategoryOpen()
                        is HomeBannerAction.OpenSection -> {
                            val target = requesters[action.sectionKey]
                            if (target != null) {
                                scope.launch { target.bringIntoView() }
                            }
                        }
                        is HomeBannerAction.OpenUrl -> externalUrlLauncher.open(action.url)
                        HomeBannerAction.None -> Unit
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(requester),
            )
        }
    }
}

@Composable
private fun HomeRailBlock(
    rail: HomeRail,
    loadingMore: Boolean,
    onProductOpen: (id: String, title: String, imageUrl: String, priceLabel: String) -> Unit,
    onShopOpen: (shopId: String) -> Unit,
    onCategoryOpen: (HomeCategory) -> Unit,
    onLoadMore: () -> Unit,
    onBannerAction: (HomeBannerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        when (rail.type) {
            HomeRailType.Categories -> CategoryChips(rail, onCategoryOpen)
            HomeRailType.Products -> when (rail.layout) {
                HomeRailLayout.HorizontalRail -> ProductRail(rail, onProductOpen, onLoadMore)
                HomeRailLayout.Grid -> ProductGrid(rail, onProductOpen)
                else -> Unit
            }
            HomeRailType.Banners -> BannerHero(rail, onBannerAction)
            HomeRailType.Shops -> ShopRail(rail, onShopOpen, onProductOpen, onLoadMore)
            HomeRailType.Unknown -> Unit
        }
        if (rail.canLoadMore() && rail.type == HomeRailType.Products) {
            LoadMoreRow(loading = loadingMore, onClick = onLoadMore)
        }
    }
}

@Composable
private fun CategoryChips(rail: HomeRail, onCategoryOpen: (HomeCategory) -> Unit) {
    val categories = rail.items.results.mapNotNull { item ->
        val category = item as? HomeRailItem.Category ?: return@mapNotNull null
        HomeCategory(id = category.slug, title = category.title, imageUrl = "")
    }
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        RailHeading(rail)
        HomeCategoriesRow(
            categories = categories,
            onCategoryClick = onCategoryOpen,
            topPadding = 0.dp,
        )
    }
}

@Composable
private fun ProductRail(
    rail: HomeRail,
    onProductOpen: (id: String, title: String, imageUrl: String, priceLabel: String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val products = rail.productCards()
    CategoriesProductRow(
        section = CategoriesProductRowSection(
            id = rail.key,
            title = rail.title.orEmpty(),
            products = products,
        ),
        subtitle = rail.subtitle,
        onSectionClick = { if (rail.canLoadMore()) onLoadMore() },
        onProductClick = { product ->
            onProductOpen(product.id, product.title, product.imageUrl, product.priceLabel)
        },
    )
}

@Composable
private fun ProductGrid(
    rail: HomeRail,
    onProductOpen: (id: String, title: String, imageUrl: String, priceLabel: String) -> Unit,
) {
    val products = rail.productCards()
    val isDesktop = LocalDesktopLayout.current
    val horizontalPad = if (isDesktop) VitranSpacing.xxxl + VitranSpacing.lg else VitranSpacing.lg
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
        RailHeading(rail, horizontalPad)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPad)) {
            val columns = when {
                maxWidth >= 1024.dp -> 4
                maxWidth >= 600.dp -> 3
                else -> 2
            }
            val gap = VitranSpacing.sm
            val cardWidth = (maxWidth - gap * (columns - 1)) / columns
            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                products.chunked(columns).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                        row.forEach { product ->
                            CategoriesProductCard(
                                product = product,
                                cardWidth = cardWidth,
                                showStoreName = false,
                                onClick = {
                                    onProductOpen(
                                        product.id,
                                        product.title,
                                        product.imageUrl,
                                        product.priceLabel,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BannerHero(
    rail: HomeRail,
    onBannerAction: (HomeBannerAction) -> Unit,
) {
    val banners = rail.items.results.filterIsInstance<HomeRailItem.Banner>()
    val isDesktop = LocalDesktopLayout.current
    val horizontalPad = if (isDesktop) VitranSpacing.xxxl + VitranSpacing.lg else VitranSpacing.lg
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
        RailHeading(rail, horizontalPad)
        LazyRow(
            contentPadding = PaddingValues(horizontal = horizontalPad),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            items(banners, key = { it.stableId }) { banner ->
                val label = stringResource(Res.string.home_banner_a11y, banner.title ?: rail.title.orEmpty())
                val clickable = banner.action != HomeBannerAction.None
                AsyncImage(
                    model = resolveNetworkImageUrl(banner.imageUrl),
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(if (isDesktop) 280.dp else 180.dp)
                        .aspectRatio(16f / 7f)
                        .clip(RoundedCornerShape(VitranRadius.xl))
                        .then(
                            if (clickable) {
                                Modifier
                                    .clickable(role = Role.Button) { onBannerAction(banner.action) }
                                    .semantics { contentDescription = label }
                            } else {
                                Modifier
                            },
                        ),
                )
            }
        }
    }
}

@Composable
private fun ShopRail(
    rail: HomeRail,
    onShopOpen: (shopId: String) -> Unit,
    onProductOpen: (id: String, title: String, imageUrl: String, priceLabel: String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val shops = rail.items.results.mapNotNull { item ->
        val shop = item as? HomeRailItem.Shop ?: return@mapNotNull null
        shop.toSpotlightCard()
    }
    HomeCategoryShopsRow(
        section = HomeCategoryShopSection(
            id = rail.key,
            title = rail.title.orEmpty(),
            shops = shops,
        ),
        onCategoryClick = { if (rail.canLoadMore()) onLoadMore() },
        onShopClick = { shop -> onShopOpen(shop.id) },
        onProductClick = { _, peek ->
            onProductOpen(peek.id, peek.title, peek.imageUrl, peek.priceLabel)
        },
    )
}

private fun HomeRailItem.Shop.toSpotlightCard(): HomeShopCard =
    HomeShopCard(
        id = id.toString(),
        name = title,
        ratingLabel = formatShopRating(ratingAvg, ratingCount),
        coverUrl = avatarUrl.orEmpty(),
        logoUrl = "",
        brandColor = ShopCoverFallback,
        useLightText = false,
        products = products.map { product ->
            HomeShopProductPeek(
                id = product.id.toString(),
                imageUrl = product.imageUrl,
                title = product.title,
                priceLabel = product.priceAmount?.let(::formatMarketplacePrice).orEmpty(),
            )
        },
    )

private val ShopCoverFallback = Color(0xFFE7E5E4)

private fun formatShopRating(average: Double?, count: Int?): String {
    if (average == null || count == null || count <= 0 || average <= 0.0) return ""
    val tenths = kotlin.math.round(average * 10).toInt()
    val whole = tenths / 10
    val fraction = kotlin.math.abs(tenths % 10)
    val label = if (fraction == 0) whole.toString() else "$whole.$fraction"
    return "${toPersianDigits(label).replace('.', '٫')} (${toPersianDigits(count.toString())})"
}

@Composable
private fun RailHeading(rail: HomeRail, horizontalPad: androidx.compose.ui.unit.Dp = VitranSpacing.lg) {
    if (rail.title.isNullOrBlank() && rail.subtitle.isNullOrBlank()) return
    Column(
        modifier = Modifier.padding(horizontal = horizontalPad),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        rail.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        rail.subtitle?.takeIf { it.isNotBlank() }?.let { subtitle ->
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun LoadMoreRow(loading: Boolean, onClick: () -> Unit) {
    val label = stringResource(Res.string.home_feed_load_more)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = VitranSpacing.lg),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.height(20.dp))
        } else {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clickable(role = Role.Button, onClick = onClick)
                    .padding(VitranSpacing.sm),
            )
        }
    }
}

private fun trackHomeProduct(
    tracker: MarketplaceAnalyticsTracker,
    rail: HomeRail,
    productId: String,
) {
    val products = rail.items.results.filterIsInstance<HomeRailItem.Product>()
    val index = products.indexOfFirst { it.id.toString() == productId }
    val product = products.getOrNull(index) ?: return
    tracker.track(
        UserPersonalizationEvent.ViewProduct(
            productId = ProductId(product.id),
            shopId = product.shopId?.let(::ShopId),
            categorySlug = product.categorySlug?.let(::CategorySlug),
            metadata = homeMetadata(rail, index + 1),
        ),
    )
}

private fun trackHomeBannerProduct(
    tracker: MarketplaceAnalyticsTracker,
    rail: HomeRail,
    action: HomeBannerAction.OpenProduct,
) {
    val productId = action.productId.toLongOrNull() ?: return
    val index = rail.items.results.indexOfFirst { item ->
        item is HomeRailItem.Banner && item.action == action
    }
    tracker.track(
        UserPersonalizationEvent.ViewProduct(
            productId = ProductId(productId),
            metadata = homeMetadata(rail, if (index >= 0) index + 1 else rail.tracking?.position),
        ),
    )
}

private fun homeMetadata(rail: HomeRail, position: Int?): UserEventMetadata =
    UserEventMetadata(
        from = "home",
        sectionKey = rail.tracking?.sectionKey?.takeIf { it.isNotBlank() } ?: rail.key,
        position = position,
    )

private fun HomeRail.productCards(): List<CategoriesProduct> =
    items.results.mapNotNull { item ->
        val product = item as? HomeRailItem.Product ?: return@mapNotNull null
        val price = product.priceAmount
        val priceLabel = price?.let(::formatMarketplacePrice).orEmpty()
        val compare = product.compareAtPriceAmount
            ?.takeIf { amount -> price != null && amount > price }
            ?.let(::formatMarketplacePrice)
        CategoriesProduct(
            id = product.id.toString(),
            storeName = "",
            title = product.title.orEmpty(),
            imageUrl = product.imageUrl ?: PlaceholderImage,
            rating = product.ratingAvg?.toFloat(),
            reviewCountLabel = product.ratingCount?.takeIf { it > 0 }?.toString(),
            priceLabel = priceLabel,
            compareAtPriceLabel = compare,
        )
    }
