package com.vitran.shop.feature.seller.boost.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.boost.domain.model.BoostTarget
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Boost purchase is blocked until an authoritative pricing contract exists.
 * [submit] must never call [com.vitran.shop.feature.seller.boost.domain.usecase.CreatePlacementBoostUseCase].
 */
sealed class BoostPricingState {
    data object PricingContractUnresolved : BoostPricingState()
}

data class CreateBoostUiState(
    val shops: List<SellerShopSummary> = emptyList(),
    val selectedShopId: ShopId? = null,
    val target: BoostTarget = BoostTarget.Shop,
    val selectedProductId: ProductId? = null,
    val pricing: BoostPricingState = BoostPricingState.PricingContractUnresolved,
    val isSubmitting: Boolean = false,
    val shopsError: AppError? = null,
)

class CreateBoostViewModel(
    private val sellerShopRepository: SellerShopRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateBoostUiState())
    val uiState: StateFlow<CreateBoostUiState> = _uiState.asStateFlow()

    init {
        loadShops()
    }

    fun loadShops() {
        viewModelScope.launch {
            when (
                val result =
                    sellerShopRepository.getMyShops(
                        SellerShopListQuery(
                            activeFilter = SellerShopFilter.All,
                            pagination = CursorPagination(perPage = 50),
                        ),
                    )
            ) {
                is AppResult.Failure ->
                    _uiState.update { it.copy(shopsError = result.error) }
                is AppResult.Success -> {
                    val shops = result.value.items
                    _uiState.update {
                        it.copy(
                            shops = shops,
                            selectedShopId = shops.firstOrNull()?.id,
                            shopsError = null,
                        )
                    }
                }
            }
        }
    }

    fun selectShop(shopId: ShopId) {
        _uiState.update { it.copy(selectedShopId = shopId, selectedProductId = null) }
    }

    fun selectShopTarget() {
        _uiState.update { it.copy(target = BoostTarget.Shop, selectedProductId = null) }
    }

    fun selectProductTarget(productId: ProductId) {
        _uiState.update {
            it.copy(target = BoostTarget.Product(productId), selectedProductId = productId)
        }
    }

    /** Intentionally a no-op: Postman sample `price_paid` is not a pricing policy. */
    fun submit() = Unit
}
