package com.zodiactap.mapper.base.actions

import com.zodiactap.mapper.system.accessibility.AccessibilityServiceEvent
import kotlinx.serialization.Serializable

@Serializable
data class TestActionEvent(val action: ActionData) : AccessibilityServiceEvent()
