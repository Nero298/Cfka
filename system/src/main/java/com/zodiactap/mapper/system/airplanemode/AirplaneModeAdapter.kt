package com.zodiactap.mapper.system.airplanemode

import com.zodiactap.mapper.common.utils.KMResult

interface AirplaneModeAdapter {
    fun isEnabled(): Boolean
    suspend fun enable(): KMResult<*>
    suspend fun disable(): KMResult<*>
}
