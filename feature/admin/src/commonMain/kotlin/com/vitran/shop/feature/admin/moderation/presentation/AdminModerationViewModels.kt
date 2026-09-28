package com.vitran.shop.feature.admin.moderation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationQuery
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationRepository
import com.vitran.shop.feature.admin.moderation.domain.AdminProductDetails
import com.vitran.shop.feature.admin.moderation.domain.AdminProductSummary
import com.vitran.shop.feature.admin.moderation.domain.AdminShopSummary
import com.vitran.shop.feature.admin.moderation.domain.ConfirmedAdminComment
import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminShopsUiState(
    val query: AdminModerationQuery = AdminModerationQuery(active = false),
    val shops: List<AdminShopSummary> = emptyList(),
    val pendingShopIds: Set<ShopId> = emptySet(),
    val loading: Boolean = false,
    val error: AppError? = null,
)

class AdminShopsViewModel(private val repository: AdminModerationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminShopsUiState())
    val uiState = _uiState.asStateFlow()
    private var loadGeneration = 0
    private var userIdJob: Job? = null

    init { refresh() }

    fun refresh() {
        userIdJob?.cancel()
        load(_uiState.value.query.copy(page = 1))
    }

    fun setActive(active: Boolean?) = applyFilter { it.copy(active = active, page = 1) }

    fun setCityId(cityId: Long?) = applyFilter { it.copy(cityId = cityId, page = 1) }

    fun setCategorySlug(categorySlug: String?) =
        applyFilter { it.copy(categorySlug = categorySlug?.ifBlank { null }, page = 1) }

    fun setUserId(userId: Long?) {
        val query = _uiState.value.query.copy(userId = userId, page = 1)
        if (query == _uiState.value.query) return
        loadGeneration += 1
        _uiState.update { it.copy(query = query, loading = true, error = null) }
        userIdJob?.cancel()
        userIdJob = viewModelScope.launch {
            delay(USER_ID_DEBOUNCE_MS)
            load(_uiState.value.query.copy(page = 1))
        }
    }

    fun clearFilters() {
        userIdJob?.cancel()
        val query = AdminModerationQuery(active = false)
        if (query == _uiState.value.query) return
        load(query)
    }

    fun confirm(id: ShopId) {
        if (id in _uiState.value.pendingShopIds) return
        _uiState.update { it.copy(pendingShopIds = it.pendingShopIds + id, error = null) }
        viewModelScope.launch {
            when (val result = repository.confirmShop(id)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(shops = it.shops.map { shop -> if (shop.id == id) result.value else shop })
                }
                is AppResult.Failure -> _uiState.update { it.copy(error = result.error) }
            }
            _uiState.update { it.copy(pendingShopIds = it.pendingShopIds - id) }
        }
    }

    private fun applyFilter(transform: (AdminModerationQuery) -> AdminModerationQuery) {
        val query = transform(_uiState.value.query)
        if (query == _uiState.value.query) return
        userIdJob?.cancel()
        load(query)
    }

    private fun load(query: AdminModerationQuery) {
        loadGeneration += 1
        val generation = loadGeneration
        _uiState.update { it.copy(query = query, loading = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.getShops(query)) {
                is AppResult.Success -> {
                    if (generation != loadGeneration) return@launch
                    _uiState.update { it.copy(shops = result.value.items, loading = false, query = query) }
                }
                is AppResult.Failure -> {
                    if (generation != loadGeneration) return@launch
                    _uiState.update { it.copy(loading = false, error = result.error, query = query) }
                }
            }
        }
    }

    private companion object {
        const val USER_ID_DEBOUNCE_MS = 400L
    }
}

data class AdminProductsUiState(
    val products: List<AdminProductSummary> = emptyList(),
    val pendingProductIds: Set<ProductId> = emptySet(),
    val loading: Boolean = false,
    val error: AppError? = null,
)

class AdminProductsViewModel(private val repository: AdminModerationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminProductsUiState())
    val uiState = _uiState.asStateFlow()
    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _uiState.update { it.copy(loading = true, error = null) }
        when (val result = repository.getProducts(AdminModerationQuery(active = false))) {
            is AppResult.Success -> _uiState.update { it.copy(products = result.value.items, loading = false) }
            is AppResult.Failure -> _uiState.update { it.copy(loading = false, error = result.error) }
        }
    }

    fun confirm(id: ProductId) {
        if (id in _uiState.value.pendingProductIds) return
        _uiState.update { it.copy(pendingProductIds = it.pendingProductIds + id, error = null) }
        viewModelScope.launch {
            when (val result = repository.confirmProduct(id)) {
                is AppResult.Success -> _uiState.update { state ->
                    state.copy(products = state.products.map {
                        if (it.id == id) it.copy(
                            active = result.value.active, confirmed = result.value.confirmed,
                            publication = result.value.publication,
                        ) else it
                    })
                }
                is AppResult.Failure -> _uiState.update { it.copy(error = result.error) }
            }
            _uiState.update { it.copy(pendingProductIds = it.pendingProductIds - id) }
        }
    }
}

sealed interface AdminProductDetailsUiState {
    data object Loading : AdminProductDetailsUiState
    data class Content(val product: AdminProductDetails, val confirming: Boolean = false) : AdminProductDetailsUiState
    data class Error(val error: AppError) : AdminProductDetailsUiState
}

class AdminProductDetailsViewModel(
    private val productId: ProductId,
    private val repository: AdminModerationRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdminProductDetailsUiState>(AdminProductDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()
    init { load() }
    fun load() = viewModelScope.launch {
        _uiState.value = AdminProductDetailsUiState.Loading
        _uiState.value = when (val result = repository.getProduct(productId)) {
            is AppResult.Success -> AdminProductDetailsUiState.Content(result.value)
            is AppResult.Failure -> AdminProductDetailsUiState.Error(result.error)
        }
    }
    fun confirm() {
        val current = _uiState.value as? AdminProductDetailsUiState.Content ?: return
        if (current.confirming) return
        _uiState.value = current.copy(confirming = true)
        viewModelScope.launch {
            _uiState.value = when (val result = repository.confirmProduct(productId)) {
                is AppResult.Success -> AdminProductDetailsUiState.Content(result.value)
                is AppResult.Failure -> AdminProductDetailsUiState.Error(result.error)
            }
        }
    }
}

data class AdminCommentsUiState(
    val commentIdText: String = "",
    val confirming: Boolean = false,
    val confirmedComment: ConfirmedAdminComment? = null,
    val error: AppError? = null,
)

/** Mutation-only UX: the admin comments contract exposes confirm, but no list endpoint. */
class AdminCommentsViewModel(private val repository: AdminModerationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminCommentsUiState())
    val uiState = _uiState.asStateFlow()
    fun setCommentId(value: String) = _uiState.update { it.copy(commentIdText = value.filter(Char::isDigit)) }
    fun confirm() {
        val id = _uiState.value.commentIdText.toLongOrNull() ?: return
        if (_uiState.value.confirming) return
        _uiState.update { it.copy(confirming = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.confirmComment(ShopCommentId(id))) {
                is AppResult.Success -> _uiState.update { it.copy(confirming = false, confirmedComment = result.value) }
                is AppResult.Failure -> _uiState.update { it.copy(confirming = false, error = result.error) }
            }
        }
    }
}
