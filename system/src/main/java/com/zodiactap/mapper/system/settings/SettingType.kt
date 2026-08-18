package com.zodiactap.mapper.system.settings

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
enum class SettingType {
    SYSTEM,
    SECURE,
    GLOBAL,
}
