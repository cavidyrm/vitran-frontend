package com.vitran.shop.feature.seller.referral.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditId
import com.vitran.shop.feature.seller.referral.domain.model.ReferralProfile
import com.vitran.shop.feature.seller.referral.domain.repository.ReferralRepository
import com.vitran.shop.feature.seller.referral.domain.usecase.ApplyReferralCreditUseCase
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ReferralsContentState {
    data object Loading : ReferralsContentState()
    data class Content(val profile: ReferralProfile) : ReferralsContentState()
    data class Error(val error: AppError) : ReferralsContentState()
}

sealed class ReferralsUiEffect {
    data class ShareInvite(val inviteUrl: String, val code: String) : ReferralsUiEffect()
}

data class ReferralsUiState(
    val content: ReferralsContentState = ReferralsContentState.Loading,
    val shops: List<SellerShopSummary> = emptyList(),
    val selectedShopId: ShopId? = null,
    val applyingCreditId: ReferralCreditId? = null,
    val applyError: AppError? = null,
    val applySuccessMessage: String? = null,
)

class ReferralsViewModel(
    private val referralRepository: ReferralRepository,
    private val applyReferralCreditUseCase: ApplyReferralCreditUseCase,
    private val sellerShopRepository: SellerShopRepository,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReferralsUiState())
    val uiState: StateFlow<ReferralsUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ReferralsUiEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<ReferralsUiEffect> = _effects.asSharedFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(content = ReferralsContentState.Loading, applyError = null) }
            loadShops()
            when (val result = referralRepository.getProfile(forceRefresh = true)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(content = ReferralsContentState.Content(result.value))
                    }
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(content = ReferralsContentState.Error(result.error))
                    }
                }
            }
        }
    }

    fun selectShop(shopId: ShopId) {
        _uiState.update { it.copy(selectedShopId = shopId) }
    }

    fun shareInvite() {
        val profile =
            (_uiState.value.content as? ReferralsContentState.Content)?.profile ?: return
        viewModelScope.launch {
            _effects.emit(
                ReferralsUiEffect.ShareInvite(
                    inviteUrl = profile.inviteUrl,
                    code = profile.code.value,
                ),
            )
        }
    }

    fun applyCredit(creditId: ReferralCreditId) {
        val shopId = _uiState.value.selectedShopId ?: return
        if (_uiState.value.applyingCreditId != null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(applyingCreditId = creditId, applyError = null, applySuccessMessage = null)
            }
            when (val result = applyReferralCreditUseCase(creditId, shopId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            applyingCreditId = null,
                            content = ReferralsContentState.Content(result.value.profile),
                            applySuccessMessage = "اعتبار با موفقیت اعمال شد",
                        )
                    }
                }
                is AppResult.Failure -> {
                    // Ambiguous timeout: refresh before offering another apply.
                    runCatching { referralRepository.getProfile(forceRefresh = true) }
                    runCatching { subscriptionRepository.refreshSubscription(shopId) }
                    _uiState.update {
                        it.copy(applyingCreditId = null, applyError = result.error)
                    }
                }
            }
        }
    }

    private suspend fun loadShops() {
        when (
            val result =
                sellerShopRepository.getMyShops(
                    SellerShopListQuery(
                        activeFilter = SellerShopFilter.All,
                        pagination = CursorPagination(perPage = 50),
                    ),
                )
        ) {
            is AppResult.Success -> {
                val shops = result.value.items
                _uiState.update {
                    it.copy(
                        shops = shops,
                        selectedShopId = it.selectedShopId ?: shops.firstOrNull()?.id,
                    )
                }
            }
            is AppResult.Failure -> Unit
        }
    }
}
