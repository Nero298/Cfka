package com.zodiactap.mapper.system.volume

import kotlinx.serialization.Serializable

@Serializable
enum class RingerMode {
    NORMAL,
    VIBRATE,
    SILENT,
}
