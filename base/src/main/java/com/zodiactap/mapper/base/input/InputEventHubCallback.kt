package com.zodiactap.mapper.base.input

import com.zodiactap.mapper.system.inputevents.KMInputEvent

interface InputEventHubCallback {
    /**
     * @return whether to consume the event.
     */
    fun onInputEvent(event: KMInputEvent, detectionSource: InputEventDetectionSource): Boolean
}
