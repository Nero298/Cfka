package com.zodiactap.mapper.base.utils

import com.zodiactap.mapper.base.keymaps.ClickType
import com.zodiactap.mapper.base.trigger.KeyEventTriggerDevice
import com.zodiactap.mapper.base.trigger.KeyEventTriggerKey
import com.zodiactap.mapper.base.trigger.Trigger
import com.zodiactap.mapper.base.trigger.TriggerKey
import com.zodiactap.mapper.base.trigger.TriggerMode

fun singleKeyTrigger(key: TriggerKey): Trigger = Trigger(
    keys = listOf(key),
    mode = TriggerMode.Undefined,
)

fun parallelTrigger(vararg keys: TriggerKey): Trigger = Trigger(
    keys = keys.toList(),
    mode = TriggerMode.Parallel(keys[0].clickType),
)

fun sequenceTrigger(vararg keys: TriggerKey): Trigger = Trigger(
    keys = keys.toList(),
    mode = TriggerMode.Sequence,
)

fun triggerKey(
    keyCode: Int,
    device: KeyEventTriggerDevice = KeyEventTriggerDevice.Internal,
    clickType: ClickType = ClickType.SHORT_PRESS,
    consume: Boolean = true,
    requiresIme: Boolean = false,
): KeyEventTriggerKey = KeyEventTriggerKey(
    keyCode = keyCode,
    device = device,
    clickType = clickType,
    consumeEvent = consume,
    requiresIme = requiresIme,
)
