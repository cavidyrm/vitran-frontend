package com.vitran.shop.feature.seller.referral.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.referral.data.remote.dto.ApplyReferralCreditRequestDto
import com.vitran.shop.feature.seller.referral.data.remote.dto.ReferralProfileDataDto
import com.vitran.shop.feature.seller.referral.data.remote.dto.ReferralValidationDataDto
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCode
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart

internal class ReferralApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun validateCode(code: ReferralCode): AppResult<ReferralValidationDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/referrals/${code.value.encodeURLPathPart()}")) {
                authMode(AuthMode.None)
            }
        }

    suspend fun getProfile(): AppResult<ReferralProfileDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/me/referral")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun applyCredit(creditId: ReferralCreditId, shopId: ShopId): AppResult<Unit> =
        executor.executeEmpty {
            client.post(environment.apiUrl("/me/referrals/credits/${creditId.value}/apply")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(ApplyReferralCreditRequestDto(shopId = shopId.value))
            }
        }
}
