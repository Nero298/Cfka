package com.zodiactap.mapper.base.actions

import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.base.system.notifications.NotificationController
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.system.SystemError
import com.zodiactap.mapper.system.camera.CameraAdapter
import com.zodiactap.mapper.system.camera.CameraFlashInfo
import com.zodiactap.mapper.system.camera.CameraLens
import com.zodiactap.mapper.system.inputmethod.ImeInfo
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import com.zodiactap.mapper.system.notifications.NotificationAdapter
import com.zodiactap.mapper.system.notifications.NotificationModel
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import com.zodiactap.mapper.system.permissions.SystemFeatureAdapter
import com.zodiactap.mapper.system.phone.PhoneAdapter
import com.zodiactap.mapper.system.settings.SettingType
import com.zodiactap.mapper.system.settings.SettingsAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.merge

class CreateActionUseCaseImpl @Inject constructor(
    private val inputMethodAdapter: InputMethodAdapter,
    private val systemFeatureAdapter: SystemFeatureAdapter,
    private val cameraAdapter: CameraAdapter,
    private val permissionAdapter: PermissionAdapter,
    private val phoneAdapter: PhoneAdapter,
    private val settingsAdapter: SettingsAdapter,
    private val notificationAdapter: NotificationAdapter,
) : CreateActionUseCase,
    IsActionSupportedUseCase by IsActionSupportedUseCaseImpl(
        systemFeatureAdapter,
        cameraAdapter,
        permissionAdapter,
    ) {
    override suspend fun getInputMethods(): List<ImeInfo> = inputMethodAdapter.inputMethods.first()

    override fun getFlashlightLenses(): Set<CameraLens> {
        return CameraLens.entries.filter { cameraAdapter.getFlashInfo(it) != null }.toSet()
    }

    override fun getFlashInfo(lens: CameraLens): CameraFlashInfo? {
        return cameraAdapter.getFlashInfo(lens)
    }

    override fun toggleFlashlight(lens: CameraLens, strength: Float) {
        cameraAdapter.toggleFlashlight(lens, strength)
    }

    override fun disableFlashlight() {
        cameraAdapter.disableFlashlight(CameraLens.FRONT)
        cameraAdapter.disableFlashlight(CameraLens.BACK)
    }

    override fun setFlashlightBrightness(lens: CameraLens, strength: Float) {
        cameraAdapter.enableFlashlight(lens, strength)
    }

    override fun isFlashlightEnabled(): Flow<Boolean> {
        return merge(
            cameraAdapter.isFlashlightOnFlow(CameraLens.FRONT),
            cameraAdapter.isFlashlightOnFlow(CameraLens.BACK),
        )
    }

    override fun requestPermission(permission: Permission) {
        permissionAdapter.request(permission)
    }

    override suspend fun testSms(number: String, message: String): KMResult<Unit> {
        if (!permissionAdapter.isGranted(Permission.SEND_SMS)) {
            return SystemError.PermissionDenied(Permission.SEND_SMS)
        }

        return phoneAdapter.sendSms(number, message)
    }

    override fun setSettingValue(
        settingType: SettingType,
        key: String,
        value: String,
    ): KMResult<Unit> {
        return settingsAdapter.setValue(settingType, key, value)
    }

    override fun getRequiredPermissionForSettingType(settingType: SettingType): Permission {
        return when (settingType) {
            SettingType.SYSTEM -> Permission.WRITE_SETTINGS
            SettingType.SECURE, SettingType.GLOBAL -> Permission.WRITE_SECURE_SETTINGS
        }
    }

    override fun isPermissionGrantedFlow(permission: Permission): Flow<Boolean> {
        return permissionAdapter.isGrantedFlow(permission)
    }

    override fun testCreateNotification(title: String, text: String, timeoutMs: Long?) {
        val notification = NotificationModel(
            // Use the same id for notifications created when testing so they overwrite each other
            id = 0,
            channel = NotificationController.CHANNEL_CUSTOM_NOTIFICATIONS,
            title = title,
            text = text,
            icon = R.drawable.ic_launcher_foreground,
            showOnLockscreen = true,
            onGoing = false,
            autoCancel = true,
            timeout = timeoutMs,
            bigTextStyle = true,
        )

        notificationAdapter.showNotification(notification)
    }
}

interface CreateActionUseCase : IsActionSupportedUseCase {
    suspend fun getInputMethods(): List<ImeInfo>

    fun isFlashlightEnabled(): Flow<Boolean>
    fun setFlashlightBrightness(lens: CameraLens, strength: Float)
    fun toggleFlashlight(lens: CameraLens, strength: Float)
    fun disableFlashlight()
    fun getFlashlightLenses(): Set<CameraLens>
    fun getFlashInfo(lens: CameraLens): CameraFlashInfo?

    fun requestPermission(permission: Permission)
    suspend fun testSms(number: String, message: String): KMResult<Unit>
    fun setSettingValue(settingType: SettingType, key: String, value: String): KMResult<Unit>
    fun getRequiredPermissionForSettingType(settingType: SettingType): Permission
    fun isPermissionGrantedFlow(permission: Permission): Flow<Boolean>
    fun testCreateNotification(title: String, text: String, timeoutMs: Long?)
}
