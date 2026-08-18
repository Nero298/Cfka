package com.zodiactap.mapper.base.sorting

import kotlinx.serialization.Serializable

@Serializable
data class SortFieldOrder(val field: SortField, val order: SortOrder = SortOrder.NONE)
