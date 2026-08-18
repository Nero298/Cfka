package com.zodiactap.mapper.system.bluetooth

import com.zodiactap.mapper.common.utils.KMResult
import kotlinx.coroutines.flow.Flow

interface BluetoothAdapter {
    val onDeviceConnect: Flow<BluetoothDeviceInfo>
    val onDeviceDisconnect: Flow<BluetoothDeviceInfo>
    val onDevicePairedChange: Flow<BluetoothDeviceInfo>

    val isBluetoothEnabled: Flow<Boolean>

    fun enable(): KMResult<*>
    fun disable(): KMResult<*>
}
