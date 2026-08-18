package com.zodiactap.mapper.base.keymaps

import com.zodiactap.mapper.system.accessibility.AccessibilityServiceEvent
import kotlinx.serialization.Serializable

@Serializable
data class TriggerKeyMapEvent(val uid: String) : AccessibilityServiceEvent()
