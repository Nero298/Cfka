package com.zodiactap.mapper.base.trigger

import com.zodiactap.mapper.base.input.InputEventDetectionSource
import com.zodiactap.mapper.common.models.EvdevDeviceInfo

sealed class RecordedKey {
    data class KeyEvent(
        val keyCode: Int,
        val scanCode: Int,
        val deviceDescriptor: String,
        val deviceName: String,
        val isExternalDevice: Boolean,
        val detectionSource: InputEventDetectionSource,
    ) : RecordedKey()

    data class EvdevEvent(val keyCode: Int, val scanCode: Int, val device: EvdevDeviceInfo) :
        RecordedKey()
}
