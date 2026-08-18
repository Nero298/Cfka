package com.zodiactap.mapper.base.system.apps

import kotlinx.serialization.Serializable

@Serializable
data class ChooseAppShortcutResult(
    val packageName: String?,
    val shortcutName: String,
    val uri: String,
)
