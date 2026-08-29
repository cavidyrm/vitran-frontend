package com.vitran.shop.core.network.model

import kotlinx.serialization.Serializable

/** Explicit empty `data` payload for successful endpoints returning `{}`. */
@Serializable
data object EmptyDataDto
