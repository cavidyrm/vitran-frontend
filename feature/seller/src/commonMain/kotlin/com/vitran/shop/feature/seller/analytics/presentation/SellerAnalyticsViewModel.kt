package com.vitran.shop.feature.seller.analytics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.FileSaveResult
import com.vitran.shop.core.platform.file.FileSaver
import com.vitran.shop.core.platform.file.analyticsExportFallbackFileName
import com.vitran.shop.core.platform.file.sanitizeDownloadFileName
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsPeriod
import com.vitran.shop.feature.seller.analytics.domain.usecase.ExportSellerAnalyticsUseCase
import com.vitran.shop.feature.seller.plan.domain.model.PlanCapabilities
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository
import com.vitran.shop.feature.seller.subscription.domain.usecase.GetShopEntitlementsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class SellerAnalyticsDashboardState {
    /** GET dashboard schema is unresolved — never display fake metrics. */
    data object ContractUnresolved : SellerAnalyticsDashboardState()
}

sealed class AnalyticsExportState {
    data object Idle : AnalyticsExportState()
    data object Downloading : AnalyticsExportState()
    data object Saving : AnalyticsExportState()
    data object Saved : AnalyticsExportState()
    data class DownloadError(val error: AppError) : AnalyticsExportState()
    data class SaveFailed(val message: String? = null) : AnalyticsExportState()
}

sealed class SellerAnalyticsUiEffect {
    data class OpenPlans(val shopId: ShopId) : SellerAnalyticsUiEffect()
}

data class SellerAnalyticsUiState(
    val shops: List<SellerShopSummary> = emptyList(),
    val selectedShopId: ShopId? = null,
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.ThirtyDays,
    val shopsLoading: Boolean = true,
    val shopsError: AppError? = null,
    val dashboard: SellerAnalyticsDashboardState = SellerAnalyticsDashboardState.ContractUnresolved,
    val exportState: AnalyticsExportState = AnalyticsExportState.Idle,
    val advancedAnalytics: Boolean = false,
)

class SellerAnalyticsViewModel(
    private val sellerShopRepository: SellerShopRepository,
    private val exportSellerAnalytics: ExportSellerAnalyticsUseCase,
    private val getShopEntitlements: GetShopEntitlementsUseCase,
    private val subscriptionRepository: SubscriptionRepository,
    private val fileSaver: FileSaver,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SellerAnalyticsUiState())
    val uiState: StateFlow<SellerAnalyticsUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<SellerAnalyticsUiEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<SellerAnalyticsUiEffect> = _effects.asSharedFlow()

    private var exportJob: Job? = null

    init {
        loadShops()
        viewModelScope.launch {
            subscriptionRepository.subscriptionsByShopId.collect {
                val shopId = _uiState.value.selectedShopId ?: return@collect
                refreshEntitlements(shopId)
            }
        }
    }

    fun loadShops() {
        viewModelScope.launch {
            _uiState.update { it.copy(shopsLoading = true, shopsError = null) }
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
                    _uiState.update {
                        it.copy(shopsLoading = false, shopsError = result.error)
                    }
                is AppResult.Success -> {
                    val shops = result.value.items
                    val selected = shops.firstOrNull()
                    _uiState.update {
                        it.copy(
                            shops = shops,
                            selectedShopId = selected?.id,
                            shopsLoading = false,
                        )
                    }
                    selected?.id?.let { refreshEntitlements(it) }
                }
            }
        }
    }

    fun selectShop(shopId: ShopId) {
        if (_uiState.value.selectedShopId == shopId) return
        cancelExportSilently()
        _uiState.update {
            it.copy(
                selectedShopId = shopId,
                exportState = AnalyticsExportState.Idle,
            )
        }
        viewModelScope.launch { refreshEntitlements(shopId) }
    }

    fun selectPeriod(period: AnalyticsPeriod) {
        if (_uiState.value.selectedPeriod == period) return
        cancelExportSilently()
        _uiState.update { it.copy(selectedPeriod = period, exportState = AnalyticsExportState.Idle) }
    }

    fun requestExport() {
        val shopId = _uiState.value.selectedShopId ?: return
        val period = _uiState.value.selectedPeriod
        val current = _uiState.value.exportState
        if (current is AnalyticsExportState.Downloading || current is AnalyticsExportState.Saving) {
            return
        }
        exportJob?.cancel()
        exportJob =
            viewModelScope.launch {
                _uiState.update { it.copy(exportState = AnalyticsExportState.Downloading) }
                val download =
                    try {
                        exportSellerAnalytics(shopId, period)
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    }
                when (download) {
                    is AppResult.Failure ->
                        _uiState.update {
                            it.copy(exportState = AnalyticsExportState.DownloadError(download.error))
                        }
                    is AppResult.Success -> {
                        _uiState.update { it.copy(exportState = AnalyticsExportState.Saving) }
                        val suggested =
                            sanitizeDownloadFileName(
                                download.value.serverSuggestedFileName,
                                analyticsExportFallbackFileName(shopId.value, period.fileNameToken()),
                            )
                        val mime = download.value.contentType?.substringBefore(';')?.trim()
                            ?.takeIf { it.isNotBlank() && !it.equals("text/html", ignoreCase = true) }
                            ?: "text/csv"
                        when (val save = fileSaver.saveFile(suggested, mime, download.value.bytes)) {
                            FileSaveResult.Saved ->
                                _uiState.update { it.copy(exportState = AnalyticsExportState.Saved) }
                            FileSaveResult.Cancelled ->
                                _uiState.update { it.copy(exportState = AnalyticsExportState.Idle) }
                            is FileSaveResult.Failed ->
                                _uiState.update {
                                    it.copy(exportState = AnalyticsExportState.SaveFailed(save.message))
                                }
                        }
                    }
                }
            }
    }

    fun openPlans() {
        val shopId = _uiState.value.selectedShopId ?: return
        _effects.tryEmit(SellerAnalyticsUiEffect.OpenPlans(shopId))
    }

    fun acknowledgeExportSaved() {
        if (_uiState.value.exportState is AnalyticsExportState.Saved) {
            _uiState.update { it.copy(exportState = AnalyticsExportState.Idle) }
        }
    }

    private fun cancelExportSilently() {
        exportJob?.cancel()
        exportJob = null
        if (_uiState.value.exportState is AnalyticsExportState.Downloading ||
            _uiState.value.exportState is AnalyticsExportState.Saving
        ) {
            _uiState.update { it.copy(exportState = AnalyticsExportState.Idle) }
        }
    }

    private suspend fun refreshEntitlements(shopId: ShopId) {
        val advanced =
            when (val result = getShopEntitlements(shopId, forceRefresh = false)) {
                is AppResult.Success -> result.value.capabilities.advancedAnalytics
                is AppResult.Failure -> PlanCapabilities().advancedAnalytics
            }
        if (_uiState.value.selectedShopId == shopId) {
            _uiState.update { it.copy(advancedAnalytics = advanced) }
        }
    }
}

private fun AnalyticsPeriod.fileNameToken(): String =
    when (this) {
        AnalyticsPeriod.SevenDays -> "7d"
        AnalyticsPeriod.ThirtyDays -> "30d"
    }
