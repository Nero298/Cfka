package com.zodiactap.mapper.base.system.bluetooth

import com.zodiactap.mapper.system.bluetooth.BluetoothDeviceInfo
import com.zodiactap.mapper.system.devices.DevicesAdapter
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class ChooseBluetoothDeviceUseCaseImpl @Inject constructor(
    private val devicesAdapter: DevicesAdapter,
    private val permissionAdapter: PermissionAdapter,
) : ChooseBluetoothDeviceUseCase {
    override val devices: StateFlow<List<BluetoothDeviceInfo>> =
        devicesAdapter.pairedBluetoothDevices

    override val hasPermissionToSeeDevices: Flow<Boolean> =
        permissionAdapter.isGrantedFlow(Permission.FIND_NEARBY_DEVICES)

    override fun requestPermission() {
        permissionAdapter.request(Permission.FIND_NEARBY_DEVICES)
    }
}

interface ChooseBluetoothDeviceUseCase {
    val devices: StateFlow<List<BluetoothDeviceInfo>>

    val hasPermissionToSeeDevices: Flow<Boolean>
    fun requestPermission()
}
