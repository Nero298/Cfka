package com.zodiactap.mapper.base.trigger

import dagger.hilt.android.scopes.ViewModelScoped
import com.zodiactap.mapper.base.floating.FloatingButtonEntityMapper
import com.zodiactap.mapper.base.keymaps.ClickType
import com.zodiactap.mapper.base.keymaps.ConfigKeyMapState
import com.zodiactap.mapper.base.keymaps.GetDefaultKeyMapOptionsUseCase
import com.zodiactap.mapper.base.keymaps.KeyMap
import com.zodiactap.mapper.base.system.accessibility.FingerprintGestureType
import com.zodiactap.mapper.common.models.EvdevDeviceInfo
import com.zodiactap.mapper.common.utils.InputDeviceUtils
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.common.utils.dataOrNull
import com.zodiactap.mapper.common.utils.firstBlocking
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.entities.AssistantTriggerKeyEntity
import com.zodiactap.mapper.data.entities.EvdevTriggerKeyEntity
import com.zodiactap.mapper.data.entities.FingerprintTriggerKeyEntity
import com.zodiactap.mapper.data.entities.FloatingButtonKeyEntity
import com.zodiactap.mapper.data.entities.KeyEventTriggerKeyEntity
import com.zodiactap.mapper.data.entities.KeyMapEntity
import com.zodiactap.mapper.data.repositories.FloatingButtonRepository
import com.zodiactap.mapper.data.repositories.FloatingLayoutRepository
import com.zodiactap.mapper.data.repositories.KeyMapRepository
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.system.devices.DevicesAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@ViewModelScoped
class ConfigTriggerUseCaseImpl @Inject constructor(
    private val state: ConfigKeyMapState,
    private val preferenceRepository: PreferenceRepository,
    private val floatingButtonRepository: FloatingButtonRepository,
    private val devicesAdapter: DevicesAdapter,
    private val floatingLayoutRepository: FloatingLayoutRepository,
    private val getDefaultKeyMapOptionsUseCase: GetDefaultKeyMapOptionsUseCase,
    private val keyMapRepository: KeyMapRepository,
) : ConfigTriggerUseCase,
    GetDefaultKeyMapOptionsUseCase by getDefaultKeyMapOptionsUseCase {
    override val keyMap: StateFlow<State<KeyMap>> = state.keyMap

    override val floatingButtonToUse: MutableStateFlow<String?> = state.floatingButtonToUse

    private val showDeviceDescriptors: Flow<Boolean> =
        preferenceRepository.get(Keys.showDeviceDescriptors).map { it == true }

    private val delegate: ConfigTriggerDelegate = ConfigTriggerDelegate()

    // This class is viewmodel scoped so this will be recomputed each time
    // the user starts configuring a key map
    private val otherTriggerKeys: List<KeyCodeTriggerKey> by lazy {
        keyMapRepository.keyMapList
            .filterIsInstance<State.Data<List<KeyMapEntity>>>()
            .map { state -> state.data.flatMap { it.trigger.keys } }
            .map { keys ->
                keys
                    .mapNotNull { key ->
                        when (key) {
                            is EvdevTriggerKeyEntity -> EvdevTriggerKey.fromEntity(key)

                            is KeyEventTriggerKeyEntity -> KeyEventTriggerKey.fromEntity(key)

                            is AssistantTriggerKeyEntity,
                            is FingerprintTriggerKeyEntity,
                            is FloatingButtonKeyEntity,
                                -> null
                        }
                    }.filterIsInstance<KeyCodeTriggerKey>()
            }.firstBlocking()
    }

    override fun setEnabled(enabled: Boolean) {
        state.update { it.copy(isEnabled = enabled) }
    }

    override suspend fun getFloatingLayoutCount(): Int {
        return floatingLayoutRepository.count()
    }

    override suspend fun addFloatingButtonTriggerKey(buttonUid: String) {
        floatingButtonToUse.update { null }

        val button = floatingButtonRepository.get(buttonUid)
            ?.let { entity ->
                FloatingButtonEntityMapper.fromEntity(
                    entity.button,
                    entity.layout.name,
                )
            }

        updateTrigger { trigger ->
            delegate.addFloatingButtonTriggerKey(trigger, buttonUid, button)
        }
    }

    override fun addAssistantTriggerKey(type: AssistantTriggerType) = updateTrigger { trigger ->
        delegate.addAssistantTriggerKey(trigger, type)
    }

    override fun addFingerprintGesture(type: FingerprintGestureType) = updateTrigger { trigger ->
        delegate.addFingerprintGesture(trigger, type)
    }

    override suspend fun addKeyEventTriggerKey(
        keyCode: Int,
        scanCode: Int,
        device: KeyEventTriggerDevice,
        requiresIme: Boolean,
    ) = updateTrigger { trigger ->
        delegate.addKeyEventTriggerKey(
            trigger,
            keyCode,
            scanCode,
            device,
            requiresIme,
            otherTriggerKeys = otherTriggerKeys,
            doNotRemap = defaultDoNotRemap.value,
        )
    }

    override suspend fun addEvdevTriggerKey(keyCode: Int, scanCode: Int, device: EvdevDeviceInfo) =
        updateTrigger { trigger ->
            delegate.addEvdevTriggerKey(
                trigger,
                keyCode,
                scanCode,
                device,
                otherTriggerKeys = otherTriggerKeys,
                doNotRemap = defaultDoNotRemap.value,
            )
        }

    override fun removeTriggerKey(uid: String) = updateTrigger { trigger ->
        delegate.removeTriggerKey(trigger, uid)
    }

    override fun moveTriggerKey(fromIndex: Int, toIndex: Int) = updateTrigger { trigger ->
        delegate.moveTriggerKey(trigger, fromIndex, toIndex)
    }

    override fun getTriggerKey(uid: String): TriggerKey? {
        return state.keyMap.value.dataOrNull()?.trigger?.keys?.find { it.uid == uid }
    }

    override fun setParallelTriggerMode() = updateTrigger { trigger ->
        delegate.setParallelTriggerMode(trigger)
    }

    override fun setSequenceTriggerMode() = updateTrigger { trigger ->
        delegate.setSequenceTriggerMode(trigger)
    }

    override fun setUndefinedTriggerMode() = updateTrigger { trigger ->
        delegate.setUndefinedTriggerMode(trigger)
    }

    override fun setTriggerShortPress() {
        updateTrigger { trigger ->
            delegate.setTriggerShortPress(trigger)
        }
    }

    override fun setTriggerLongPress() {
        updateTrigger { trigger ->
            delegate.setTriggerLongPress(trigger)
        }
    }

    override fun setTriggerDoublePress() {
        updateTrigger { trigger ->
            delegate.setTriggerDoublePress(trigger)
        }
    }

    override fun setTriggerKeyClickType(keyUid: String, clickType: ClickType) {
        updateTrigger { trigger ->
            delegate.setTriggerKeyClickType(trigger, keyUid, clickType)
        }
    }

    override fun setTriggerKeyDevice(keyUid: String, device: KeyEventTriggerDevice) {
        updateTrigger { trigger ->
            delegate.setTriggerKeyDevice(trigger, keyUid, device)
        }
    }

    override fun setTriggerKeyConsumeKeyEvent(keyUid: String, consumeKeyEvent: Boolean) {
        updateTrigger { trigger ->
            delegate.setTriggerKeyConsumeKeyEvent(trigger, keyUid, consumeKeyEvent)
        }
    }

    override fun setAssistantTriggerKeyType(keyUid: String, type: AssistantTriggerType) {
        updateTrigger { trigger ->
            delegate.setAssistantTriggerKeyType(trigger, keyUid, type)
        }
    }

    override fun setFingerprintGestureType(keyUid: String, type: FingerprintGestureType) {
        updateTrigger { trigger ->
            delegate.setFingerprintGestureType(trigger, keyUid, type)
        }
    }

    override fun setVibrateEnabled(enabled: Boolean) = updateTrigger { trigger ->
        delegate.setVibrateEnabled(trigger, enabled)
    }

    override fun setVibrationDuration(duration: Int) = updateTrigger { trigger ->
        delegate.setVibrationDuration(trigger, duration, defaultVibrateDuration.value)
    }

    override fun setLongPressDelay(delay: Int) = updateTrigger { trigger ->
        delegate.setLongPressDelay(trigger, delay, defaultLongPressDelay.value)
    }

    override fun setDoublePressDelay(delay: Int) {
        updateTrigger { trigger ->
            delegate.setDoublePressDelay(trigger, delay, defaultDoublePressDelay.value)
        }
    }

    override fun setSequenceTriggerTimeout(delay: Int) {
        updateTrigger { trigger ->
            delegate.setSequenceTriggerTimeout(trigger, delay, defaultSequenceTriggerTimeout.value)
        }
    }

    override fun setLongPressDoubleVibrationEnabled(enabled: Boolean) {
        updateTrigger { trigger ->
            delegate.setLongPressDoubleVibrationEnabled(trigger, enabled)
        }
    }

    override fun setTriggerFromOtherAppsEnabled(enabled: Boolean) {
        updateTrigger { trigger ->
            delegate.setTriggerFromOtherAppsEnabled(trigger, enabled)
        }
    }

    override fun setShowToastEnabled(enabled: Boolean) {
        updateTrigger { trigger ->
            delegate.setShowToastEnabled(trigger, enabled)
        }
    }

    override fun setScanCodeDetectionEnabled(keyUid: String, enabled: Boolean) {
        updateTrigger { trigger ->
            delegate.setScanCodeDetectionEnabled(trigger, keyUid, enabled)
        }
    }

    override fun getAvailableTriggerKeyDevices(): List<KeyEventTriggerDevice> {
        val externalKeyEventTriggerDevices = sequence {
            val inputDevices =
                devicesAdapter.connectedInputDevices.value.dataOrNull() ?: emptyList()

            val showDeviceDescriptors = showDeviceDescriptors.firstBlocking()

            for (device in inputDevices) {
                if (device.isExternal) {
                    val name = if (showDeviceDescriptors) {
                        InputDeviceUtils.appendDeviceDescriptorToName(
                            device.descriptor,
                            device.name,
                        )
                    } else {
                        device.name
                    }

                    yield(KeyEventTriggerDevice.External(device.descriptor, name))
                }
            }
        }

        return sequence {
            yield(KeyEventTriggerDevice.Internal)
            yield(KeyEventTriggerDevice.Any)
            yieldAll(externalKeyEventTriggerDevices)
        }.toList()
    }

    private fun updateTrigger(block: (trigger: Trigger) -> Trigger) {
        state.update { keyMap ->
            val newTrigger = block(keyMap.trigger)

            keyMap.copy(trigger = newTrigger)
        }
    }
}

