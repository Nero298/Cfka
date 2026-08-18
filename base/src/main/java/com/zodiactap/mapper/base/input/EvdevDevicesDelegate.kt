package com.zodiactap.mapper.base.input

import com.zodiactap.mapper.common.models.EvdevDeviceInfo
import com.zodiactap.mapper.common.models.GrabTargetKeyCode
import com.zodiactap.mapper.common.models.GrabbedDeviceHandle
import com.zodiactap.mapper.common.utils.onFailure
import com.zodiactap.mapper.common.utils.valueIfFailure
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManager
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Need to use a cache that maps a device id to the other device information. This information
 * could be sent in the onEvdevEvent callback instead, but sending non-primitive strings for the
 * device name introduces extra overhead across Binder and JNI.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class EvdevDevicesDelegate @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val systemBridgeConnectionManager: SystemBridgeConnectionManager,
) {
    private val grabbedDevicesById: MutableStateFlow<Map<Int, EvdevDeviceInfo>> =
        MutableStateFlow(emptyMap())

    // Use a channel so there are no race conditions when grabbing and that all
    // grab operations finish in the correct order to completion.
    private val grabDevicesChannel: Channel<List<GrabTargetKeyCode>> = Channel(capacity = 16)

    // All the evdev devices on the device, regardless of whether they are grabbed.
    val allDevices: MutableStateFlow<List<EvdevDeviceInfo>> = MutableStateFlow(emptyList())

    init {
        coroutineScope.launch {
            systemBridgeConnectionManager.connectionState.collect { connectionState ->
                when (connectionState) {
                    is SystemBridgeConnectionState.Connected -> {
                        allDevices.value = fetchAllDevices()
                    }

                    is SystemBridgeConnectionState.Disconnected -> {
                        allDevices.value = emptyList()
                        grabbedDevicesById.value = emptyMap()
                    }
                }
            }
        }

        coroutineScope.launch {
            grabDevicesChannel.receiveAsFlow().collect { devices ->
                withContext(Dispatchers.IO) {
                    invalidateGrabbedDevices(devices)
                }
            }
        }
    }

    private fun invalidateGrabbedDevices(devices: List<GrabTargetKeyCode>) {
        systemBridgeConnectionManager
            .run { bridge -> bridge.setGrabTargets(devices.toTypedArray()) }
            // The callback will respond with the new grabbed devices.
            .onFailure { error ->
                Timber.w(
                    "Grabbing devices failed in system bridge: $error",
                )
            }
    }

    fun setGrabTargets(devices: List<GrabTargetKeyCode>) {
        grabDevicesChannel.trySend(devices)
    }

    fun getGrabbedDeviceInfo(id: Int): EvdevDeviceInfo? {
        return grabbedDevicesById.value[id]
    }

    fun getGrabbedDevices(): List<EvdevDeviceInfo> {
        return grabbedDevicesById.value.values.toList()
    }

    fun onGrabbedDevicesChanged(devices: List<GrabbedDeviceHandle>) {
        Timber.d("Grabbed devices changed: [${devices.joinToString { it.name }}]")

        grabbedDevicesById.value =
            devices.associate { handle ->
                handle.id to
                    EvdevDeviceInfo(handle.name, handle.bus, handle.vendor, handle.product)
            }
    }

    fun onEvdevDevicesChanged(devices: List<EvdevDeviceInfo>) {
        Timber.d("Evdev devices changed: [${devices.joinToString { it.name }}]")

        allDevices.value = devices
    }

    private suspend fun fetchAllDevices(): List<EvdevDeviceInfo> {
        // Do it on a separate thread in case there is deadlock
        return withContext(Dispatchers.IO) {
            systemBridgeConnectionManager.run { bridge ->
                bridge.evdevInputDevices?.filterNotNull() ?: emptyList()
            }
        }.onFailure { error ->
            Timber.e("Failed to get evdev input devices from system bridge: $error")
        }.valueIfFailure { emptyList() }
    }
}
