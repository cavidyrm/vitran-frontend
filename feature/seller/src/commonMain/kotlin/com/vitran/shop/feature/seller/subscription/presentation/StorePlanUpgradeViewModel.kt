package com.vitran.shop.feature.seller.subscription.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopPublicCacheInvalidator
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanSummary
import com.vitran.shop.feature.seller.plan.domain.repository.PlanRepository
import com.vitran.shop.feature.seller.referral.domain.repository.ReferralRepository
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.subscription.domain.PaymentUrlValidator
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentFlowState
import com.vitran.shop.feature.seller.subscription.domain.model.PurchasePlanCommand
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository
import com.vitran.shop.feature.seller.subscription.domain.usecase.PaymentVerificationResult
import com.vitran.shop.feature.seller.subscription.domain.usecase.PurchasePlanUseCase
import com.vitran.shop.feature.seller.subscription.domain.usecase.VerifyPendingPaymentUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class StorePlanUpgradeUiEffect {
    data class OpenExternalUrl(val url: String) : StorePlanUpgradeUiEffect()
}

data class StorePlanUpgradeUiState(
    val shopsLoading: Boolean = true,
    val shops: List<SellerShopSummary> = emptyList(),
    val selectedShopId: ShopId? = null,
    val storeName: String = "",
    val plansLoading: Boolean = true,
    val plans: List<PlanSummary> = emptyList(),
    val currentSubscription: ShopSubscription? = null,
    val plansError: AppError? = null,
    val shopsError: AppError? = null,
    val payment: PaymentFlowState = PaymentFlowState.Idle,
    val purchasingPlanId: PlanId? = null,
)

