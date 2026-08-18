package com.zodiactap.mapper.base.system.bluetooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.base.utils.ui.DefaultSimpleListItem
import com.zodiactap.mapper.base.utils.ui.DialogProvider
import com.zodiactap.mapper.base.utils.ui.ListItem
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import com.zodiactap.mapper.base.utils.ui.TextListItem
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.system.bluetooth.BluetoothDeviceInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltViewModel
class ChooseBluetoothDeviceViewModel @Inject constructor(
    private val useCase: ChooseBluetoothDeviceUseCase,
    private val resourceProvider: ResourceProvider,
    dialogProvider: DialogProvider,
) : ViewModel(),
    ResourceProvider by resourceProvider,
    DialogProvider by dialogProvider {

    private val _caption = MutableStateFlow<String?>(null)
    val caption: StateFlow<String?> = _caption

    private val _listItems: MutableStateFlow<State<List<ListItem>>> =
        MutableStateFlow(State.Loading)
    val listItems: StateFlow<State<List<ListItem>>> = _listItems.asStateFlow()

    private val _returnResult = MutableSharedFlow<BluetoothDeviceInfo>()
    val returnResult = _returnResult.asSharedFlow()

    private val missingPermissionListItem: TextListItem.Error by lazy {
        TextListItem.Error(
            "missing_permission",
            getString(R.string.error_choose_bluetooth_devices_permission_denied),
        )
    }

    init {
        combine(useCase.devices, useCase.hasPermissionToSeeDevices) { devices, permissionGranted ->
            if (!permissionGranted) {
                _caption.value = null
                _listItems.value = State.Data(listOf(missingPermissionListItem))
            } else {
                val devicesListItems = devices.map { device ->
                    DefaultSimpleListItem(
                        id = device.address,
                        title = device.name,
                    )
                }

                _caption.value = if (devices.isEmpty()) {
                    getString(R.string.caption_no_paired_bt_devices)
                } else {
                    null
                }

                _listItems.value = State.Data(devicesListItems)
            }
        }.launchIn(viewModelScope)
    }

    fun onFixMissingPermissionListItemClick() {
        useCase.requestPermission()
    }

    fun onBluetoothDeviceListItemClick(id: String) {
        viewModelScope.launch {
            val deviceInfo = useCase.devices.value.find { it.address == id } ?: return@launch
            _returnResult.emit(deviceInfo)
        }
    }
}
