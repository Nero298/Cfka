package com.zodiactap.mapper.base.detection

import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.base.constraints.ConstraintState
import com.zodiactap.mapper.base.groups.Group
import com.zodiactap.mapper.base.groups.GroupEntityMapper
import com.zodiactap.mapper.base.input.InjectKeyEventModel
import com.zodiactap.mapper.base.input.InputEventHub
import com.zodiactap.mapper.base.keymaps.KeyMap
import com.zodiactap.mapper.base.keymaps.KeyMapEntityMapper
import com.zodiactap.mapper.base.system.accessibility.IAccessibilityService
import com.zodiactap.mapper.base.system.navigation.OpenMenuHelper
import com.zodiactap.mapper.base.trigger.FingerprintTriggerKey
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.common.utils.dataOrNull
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.PreferenceDefaults
import com.zodiactap.mapper.data.repositories.FloatingButtonRepository
import com.zodiactap.mapper.data.repositories.GroupRepository
import com.zodiactap.mapper.data.repositories.KeyMapRepository
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.system.popup.ToastAdapter
import com.zodiactap.mapper.system.vibrator.VibratorAdapter
import com.zodiactap.mapper.system.volume.VolumeAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class DetectKeyMapsUseCaseImpl @AssistedInject constructor(
    @Assisted
    private val accessibilityService: IAccessibilityService,
    @Assisted
    private val coroutineScope: CoroutineScope,
    private val keyMapRepository: KeyMapRepository,
    private val floatingButtonRepository: FloatingButtonRepository,
    private val groupRepository: GroupRepository,
    private val preferenceRepository: PreferenceRepository,
    private val volumeAdapter: VolumeAdapter,
    private val toastAdapter: ToastAdapter,
    private val resourceProvider: ResourceProvider,
    private val vibrator: VibratorAdapter,
    private val inputEventHub: InputEventHub,
) : DetectKeyMapsUseCase {

    @AssistedFactory
    interface Factory {
        fun create(
            accessibilityService: IAccessibilityService,
            coroutineScope: CoroutineScope,
        ): DetectKeyMapsUseCaseImpl
    }

    companion object {
        fun processKeyMapsAndGroups(
            keyMaps: List<KeyMap>,
            groups: List<Group>,
        ): List<DetectKeyMapModel> = buildList {
            val groupMap = groups.associateBy { it.uid }

            keyMapLoop@ for (keyMap in keyMaps) {
                var depth = 0
                var groupUid: String? = keyMap.groupUid
                val constraintStates = mutableListOf<ConstraintState>()

                while (depth < 100) {
                    if (groupUid == null) {
                        add(
                            DetectKeyMapModel(
                                keyMap = keyMap,
                                groupConstraintStates = constraintStates,
                            ),
                        )
                        break
                    }

                    if (!groupMap.containsKey(groupUid)) {
                        continue@keyMapLoop
                    }

                    val group = groupMap[groupUid]!!
                    groupUid = group.parentUid

                    if (group.constraintState.constraints.isNotEmpty()) {
                        constraintStates.add(group.constraintState)
                    }

                    depth++
                }
            }
        }
    }

    override val allKeyMapList: Flow<List<DetectKeyMapModel>> = combine(
        keyMapRepository.keyMapList,
        floatingButtonRepository.buttonsList,
        groupRepository.groups,
    ) { keyMapListState, buttonListState, groupEntities ->
        if (keyMapListState is State.Loading || buttonListState is State.Loading) {
            return@combine emptyList()
        }

        val keyMapEntityList = keyMapListState.dataOrNull() ?: return@combine emptyList()
        val buttonEntityList = buttonListState.dataOrNull() ?: return@combine emptyList()

        val keyMapList = keyMapEntityList.map { keyMap ->
            KeyMapEntityMapper.fromEntity(keyMap, buttonEntityList)
        }

        val groupList = groupEntities.map { GroupEntityMapper.fromEntity(it) }

        processKeyMapsAndGroups(keyMapList, groupList)
    }.flowOn(Dispatchers.Default)

    override val requestFingerprintGestureDetection: Flow<Boolean> =
        allKeyMapList.map { models ->
            models.any { model ->
                model.keyMap.isEnabled &&
                    model.keyMap.trigger.keys.any { it is FingerprintTriggerKey }
            }
        }

    override val keyMapsToTriggerFromOtherApps: Flow<List<KeyMap>> =
        allKeyMapList.map { keyMapList ->
            keyMapList.filter { it.keyMap.trigger.triggerFromOtherApps }.map { it.keyMap }
        }.flowOn(Dispatchers.Default)

    override val defaultLongPressDelay: Flow<Long> =
        preferenceRepository.get(Keys.defaultLongPressDelay)
            .map { it ?: PreferenceDefaults.LONG_PRESS_DELAY }
            .map { it.toLong() }

    override val defaultDoublePressDelay: Flow<Long> =
        preferenceRepository.get(Keys.defaultDoublePressDelay)
            .map { it ?: PreferenceDefaults.DOUBLE_PRESS_DELAY }
            .map { it.toLong() }

    override val defaultSequenceTriggerTimeout: Flow<Long> =
        preferenceRepository.get(Keys.defaultSequenceTriggerTimeout)
            .map { it ?: PreferenceDefaults.SEQUENCE_TRIGGER_TIMEOUT }
            .map { it.toLong() }

    override val currentTime: Long
        get() = SystemClock.elapsedRealtime()

    private val openMenuHelper = OpenMenuHelper(
        accessibilityService,
        inputEventHub,
    )

    override val forceVibrate: Flow<Boolean> =
        preferenceRepository.get(Keys.forceVibrate).map { it == true }

    override val defaultVibrateDuration: Flow<Long> =
        preferenceRepository.get(Keys.defaultVibrateDuration)
            .map { it ?: PreferenceDefaults.VIBRATION_DURATION }
            .map { it.toLong() }

    override val injectKeyEventsWithSystemBridge: StateFlow<Boolean> =
        preferenceRepository.get(Keys.keyEventActionsUseSystemBridge)
            .map { it ?: PreferenceDefaults.KEY_EVENT_ACTIONS_USE_SYSTEM_BRIDGE }
            .stateIn(coroutineScope, SharingStarted.Eagerly, false)

    override fun showTriggeredToast() {
        toastAdapter.show(resourceProvider.getString(R.string.toast_triggered_keymap))
    }

    override fun vibrate(duration: Long) {
        vibrator.vibrate(duration)
    }

    override fun imitateKeyEvent(
        keyCode: Int,
        metaState: Int,
        deviceId: Int,
        action: Int,
        scanCode: Int,
        source: Int,
    ) {
        val model = InjectKeyEventModel(
            keyCode = keyCode,
            action = action,
            metaState = metaState,
            deviceId = deviceId,
            scanCode = scanCode,
            source = source,
        )

        if (inputEventHub.isSystemBridgeConnected()) {
            Timber.d(
                "Imitate button press ${
                    KeyEvent.keyCodeToString(
                        keyCode,
                    )
                } with system bridge, key code: $keyCode, device id: $deviceId, meta state: $metaState, scan code: $scanCode",
            )
            inputEventHub.injectKeyEventAsync(model)
        } else {
            Timber.d(
                "Imitate button press ${
                    KeyEvent.keyCodeToString(
                        keyCode,
                    )
                }, key code: $keyCode, device id: $deviceId, meta state: $metaState, scan code: $scanCode",
            )

            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> volumeAdapter.raiseVolume(showVolumeUi = true)

                KeyEvent.KEYCODE_VOLUME_DOWN -> volumeAdapter.lowerVolume(showVolumeUi = true)

                KeyEvent.KEYCODE_BACK -> accessibilityService.doGlobalAction(
                    AccessibilityService.GLOBAL_ACTION_BACK,
                )

                KeyEvent.KEYCODE_HOME -> accessibilityService.doGlobalAction(
                    AccessibilityService.GLOBAL_ACTION_HOME,
                )

                KeyEvent.KEYCODE_APP_SWITCH -> accessibilityService.doGlobalAction(
                    AccessibilityService.GLOBAL_ACTION_POWER_DIALOG,
                )

                KeyEvent.KEYCODE_MENU -> openMenuHelper.openMenu()

                else -> inputEventHub.injectKeyEventAsync(model)
            }
        }
    }

    override fun imitateEvdevEvent(deviceId: Int, type: Int, code: Int, value: Int) {
        if (inputEventHub.isSystemBridgeConnected()) {
            Timber.d(
                "Imitate evdev event, device id: $deviceId, type: $type, code: $code, value: $value",
            )
            inputEventHub.injectEvdevEvent(deviceId, type, code, value)
        } else {
            Timber.w(
                "Cannot imitate evdev event without system bridge connected.",
            )
        }
    }
}

interface DetectKeyMapsUseCase {
    val allKeyMapList: Flow<List<DetectKeyMapModel>>
    val requestFingerprintGestureDetection: Flow<Boolean>
    val keyMapsToTriggerFromOtherApps: Flow<List<KeyMap>>

    val defaultLongPressDelay: Flow<Long>
    val defaultDoublePressDelay: Flow<Long>
    val defaultSequenceTriggerTimeout: Flow<Long>

    val forceVibrate: Flow<Boolean>
    val defaultVibrateDuration: Flow<Long>

    fun showTriggeredToast()
    fun vibrate(duration: Long)

    val currentTime: Long

    fun imitateKeyEvent(
        keyCode: Int,
        metaState: Int = 0,
        deviceId: Int = 0,
        action: Int,
        scanCode: Int = 0,
        source: Int = InputDevice.SOURCE_UNKNOWN,
    )

    fun imitateEvdevEvent(deviceId: Int, type: Int, code: Int, value: Int)

    val injectKeyEventsWithSystemBridge: StateFlow<Boolean>
}
