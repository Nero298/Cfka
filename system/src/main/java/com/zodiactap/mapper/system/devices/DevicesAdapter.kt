package com.zodiactap.mapper.system.devices

import com.zodiactap.mapper.common.utils.InputDeviceInfo
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.system.bluetooth.BluetoothDeviceInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DevicesAdapter {
    val onInputDeviceConnect: Flow<InputDeviceInfo>
    val onInputDeviceDisconnect: Flow<InputDeviceInfo>
    val connectedInputDevices: StateFlow<State<List<InputDeviceInfo>>>

    val pairedBluetoothDevices: StateFlow<List<BluetoothDeviceInfo>>
    val connectedBluetoothDevices: StateFlow<Set<BluetoothDeviceInfo>>

    fun deviceHasKey(id: Int, keyCode: Int): Boolean
    fun getInputDeviceName(descriptor: String): KMResult<String>
    fun getInputDevice(deviceId: Int): InputDeviceInfo?
}
