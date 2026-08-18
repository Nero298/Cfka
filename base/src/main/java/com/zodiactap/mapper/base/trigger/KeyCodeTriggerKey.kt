package com.zodiactap.mapper.base.trigger

import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.base.keymaps.ClickType
import com.zodiactap.mapper.base.utils.KeyCodeStrings
import com.zodiactap.mapper.base.utils.ScancodeStrings
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import com.zodiactap.mapper.system.inputevents.KeyEventUtils

sealed interface KeyCodeTriggerKey {
    val keyCode: Int

    /**
     * Scancodes were only saved to KeyEvent trigger keys in version 4.0.0 so this is null
     * to be backwards compatible.
     */
    val scanCode: Int?
    val clickType: ClickType

    /**
     * The user can specify they want to detect with the scancode instead of the key code.
     */
    val detectWithScanCodeUserSetting: Boolean

    /**
     * Whether the event that triggers this key will be consumed and not passed
     * onto subsequent apps. E.g consuming the volume down key event will mean the volume
     * doesn't change.
     */
    val consumeEvent: Boolean

    fun isSameDevice(otherKey: KeyCodeTriggerKey): Boolean
}

fun KeyCodeTriggerKey.detectWithScancode(): Boolean {
    return scanCode != null && (detectWithScanCodeUserSetting || isKeyCodeUnknown())
}

fun KeyCodeTriggerKey.isKeyCodeUnknown(): Boolean {
    return KeyEventUtils.isKeyCodeUnknown(keyCode)
}

fun KeyCodeTriggerKey.isScanCodeDetectionUserConfigurable(): Boolean {
    return scanCode != null && !isKeyCodeUnknown()
}

/**
 * Get the label for the key code or scan code, depending on whether to detect it with a scan code.
 */
fun KeyCodeTriggerKey.getCodeLabel(resourceProvider: ResourceProvider): String {
    if (detectWithScancode() && scanCode != null) {
        val codeLabel = ScancodeStrings.getScancodeLabel(scanCode!!)
            ?: resourceProvider.getString(R.string.trigger_key_unknown_scan_code, scanCode!!)

        return "$codeLabel (${resourceProvider.getString(
            R.string.trigger_key_scan_code_detection_flag,
        )})"
    } else {
        return KeyCodeStrings.keyCodeToString(keyCode)
            ?: resourceProvider.getString(R.string.trigger_key_unknown_key_code, keyCode)
    }
}
