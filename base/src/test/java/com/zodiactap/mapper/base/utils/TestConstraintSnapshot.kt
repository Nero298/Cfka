package com.zodiactap.mapper.base.utils

import com.zodiactap.mapper.base.constraints.Constraint
import com.zodiactap.mapper.base.constraints.ConstraintData
import com.zodiactap.mapper.base.constraints.ConstraintSnapshot
import com.zodiactap.mapper.common.utils.Orientation
import com.zodiactap.mapper.common.utils.PhysicalOrientation
import com.zodiactap.mapper.common.utils.SizeKM
import com.zodiactap.mapper.system.bluetooth.BluetoothDeviceInfo
import com.zodiactap.mapper.system.camera.CameraLens
import com.zodiactap.mapper.system.foldable.HingeState
import com.zodiactap.mapper.system.foldable.isClosed
import com.zodiactap.mapper.system.foldable.isOpen
import com.zodiactap.mapper.system.phone.CallState
import com.zodiactap.mapper.system.volume.RingerMode
import java.time.LocalTime
import timber.log.Timber

class TestConstraintSnapshot(
    val appInForeground: String? = null,
    val connectedBluetoothDevices: Set<BluetoothDeviceInfo> = emptySet(),
    val orientation: Orientation = Orientation.ORIENTATION_0,
    val physicalOrientation: PhysicalOrientation = PhysicalOrientation.PORTRAIT,
    val isScreenOn: Boolean = false,
    val appsPlayingMedia: List<String> = emptyList(),
    val isWifiEnabled: Boolean = false,
    val connectedWifiSSID: String? = null,
    val chosenImeId: String? = null,
    val isKeyboardShowing: Boolean = false,
    val callState: CallState = CallState.NONE,
    val ringerMode: RingerMode = RingerMode.NORMAL,
    val isCharging: Boolean = false,
    val isLocked: Boolean = false,
    val isBackFlashlightOn: Boolean = false,
    val isFrontFlashlightOn: Boolean = false,
    val isLockscreenShowing: Boolean = false,
    val localTime: LocalTime = LocalTime.now(),
    val hingeState: HingeState = HingeState.Unavailable,
    val isNotificationPanelShowing: Boolean = false,
    val displaySize: SizeKM = SizeKM(1080, 1920),
) : ConstraintSnapshot {

    override fun isSatisfied(constraint: Constraint): Boolean {
        val isSatisfied = when (val data = constraint.data) {
            is ConstraintData.AppInForeground -> appInForeground == data.packageName
            is ConstraintData.AppNotInForeground -> appInForeground != data.packageName
            is ConstraintData.AppPlayingMedia ->
                appsPlayingMedia.contains(data.packageName)

            is ConstraintData.AppNotPlayingMedia ->
                appsPlayingMedia.none { it == data.packageName }

            is ConstraintData.MediaPlaying -> appsPlayingMedia.isNotEmpty()
            is ConstraintData.NoMediaPlaying -> appsPlayingMedia.isEmpty()
            is ConstraintData.BtDeviceConnected -> {
                connectedBluetoothDevices.any { it.address == data.bluetoothAddress }
            }

            is ConstraintData.BtDeviceDisconnected -> {
                connectedBluetoothDevices.none { it.address == data.bluetoothAddress }
            }

            is ConstraintData.OrientationCustom -> orientation == data.orientation
            is ConstraintData.OrientationLandscape ->
                orientation == Orientation.ORIENTATION_90 ||
                    orientation == Orientation.ORIENTATION_270

            is ConstraintData.OrientationPortrait ->
                orientation == Orientation.ORIENTATION_0 ||
                    orientation == Orientation.ORIENTATION_180

            is ConstraintData.PhysicalOrientation ->
                physicalOrientation == data.physicalOrientation

            is ConstraintData.ScreenOff -> !isScreenOn
            is ConstraintData.ScreenOn -> isScreenOn
            is ConstraintData.DisplayResolution ->
                (displaySize.width == data.width && displaySize.height == data.height) ||
                    (displaySize.width == data.height && displaySize.height == data.width)
            is ConstraintData.FlashlightOff -> when (data.lens) {
                CameraLens.BACK -> !isBackFlashlightOn
                CameraLens.FRONT -> !isFrontFlashlightOn
            }

            is ConstraintData.FlashlightOn -> when (data.lens) {
                CameraLens.BACK -> isBackFlashlightOn
                CameraLens.FRONT -> isFrontFlashlightOn
            }

            is ConstraintData.WifiConnected -> {
                if (data.ssid == null) {
                    // connected to any network
                    connectedWifiSSID != null
                } else {
                    connectedWifiSSID == data.ssid
                }
            }

            is ConstraintData.WifiDisconnected ->
                if (data.ssid == null) {
                    // connected to no network
                    connectedWifiSSID == null
                } else {
                    connectedWifiSSID != data.ssid
                }

            is ConstraintData.WifiOff -> !isWifiEnabled
            is ConstraintData.WifiOn -> isWifiEnabled
            is ConstraintData.ImeChosen -> chosenImeId == data.imeId
            is ConstraintData.ImeNotChosen -> chosenImeId != data.imeId
            is ConstraintData.KeyboardShowing -> isKeyboardShowing
            is ConstraintData.KeyboardNotShowing -> !isKeyboardShowing
            is ConstraintData.DeviceIsLocked -> isLocked
            is ConstraintData.DeviceIsUnlocked -> !isLocked
            is ConstraintData.InPhoneCall -> callState == CallState.IN_PHONE_CALL
            is ConstraintData.NotInPhoneCall -> callState == CallState.NONE
            is ConstraintData.PhoneRinging -> callState == CallState.RINGING
            is ConstraintData.RingerMode -> ringerMode == data.ringerMode
            is ConstraintData.Charging -> isCharging
            is ConstraintData.Discharging -> !isCharging
            is ConstraintData.LockScreenShowing -> isLockscreenShowing
            is ConstraintData.LockScreenNotShowing -> !isLockscreenShowing
            is ConstraintData.Time -> {
                val startTime = data.startTime
                val endTime = data.endTime

                if (startTime.isAfter(endTime)) {
                    localTime.isAfter(startTime) || localTime.isBefore(endTime)
                } else {
                    localTime.isAfter(startTime) && localTime.isBefore(endTime)
                }
            }

            ConstraintData.HingeClosed ->
                hingeState is HingeState.Available && hingeState.isClosed()
            ConstraintData.HingeOpen ->
                hingeState is HingeState.Available && hingeState.isOpen()
            ConstraintData.NotificationPanelShowing -> isNotificationPanelShowing
            ConstraintData.NotificationPanelNotShowing -> !isNotificationPanelShowing
        }

        if (isSatisfied) {
            Timber.d("Constraint satisfied: $constraint")
        } else {
            Timber.d("Constraint not satisfied: $constraint")
        }

        return isSatisfied
    }
}
