package com.vitran.shop.feature.seller.shop.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollection
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.model.ShopCollectionDescriptionMaxLength
import com.vitran.shop.feature.seller.shop.domain.model.ShopCollectionProductIdMaxCount
import com.vitran.shop.feature.seller.shop.domain.model.ShopCollectionTitleMaxLength
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository

class CreateShopCollectionUseCase(
    private val sellerShopRepository: SellerShopRepository,
) {
    suspend operator fun invoke(command: CreateShopCollectionCommand): AppResult<ShopCollection> {
        val title = command.title.trim()
        validateTitle(title)?.let { return AppResult.Failure(it) }
        val description = command.description?.trim()?.takeIf { it.isNotEmpty() }
        validateDescription(description)?.let { return AppResult.Failure(it) }
        validateProductIds(command.productIds.size)?.let { return AppResult.Failure(it) }
        return sellerShopRepository.createCollection(
            command.copy(title = title, description = description),
        )
    }
}

class UpdateShopCollectionUseCase(
    private val sellerShopRepository: SellerShopRepository,
) {
    suspend operator fun invoke(command: UpdateShopCollectionCommand): AppResult<ShopCollection> {
        val title = command.title?.trim()
        if (title != null) {
            validateTitle(title)?.let { return AppResult.Failure(it) }
        }
        val description = command.description?.trim()?.takeIf { it.isNotEmpty() }
        validateDescription(description)?.let { return AppResult.Failure(it) }
        command.productIds?.let { ids ->
            validateProductIds(ids.size)?.let { return AppResult.Failure(it) }
        }
        return sellerShopRepository.updateCollection(
            command.copy(title = title, description = description),
        )
    }
}

class DeleteShopCollectionUseCase(
    private val sellerShopRepository: SellerShopRepository,
) {
    suspend operator fun invoke(shopId: ShopId, collectionId: ShopCollectionId): AppResult<Unit> =
        sellerShopRepository.deleteCollection(shopId, collectionId)
}

private fun validateTitle(title: String): AppError.Validation? =
    when {
        title.isEmpty() -> AppError.Validation(message = "عنوان کالکشن الزامی است")
        title.length > ShopCollectionTitleMaxLength ->
            AppError.Validation(message = "عنوان کالکشن حداکثر $ShopCollectionTitleMaxLength نویسه است")
        else -> null
    }

private fun validateDescription(description: String?): AppError.Validation? =
    if (description != null && description.length > ShopCollectionDescriptionMaxLength) {
        AppError.Validation(message = "توضیح کالکشن حداکثر $ShopCollectionDescriptionMaxLength نویسه است")
    } else {
        null
    }

private fun validateProductIds(count: Int): AppError.Validation? =
    if (count > ShopCollectionProductIdMaxCount) {
        AppError.Validation(message = "هر کالکشن حداکثر $ShopCollectionProductIdMaxCount محصول دارد")
    } else {
        null
    }
