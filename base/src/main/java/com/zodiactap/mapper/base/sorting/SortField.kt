package com.zodiactap.mapper.base.sorting

import kotlinx.serialization.Serializable

@Serializable
enum class SortField {
    TRIGGER,
    ACTIONS,
    CONSTRAINTS,
    OPTIONS,
}