interface ConfigTriggerUseCase : GetDefaultKeyMapOptionsUseCase {

    val keyMap: StateFlow<State<KeyMap>>

    fun setEnabled(enabled: Boolean)

    // trigger
    suspend fun addKeyEventTriggerKey(
        keyCode: Int,
        scanCode: Int,
        device: KeyEventTriggerDevice,
        requiresIme: Boolean,
    )

    suspend fun addFloatingButtonTriggerKey(buttonUid: String)
    fun addAssistantTriggerKey(type: AssistantTriggerType)
    fun addFingerprintGesture(type: FingerprintGestureType)
    suspend fun addEvdevTriggerKey(keyCode: Int, scanCode: Int, device: EvdevDeviceInfo)

    fun removeTriggerKey(uid: String)
    fun getTriggerKey(uid: String): TriggerKey?
    fun moveTriggerKey(fromIndex: Int, toIndex: Int)

    fun setParallelTriggerMode()
    fun setSequenceTriggerMode()
    fun setUndefinedTriggerMode()

    fun setTriggerShortPress()
    fun setTriggerLongPress()
    fun setTriggerDoublePress()

    fun setTriggerKeyClickType(keyUid: String, clickType: ClickType)
    fun setTriggerKeyDevice(keyUid: String, device: KeyEventTriggerDevice)
    fun setTriggerKeyConsumeKeyEvent(keyUid: String, consumeKeyEvent: Boolean)
    fun setAssistantTriggerKeyType(keyUid: String, type: AssistantTriggerType)
    fun setFingerprintGestureType(keyUid: String, type: FingerprintGestureType)

    fun setVibrateEnabled(enabled: Boolean)
    fun setVibrationDuration(duration: Int)
    fun setLongPressDelay(delay: Int)
    fun setDoublePressDelay(delay: Int)
    fun setSequenceTriggerTimeout(delay: Int)
    fun setLongPressDoubleVibrationEnabled(enabled: Boolean)
    fun setTriggerFromOtherAppsEnabled(enabled: Boolean)
    fun setShowToastEnabled(enabled: Boolean)
    fun setScanCodeDetectionEnabled(keyUid: String, enabled: Boolean)

    fun getAvailableTriggerKeyDevices(): List<KeyEventTriggerDevice>

    val floatingButtonToUse: MutableStateFlow<String?>
    suspend fun getFloatingLayoutCount(): Int
}
