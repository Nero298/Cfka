package com.zodiactap.mapper.base.system.intents

import com.zodiactap.mapper.system.intents.IntentExtraModel
import com.zodiactap.mapper.system.intents.IntentTarget
import kotlinx.serialization.Serializable

@Serializable
data class ConfigIntentResult(
    val uri: String,
    val target: IntentTarget,
    val description: String,
    val extras: List<IntentExtraModel>,
)
