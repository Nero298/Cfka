package com.zodiactap.mapper.system.inputevents

import android.view.MotionEvent
import com.zodiactap.mapper.common.utils.InputDeviceInfo
import com.zodiactap.mapper.common.utils.InputDeviceUtils

/**
 * This is our own abstraction over MotionEvent so that it is easier to write tests and read
 * values without relying on the Android SDK.
 */
data class KMGamePadEvent(
    val eventTime: Long,
    val metaState: Int,
    val device: InputDeviceInfo,
    val axisHatX: Float,
    val axisHatY: Float,
) : KMInputEvent {

    companion object {
        fun fromMotionEvent(event: MotionEvent): KMGamePadEvent? {
            val device = event.device ?: return null

            return KMGamePadEvent(
                eventTime = event.eventTime,
                metaState = event.metaState,
                device = InputDeviceUtils.createInputDeviceInfo(device),
                axisHatX = event.getAxisValue(MotionEvent.AXIS_HAT_X),
                axisHatY = event.getAxisValue(MotionEvent.AXIS_HAT_Y),
            )
        }
    }

    val deviceId: Int = device.id
}
