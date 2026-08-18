package com.zodiactap.mapper.system.devices

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.hardware.input.InputManager
import android.view.InputDevice
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import com.zodiactap.mapper.common.utils.InputDeviceInfo
import com.zodiactap.mapper.common.utils.InputDeviceUtils
import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.common.utils.Success
import com.zodiactap.mapper.common.utils.ifIsData
import com.zodiactap.mapper.system.bluetooth.BluetoothDeviceInfo
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Singleton
class AndroidDevicesAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapter: com.zodiactap.mapper.system.bluetooth.BluetoothAdapter,
    private val permissionAdapter: PermissionAdapter,
    private val coroutineScope: CoroutineScope,
) : DevicesAdapter,
    InputManager.InputDeviceListener {

    private val ctx = context.applicationContext
    private val inputManager = ctx.getSystemService<InputManager>()

    override val onInputDeviceConnect: MutableSharedFlow<InputDeviceInfo> = MutableSharedFlow()
    override val onInputDeviceDisconnect: MutableSharedFlow<InputDeviceInfo> = MutableSharedFlow()
    override val connectedInputDevices =
        MutableStateFlow<State<List<InputDeviceInfo>>>(State.Loading)

    override val pairedBluetoothDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())

    override val connectedBluetoothDevices = MutableStateFlow<Set<BluetoothDeviceInfo>>(emptySet())

    private val bluetoothManager: BluetoothManager? = ctx.getSystemService()

    init {
        coroutineScope.launch {
            updatePairedBluetoothDevices()
            updateInputDevices()
        }

        coroutineScope.launch {
            merge(
                bluetoothAdapter.onDevicePairedChange,
                bluetoothAdapter.isBluetoothEnabled,
                permissionAdapter.isGrantedFlow(Permission.FIND_NEARBY_DEVICES),
            ).collectLatest {
                updatePairedBluetoothDevices()
            }
        }

        inputManager?.registerInputDeviceListener(this, null)

        bluetoothAdapter.onDeviceConnect.onEach { device ->
            val currentValue = connectedBluetoothDevices.value

            connectedBluetoothDevices.value = currentValue.plus(device)
        }.launchIn(coroutineScope)

        bluetoothAdapter.onDeviceDisconnect.onEach { device ->

            val currentValue = connectedBluetoothDevices.value

            connectedBluetoothDevices.value = currentValue.minus(device)
        }.launchIn(coroutineScope)

        bluetoothAdapter.isBluetoothEnabled.onEach { isEnabled ->
            if (!isEnabled) {
                connectedBluetoothDevices.update { emptySet() }
            }
        }.launchIn(coroutineScope)
    }

    override fun deviceHasKey(id: Int, keyCode: Int): Boolean {
        val device = InputDevice.getDevice(id) ?: return false

        return device.hasKeys(keyCode)[0]
    }

    override fun getInputDeviceName(descriptor: String): KMResult<String> {
        for (id in InputDevice.getDeviceIds()) {
            val device = InputDevice.getDevice(id) ?: continue

            if (device.descriptor == descriptor) {
                return Success(device.name)
            }
        }

        return KMError.DeviceNotFound(descriptor)
    }

    override fun getInputDevice(deviceId: Int): InputDeviceInfo? {
        return InputDevice.getDevice(deviceId)?.let { InputDeviceUtils.createInputDeviceInfo(it) }
    }

    override fun onInputDeviceAdded(deviceId: Int) {
        coroutineScope.launch {
            val device = InputDevice.getDevice(deviceId) ?: return@launch
            onInputDeviceConnect.emit(
                InputDeviceUtils.createInputDeviceInfo(device),
            )

            updateInputDevices()
        }
    }

    override fun onInputDeviceRemoved(deviceId: Int) {
        coroutineScope.launch {
            connectedInputDevices.value.ifIsData { connectedInputDevices ->
                val device = connectedInputDevices.find { it.id == deviceId }
                    ?: return@ifIsData

                onInputDeviceDisconnect.emit(device)
            }

            updateInputDevices()
        }
    }

    override fun onInputDeviceChanged(deviceId: Int) {
        updateInputDevices()
    }

    private fun updateInputDevices() {
        val devices = mutableListOf<InputDeviceInfo>()

        for (id in InputDevice.getDeviceIds()) {
            val device = InputDevice.getDevice(id) ?: continue

            devices.add(InputDeviceUtils.createInputDeviceInfo(device))
        }

        connectedInputDevices.value = State.Data(devices)
    }

    private fun updatePairedBluetoothDevices() {
        val adapter = bluetoothManager?.adapter

        if (adapter == null || !permissionAdapter.isGranted(Permission.FIND_NEARBY_DEVICES)) {
            pairedBluetoothDevices.value = emptyList()
            return
        }

        val devices = adapter.bondedDevices?.mapNotNull { device: BluetoothDevice? ->
            if (device == null) {
                return@mapNotNull null
            }

            val address = device.address ?: return@mapNotNull null
            val name = device.name ?: return@mapNotNull null

            BluetoothDeviceInfo(address, name)
        }

        pairedBluetoothDevices.value = devices ?: emptyList()
    }
}
