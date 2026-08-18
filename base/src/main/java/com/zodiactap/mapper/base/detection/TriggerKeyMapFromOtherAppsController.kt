package com.zodiactap.mapper.base.detection

import com.zodiactap.mapper.base.actions.PerformActionsUseCase
import com.zodiactap.mapper.base.constraints.DetectConstraintsUseCase
import com.zodiactap.mapper.base.keymaps.KeyMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class TriggerKeyMapFromOtherAppsController(
    coroutineScope: CoroutineScope,
    detectKeyMapsUseCase: DetectKeyMapsUseCase,
    performActionsUseCase: PerformActionsUseCase,
    detectConstraintsUseCase: DetectConstraintsUseCase,
) : SimpleMappingController(
    coroutineScope,
    detectKeyMapsUseCase,
    performActionsUseCase,
    detectConstraintsUseCase,
) {
    private var keyMapList = emptyList<KeyMap>()

    init {
        coroutineScope.launch {
            detectKeyMapsUseCase.keyMapsToTriggerFromOtherApps.collectLatest { keyMaps ->
                reset()
                this@TriggerKeyMapFromOtherAppsController.keyMapList = keyMaps
            }
        }
    }

    fun onDetected(uid: String) {
        val keyMap = keyMapList.find { it.uid == uid }
        if (keyMap != null) {
            onDetected(keyMap)

            Timber.d("Triggered key map successfully from Intent, $keyMap")
        } else {
            Timber.d(
                "Failed to trigger key map from intent because key map doesn't exist, uid = $uid",
            )
        }
    }
}
