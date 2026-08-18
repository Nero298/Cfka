package com.zodiactap.mapper.base.system.navigation

import android.view.InputDevice
import android.view.KeyEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.zodiactap.mapper.base.input.InjectKeyEventModel
import com.zodiactap.mapper.base.input.InputEventHub
import com.zodiactap.mapper.base.system.accessibility.AccessibilityNodeAction
import com.zodiactap.mapper.base.system.accessibility.IAccessibilityService
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.success
import com.zodiactap.mapper.common.utils.then

class OpenMenuHelper(
    private val accessibilityService: IAccessibilityService,
    private val inputEventHub: InputEventHub,
) {

    companion object {
        private const val OVERFLOW_MENU_CONTENT_DESCRIPTION = "More options"
    }

    fun openMenu(): KMResult<*> {
        when {
            inputEventHub.isSystemBridgeConnected() -> {
                val downEvent = InjectKeyEventModel(
                    keyCode = KeyEvent.KEYCODE_MENU,
                    action = KeyEvent.ACTION_DOWN,
                    metaState = 0,
                    scanCode = 0,
                    deviceId = -1,
                    repeatCount = 0,
                    source = InputDevice.SOURCE_UNKNOWN,
                )

                val upEvent = downEvent.copy(action = KeyEvent.ACTION_UP)

                return inputEventHub.injectKeyEventAsync(downEvent).then {
                    inputEventHub.injectKeyEventAsync(upEvent)
                }
            }

            else -> {
                accessibilityService.performActionOnNode({
                    it.contentDescription ==
                        OVERFLOW_MENU_CONTENT_DESCRIPTION
                }) {
                    AccessibilityNodeAction(
                        AccessibilityNodeInfoCompat.ACTION_CLICK,
                    )
                }

                return success()
            }
        }
    }
}
