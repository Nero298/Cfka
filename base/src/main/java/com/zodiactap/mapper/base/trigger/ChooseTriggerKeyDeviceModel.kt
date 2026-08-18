package com.zodiactap.mapper.base.trigger

data class ChooseTriggerKeyDeviceModel(
    val triggerKeyUid: String,
    val devices: List<KeyEventTriggerDevice>,
)