class StorePlanUpgradeViewModel(
    private val sellerShopRepository: SellerShopRepository,
    private val planRepository: PlanRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val purchasePlanUseCase: PurchasePlanUseCase,
    private val verifyPendingPaymentUseCase: VerifyPendingPaymentUseCase,
    private val referralRepository: ReferralRepository,
    private val shopPublicCacheInvalidator: ShopPublicCacheInvalidator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StorePlanUpgradeUiState())
    val uiState: StateFlow<StorePlanUpgradeUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<StorePlanUpgradeUiEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<StorePlanUpgradeUiEffect> = _effects.asSharedFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(shopsLoading = true, plansLoading = true, shopsError = null, plansError = null)
            }
            loadShops()
            loadPlans()
            _uiState.value.selectedShopId?.let { loadSubscription(it) }
        }
    }

    fun selectShop(shopId: ShopId) {
        val shop = _uiState.value.shops.firstOrNull { it.id == shopId } ?: return
        _uiState.update {
            it.copy(selectedShopId = shopId, storeName = shop.title, payment = PaymentFlowState.Idle)
        }
        viewModelScope.launch { loadSubscription(shopId) }
    }

    fun purchasePlan(planId: PlanId) {
        val shopId = _uiState.value.selectedShopId ?: return
        val payment = _uiState.value.payment
        if (payment is PaymentFlowState.Initiating ||
            payment is PaymentFlowState.AwaitingExternalPayment ||
            payment is PaymentFlowState.Verifying
        ) {
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(payment = PaymentFlowState.Initiating, purchasingPlanId = planId)
            }
            when (val result = purchasePlanUseCase(PurchasePlanCommand(shopId, planId))) {
                is AppResult.Success -> {
                    val session = result.value.session
                    if (!PaymentUrlValidator.isSafeToLaunch(session.paymentUrl)) {
                        _uiState.update {
                            it.copy(
                                payment = PaymentFlowState.Error(
                                    message = "آدرس پرداخت نامعتبر است",
                                    session = session,
                                    targetPlanId = planId,
                                    baseline = result.value.baseline,
                                ),
                                purchasingPlanId = null,
                            )
                        }
                        return@launch
                    }
                    _uiState.update {
                        it.copy(
                            payment = PaymentFlowState.AwaitingExternalPayment(
                                session = session,
                                targetPlanId = planId,
                                baseline = result.value.baseline,
                            ),
                            purchasingPlanId = null,
                        )
                    }
                    _effects.emit(StorePlanUpgradeUiEffect.OpenExternalUrl(session.paymentUrl))
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            payment = PaymentFlowState.Error(message = result.error.message),
                            purchasingPlanId = null,
                        )
                    }
                }
            }
        }
    }

    /** Retry opening an already-created payment session without a new POST. */
    fun retryOpenPaymentUrl() {
        val awaiting = _uiState.value.payment
        val session =
            when (awaiting) {
                is PaymentFlowState.AwaitingExternalPayment -> awaiting.session
                is PaymentFlowState.NotYetConfirmed -> awaiting.session
                is PaymentFlowState.Error -> awaiting.session
                else -> null
            } ?: return
        if (!PaymentUrlValidator.isSafeToLaunch(session.paymentUrl)) return
        viewModelScope.launch {
            _effects.emit(StorePlanUpgradeUiEffect.OpenExternalUrl(session.paymentUrl))
        }
    }

    fun onPaymentUrlLaunchFailed() {
        val current = _uiState.value.payment
        if (current is PaymentFlowState.AwaitingExternalPayment) {
            _uiState.update {
                it.copy(
                    payment = PaymentFlowState.Error(
                        message = "باز کردن صفحه پرداخت ممکن نشد",
                        session = current.session,
                        targetPlanId = current.targetPlanId,
                        baseline = current.baseline,
                    ),
                )
            }
        }
    }

    fun onAppResumed() {
        verifyPendingPayment()
    }

    fun verifyPendingPayment() {
        val shopId = _uiState.value.selectedShopId ?: return
        val pending = _uiState.value.payment
        val (session, targetPlanId, baseline) =
            when (pending) {
                is PaymentFlowState.AwaitingExternalPayment ->
                    Triple(pending.session, pending.targetPlanId, pending.baseline)
                is PaymentFlowState.NotYetConfirmed ->
                    Triple(pending.session, pending.targetPlanId, pending.baseline)
                is PaymentFlowState.Error -> {
                    val s = pending.session ?: return
                    val t = pending.targetPlanId ?: return
                    Triple(s, t, pending.baseline)
                }
                else -> return
            }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    payment = PaymentFlowState.Verifying(session, targetPlanId, baseline),
                )
            }
            when (
                val result =
                    verifyPendingPaymentUseCase(shopId, targetPlanId, baseline)
            ) {
                is PaymentVerificationResult.Confirmed -> {
                    _uiState.update {
                        it.copy(
                            payment = PaymentFlowState.Confirmed(result.subscription),
                            currentSubscription = result.subscription,
                        )
                    }
                    runCatching { shopPublicCacheInvalidator.invalidate(shopId) }
                    // Best-effort referral refresh after confirmed purchase.
                    runCatching { referralRepository.getProfile(forceRefresh = true) }
                }
                is PaymentVerificationResult.NotYetConfirmed -> {
                    _uiState.update {
                        it.copy(
                            payment = PaymentFlowState.NotYetConfirmed(
                                session = session,
                                targetPlanId = targetPlanId,
                                baseline = baseline,
                            ),
                            currentSubscription = result.subscription,
                        )
                    }
                }
                is PaymentVerificationResult.VerificationError -> {
                    _uiState.update {
                        it.copy(
                            payment = PaymentFlowState.AwaitingExternalPayment(
                                session = session,
                                targetPlanId = targetPlanId,
                                baseline = baseline,
                            ),
                        )
                    }
                }
            }
        }
    }

    fun clearPaymentState() {
        _uiState.update { it.copy(payment = PaymentFlowState.Idle) }
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
                val selected = shops.firstOrNull()
                _uiState.update {
                    it.copy(
                        shopsLoading = false,
                        shops = shops,
                        selectedShopId = selected?.id,
                        storeName = selected?.title.orEmpty(),
                        shopsError =
                            if (shops.isEmpty()) {
                                AppError.Validation(message = "فروشگاهی یافت نشد")
                            } else {
                                null
                            },
                    )
                }
            }
            is AppResult.Failure -> {
                _uiState.update {
                    it.copy(shopsLoading = false, shopsError = result.error)
                }
            }
        }
    }

    private suspend fun loadPlans() {
        when (val result = planRepository.getPlans()) {
            is AppResult.Success -> {
                _uiState.update {
                    it.copy(plansLoading = false, plans = result.value, plansError = null)
                }
            }
            is AppResult.Failure -> {
                _uiState.update {
                    it.copy(plansLoading = false, plansError = result.error)
                }
            }
        }
    }

    private suspend fun loadSubscription(shopId: ShopId) {
        when (val result = subscriptionRepository.getSubscription(shopId)) {
            is AppResult.Success -> {
                _uiState.update { it.copy(currentSubscription = result.value) }
            }
            is AppResult.Failure -> {
                // Plans can still display; subscription highlight optional.
            }
        }
    }
}
