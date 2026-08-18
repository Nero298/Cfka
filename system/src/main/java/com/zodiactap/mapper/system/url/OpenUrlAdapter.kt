package com.zodiactap.mapper.system.url

import com.zodiactap.mapper.common.utils.KMResult

interface OpenUrlAdapter {
    fun openUrl(url: String): KMResult<*>
}
