package com.zodiactap.mapper.base.actions.keyevent

import com.zodiactap.mapper.common.utils.InputDeviceInfo
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.system.devices.DevicesAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConfigKeyEventUseCaseImpl @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val devicesAdapter: DevicesAdapter,
) : ConfigKeyEventUseCase {
    override val inputDevices: Flow<List<InputDeviceInfo>> =
        devicesAdapter.connectedInputDevices.map { state ->
            if (state !is State.Data) {
                emptyList()
            } else {
                state.data
            }
        }

    override val showDeviceDescriptors: Flow<Boolean> =
        preferenceRepository.get(Keys.showDeviceDescriptors).map { it ?: false }
}

interface ConfigKeyEventUseCase {
    val inputDevices: Flow<List<InputDeviceInfo>>
    val showDeviceDescriptors: Flow<Boolean>
}
