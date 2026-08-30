package com.vitran.shop.feature.seller.analytics.presentation

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.FileSaveResult
import com.vitran.shop.core.platform.file.FileSaver
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsExport
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsPeriod
import com.vitran.shop.feature.seller.analytics.domain.repository.SellerAnalyticsRepository
import com.vitran.shop.feature.seller.analytics.domain.usecase.ExportSellerAnalyticsUseCase
import com.vitran.shop.feature.seller.plan.domain.model.PlanCapabilities
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanLimits
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import com.vitran.shop.feature.seller.plan.domain.repository.PlanRepository
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentSession
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.feature.seller.subscription.domain.model.SubscriptionPlan
import com.vitran.shop.feature.seller.subscription.domain.model.SubscriptionStatus
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository
import com.vitran.shop.feature.seller.subscription.domain.usecase.GetShopEntitlementsUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class SellerAnalyticsViewModelTest {

    @Test
    fun dashboard_isContractUnresolved() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm = createVm()
            advanceUntilIdle()
            assertIs<SellerAnalyticsDashboardState.ContractUnresolved>(vm.uiState.value.dashboard)
            assertEquals(ShopId(1), vm.uiState.value.selectedShopId)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun export_saved_recordsFilenameAndBytes() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val saver = RecordingFileSaver()
            val bytes = "a,b\n".encodeToByteArray()
            val vm =
                createVm(
                    exportRepo = FakeAnalyticsRepo(AppResult.Success(AnalyticsExport(bytes, "text/csv", "export.csv"))),
                    fileSaver = saver,
                )
            advanceUntilIdle()
            vm.requestExport()
            advanceUntilIdle()
            assertIs<AnalyticsExportState.Saved>(vm.uiState.value.exportState)
            assertEquals("export.csv", saver.lastName)
            assertEquals("text/csv", saver.lastMime)
            assertTrue(saver.lastBytes.contentEquals(bytes))
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun export_userCancel_returnsIdle_notServerError() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm =
                createVm(
                    exportRepo =
                        FakeAnalyticsRepo(
                            AppResult.Success(AnalyticsExport(byteArrayOf(1), "text/csv", null)),
                        ),
                    fileSaver = RecordingFileSaver(result = FileSaveResult.Cancelled),
                )
            advanceUntilIdle()
            vm.requestExport()
            advanceUntilIdle()
            assertIs<AnalyticsExportState.Idle>(vm.uiState.value.exportState)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun export_saveFailure_doesNotClearDashboard() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm =
                createVm(
                    exportRepo =
                        FakeAnalyticsRepo(
                            AppResult.Success(AnalyticsExport(byteArrayOf(1), "text/csv", null)),
                        ),
                    fileSaver = RecordingFileSaver(result = FileSaveResult.Failed("disk")),
                )
            advanceUntilIdle()
            vm.requestExport()
            advanceUntilIdle()
            assertIs<AnalyticsExportState.SaveFailed>(vm.uiState.value.exportState)
            assertIs<SellerAnalyticsDashboardState.ContractUnresolved>(vm.uiState.value.dashboard)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun export_downloadFailure_keepsDashboard() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm =
                createVm(
                    exportRepo = FakeAnalyticsRepo(AppResult.Failure(AppError.Forbidden(message = "no"))),
                )
            advanceUntilIdle()
            vm.requestExport()
            advanceUntilIdle()
            val err = assertIs<AnalyticsExportState.DownloadError>(vm.uiState.value.exportState)
            assertIs<AppError.Forbidden>(err.error)
            assertIs<SellerAnalyticsDashboardState.ContractUnresolved>(vm.uiState.value.dashboard)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun duplicateExport_whileDownloading_isIgnored() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo = FakeAnalyticsRepo(delay = true)
            val vm = createVm(exportRepo = repo)
            advanceUntilIdle()
            vm.requestExport()
            runCurrent()
            vm.requestExport()
            assertEquals(1, repo.exportCalls)
            repo.release()
            advanceUntilIdle()
            assertEquals(1, repo.exportCalls)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun shopSwitch_cancelsInFlightExport() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repo = FakeAnalyticsRepo(delay = true)
            val vm =
                createVm(
                    shops =
                        listOf(
                            SellerShopSummary(ShopId(1), "A", true, true),
                            SellerShopSummary(ShopId(2), "B", true, true),
                        ),
                    exportRepo = repo,
                )
            advanceUntilIdle()
            vm.requestExport()
            runCurrent()
            vm.selectShop(ShopId(2))
            repo.release()
            advanceUntilIdle()
            assertEquals(ShopId(2), vm.uiState.value.selectedShopId)
            assertIs<AnalyticsExportState.Idle>(vm.uiState.value.exportState)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun periodChange_updatesSelection() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm = createVm()
            advanceUntilIdle()
            vm.selectPeriod(AnalyticsPeriod.SevenDays)
            assertEquals(AnalyticsPeriod.SevenDays, vm.uiState.value.selectedPeriod)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun openPlans_emitsShopId() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm = createVm()
            advanceUntilIdle()
            var effect: SellerAnalyticsUiEffect? = null
            backgroundScope.launch { vm.effects.collect { effect = it } }
            runCurrent()
            vm.openPlans()
            runCurrent()
            assertEquals(SellerAnalyticsUiEffect.OpenPlans(ShopId(1)), effect)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun entitlements_useCapabilityNotSlug() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm = createVm(advancedAnalytics = true)
            advanceUntilIdle()
            assertTrue(vm.uiState.value.advancedAnalytics)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun createVm(
        shops: List<SellerShopSummary> = listOf(SellerShopSummary(ShopId(1), "S", true, true)),
        exportRepo: FakeAnalyticsRepo = FakeAnalyticsRepo(),
        fileSaver: FileSaver = RecordingFileSaver(),
        advancedAnalytics: Boolean = false,
    ): SellerAnalyticsViewModel {
        val subscriptions = MutableStateFlow<Map<ShopId, ShopSubscription>>(emptyMap())
        return SellerAnalyticsViewModel(
            sellerShopRepository = FakeShopRepo(shops),
            exportSellerAnalytics = ExportSellerAnalyticsUseCase(exportRepo),
            getShopEntitlements =
                GetShopEntitlementsUseCase(
                    subscriptionRepository = FakeSubscriptionRepo(subscriptions, advancedAnalytics),
                    planRepository = FakePlanRepo(advancedAnalytics),
                ),
            subscriptionRepository = FakeSubscriptionRepo(subscriptions, advancedAnalytics),
            fileSaver = fileSaver,
        )
    }
}

