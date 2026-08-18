package com.zodiactap.mapper.base.constraints

import android.content.pm.PackageManager
import android.os.Build
import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.onSuccess
import com.zodiactap.mapper.system.SystemError
import com.zodiactap.mapper.system.apps.PackageManagerAdapter
import com.zodiactap.mapper.system.camera.CameraAdapter
import com.zodiactap.mapper.system.camera.CameraLens
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import com.zodiactap.mapper.system.permissions.SystemFeatureAdapter

class LazyConstraintErrorSnapshot(
    private val packageManager: PackageManagerAdapter,
    private val permissionAdapter: PermissionAdapter,
    private val systemFeatureAdapter: SystemFeatureAdapter,
    private val inputMethodAdapter: InputMethodAdapter,
    private val cameraAdapter: CameraAdapter,
) : ConstraintErrorSnapshot {

    private val inputMethods by lazy { inputMethodAdapter.inputMethods.value }
    private val grantedPermissions: MutableMap<Permission, Boolean> = mutableMapOf()
    private val flashLenses by lazy {
        buildSet {
            if (cameraAdapter.getFlashInfo(CameraLens.FRONT) != null) {
                add(CameraLens.FRONT)
            }

            if (cameraAdapter.getFlashInfo(CameraLens.BACK) != null) {
                add(CameraLens.BACK)
            }
        }
    }

    override fun getError(constraint: Constraint): KMError? {
        when (constraint.data) {
            is ConstraintData.AppInForeground -> return getAppError(constraint.data.packageName)
            is ConstraintData.AppNotInForeground -> return getAppError(constraint.data.packageName)

            is ConstraintData.AppPlayingMedia -> {
                if (!isPermissionGranted(Permission.NOTIFICATION_LISTENER)) {
                    return SystemError.PermissionDenied(Permission.NOTIFICATION_LISTENER)
                }

                return getAppError(constraint.data.packageName)
            }

            is ConstraintData.AppNotPlayingMedia -> {
                if (!isPermissionGranted(Permission.NOTIFICATION_LISTENER)) {
                    return SystemError.PermissionDenied(Permission.NOTIFICATION_LISTENER)
                }

                return getAppError(constraint.data.packageName)
            }

            ConstraintData.MediaPlaying, ConstraintData.NoMediaPlaying -> {
                if (!isPermissionGranted(Permission.NOTIFICATION_LISTENER)) {
                    return SystemError.PermissionDenied(Permission.NOTIFICATION_LISTENER)
                }
            }

            is ConstraintData.BtDeviceConnected,
            is ConstraintData.BtDeviceDisconnected,
                -> {
                if (!systemFeatureAdapter.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
                    return KMError.SystemFeatureNotSupported(PackageManager.FEATURE_BLUETOOTH)
                }

                if (!isPermissionGranted(Permission.FIND_NEARBY_DEVICES)) {
                    return SystemError.PermissionDenied(Permission.FIND_NEARBY_DEVICES)
                }
            }

            is ConstraintData.OrientationCustom,
            ConstraintData.OrientationLandscape,
            ConstraintData.OrientationPortrait,
                ->
                if (!isPermissionGranted(Permission.WRITE_SETTINGS)) {
                    return SystemError.PermissionDenied(Permission.WRITE_SETTINGS)
                }

            is ConstraintData.FlashlightOn -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return KMError.SdkVersionTooLow(minSdk = Build.VERSION_CODES.M)
                }

                if (!flashLenses.contains(constraint.data.lens)) {
                    return when (constraint.data.lens) {
                        CameraLens.FRONT -> KMError.FrontFlashNotFound
                        CameraLens.BACK -> KMError.BackFlashNotFound
                    }
                }
            }

            is ConstraintData.FlashlightOff -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return KMError.SdkVersionTooLow(minSdk = Build.VERSION_CODES.M)
                }

                if (!flashLenses.contains(constraint.data.lens)) {
                    return when (constraint.data.lens) {
                        CameraLens.FRONT -> KMError.FrontFlashNotFound
                        CameraLens.BACK -> KMError.BackFlashNotFound
                    }
                }
            }

            is ConstraintData.WifiConnected, is ConstraintData.WifiDisconnected -> {
                if (!isPermissionGranted(Permission.ACCESS_FINE_LOCATION)) {
                    return SystemError.PermissionDenied(Permission.ACCESS_FINE_LOCATION)
                }
            }

            is ConstraintData.ImeChosen -> {
                if (inputMethods.none { it.id == constraint.data.imeId }) {
                    return KMError.InputMethodNotFound(constraint.data.imeLabel)
                }
            }

            is ConstraintData.InPhoneCall,
            is ConstraintData.PhoneRinging,
            is ConstraintData.NotInPhoneCall,
                -> {
                if (!isPermissionGranted(Permission.READ_PHONE_STATE)) {
                    return SystemError.PermissionDenied(Permission.READ_PHONE_STATE)
                }
            }

            else -> Unit
        }

        return null
    }

    private fun getAppError(packageName: String): KMError? {
        packageManager.isAppEnabled(packageName).onSuccess { isEnabled ->
            if (!isEnabled) {
                return KMError.AppDisabled(packageName)
            }
        }

        if (!packageManager.isAppInstalled(packageName)) {
            return KMError.AppNotFound(packageName)
        }

        return null
    }

    private fun isPermissionGranted(permission: Permission): Boolean {
        if (grantedPermissions.contains(permission)) {
            return grantedPermissions[permission]!!
        } else {
            val isGranted = permissionAdapter.isGranted(permission)
            grantedPermissions[permission] = isGranted
            return isGranted
        }
    }
}

interface ConstraintErrorSnapshot {
    fun getError(constraint: Constraint): KMError?
}
