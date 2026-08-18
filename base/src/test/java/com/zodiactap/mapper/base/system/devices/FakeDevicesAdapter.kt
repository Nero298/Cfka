package com.zodiactap.mapper.base.system.devices

import com.zodiactap.mapper.common.utils.InputDeviceInfo
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.system.bluetooth.BluetoothDeviceInfo
import com.zodiactap.mapper.system.devices.DevicesAdapter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeDevicesAdapter : DevicesAdapter {
    override val onInputDeviceConnect = MutableSharedFlow<InputDeviceInfo>()
    override val onInputDeviceDisconnect = MutableSharedFlow<InputDeviceInfo>()

    var deviceHasKey: (id: Int, keyCode: Int) -> Boolean = { _, _ -> false }

    override val connectedInputDevices =
        MutableStateFlow<State<List<InputDeviceInfo>>>(State.Loading)

    override val pairedBluetoothDevices: StateFlow<List<BluetoothDeviceInfo>>
        get() = throw Exception()

    override val connectedBluetoothDevices: StateFlow<Set<BluetoothDeviceInfo>>
        get() = throw Exception()

    override fun deviceHasKey(id: Int, keyCode: Int): Boolean {
        return deviceHasKey.invoke(id, keyCode)
    }

    override fun getInputDeviceName(descriptor: String): KMResult<String> {
        throw Exception()
    }

    override fun getInputDevice(deviceId: Int): InputDeviceInfo? {
        throw NotImplementedError()
    }
}