private class RecordingFileSaver(
    private val result: FileSaveResult = FileSaveResult.Saved,
) : FileSaver {
    var lastName: String? = null
    var lastMime: String? = null
    var lastBytes: ByteArray = byteArrayOf()

    override suspend fun saveFile(
        suggestedName: String,
        mimeType: String,
        content: ByteArray,
    ): FileSaveResult {
        lastName = suggestedName
        lastMime = mimeType
        lastBytes = content
        return result
    }
}

private class FakeAnalyticsRepo(
    private val result: AppResult<AnalyticsExport> =
        AppResult.Success(AnalyticsExport(byteArrayOf(1), "text/csv", null)),
    private val delay: Boolean = false,
) : SellerAnalyticsRepository {
    var exportCalls = 0
    private val gate = kotlinx.coroutines.CompletableDeferred<Unit>()

    fun release() {
        gate.complete(Unit)
    }

    override suspend fun exportAnalytics(shopId: ShopId, period: AnalyticsPeriod): AppResult<AnalyticsExport> {
        exportCalls += 1
        if (delay) gate.await()
        return result
    }
}

private class FakeShopRepo(
    private val shops: List<SellerShopSummary>,
) : SellerShopRepository {
    override suspend fun checkSlugAvailability(
        slug: com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug,
        excludeId: ShopId?,
    ) = AppResult.Failure(AppError.Unexpected())

    override suspend fun createShop(command: CreateShopCommand) = AppResult.Failure(AppError.Unexpected())

    override suspend fun getMyShops(query: SellerShopListQuery) =
        AppResult.Success(CursorPage(shops, null, false))

    override suspend fun getMyShop(shopId: ShopId) = AppResult.Failure(AppError.NotFound())

    override suspend fun updateShop(command: UpdateShopCommand) = AppResult.Failure(AppError.Unexpected())

    override suspend fun getFulfillmentOptions(shopId: ShopId) =
        AppResult.Success(emptyList<com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode>())

    override suspend fun regenerateApiKey(shopId: ShopId) = AppResult.Failure(AppError.Unexpected())
}

private class FakeSubscriptionRepo(
    override val subscriptionsByShopId: MutableStateFlow<Map<ShopId, ShopSubscription>>,
    private val advanced: Boolean,
) : SubscriptionRepository {
    override suspend fun getSubscription(shopId: ShopId, forceRefresh: Boolean) =
        AppResult.Success(sampleSubscription(shopId))

    override suspend fun refreshSubscription(shopId: ShopId) = getSubscription(shopId, true)

    override suspend fun startPlanPurchase(shopId: ShopId, planId: PlanId) =
        AppResult.Failure(AppError.Unexpected())
}

private class FakePlanRepo(
    private val advanced: Boolean,
) : PlanRepository {
    override suspend fun getPlans(forceRefresh: Boolean) =
        AppResult.Success(
            listOf(
                com.vitran.shop.feature.seller.plan.domain.model.PlanSummary(
                    id = PlanId(1),
                    slug = PlanSlug.of("free"),
                    title = "Free",
                    priceAmount = 0,
                    durationDays = null,
                    limits = PlanLimits(15, 3, 1),
                    capabilities = PlanCapabilities(advancedAnalytics = advanced),
                    sortOrder = 1,
                    active = true,
                ),
            ),
        )

    override suspend fun getPlan(planId: PlanId, forceRefresh: Boolean) =
        AppResult.Failure(AppError.NotFound())

    override suspend fun refreshPlans() = getPlans(true)
}

private fun sampleSubscription(shopId: ShopId) =
    ShopSubscription(
        shopId = shopId,
        plan =
            SubscriptionPlan(
                id = PlanId(1),
                slug = PlanSlug.of("free"),
                title = "Free",
                priceAmount = null,
                durationDays = null,
                limits = PlanLimits(15, 3, 1),
            ),
        status = SubscriptionStatus.Active,
        startedAt = Instant.parse("2026-06-01T10:00:00Z"),
        expiresAt = null,
        daysRemaining = null,
    )
