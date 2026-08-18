package com.zodiactap.mapper.base.system.accessibility

import com.zodiactap.mapper.system.accessibility.AccessibilityServiceEvent
import kotlinx.serialization.Serializable

sealed class RecordAccessibilityNodeEvent : AccessibilityServiceEvent() {
    @Serializable
    data object StartRecordingNodes : RecordAccessibilityNodeEvent()

    @Serializable
    data object StopRecordingNodes : RecordAccessibilityNodeEvent()

    @Serializable
    data class OnRecordNodeStateChanged(val state: RecordAccessibilityNodeState) :
        RecordAccessibilityNodeEvent()
}
