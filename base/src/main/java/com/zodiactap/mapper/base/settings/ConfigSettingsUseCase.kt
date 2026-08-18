package com.zodiactap.mapper.base.settings

import androidx.datastore.preferences.core.Preferences
import com.zodiactap.mapper.base.actions.sound.SoundFileInfo
import com.zodiactap.mapper.base.actions.sound.SoundsManager
import com.zodiactap.mapper.base.system.inputmethod.KeyMapperImeHelper
import com.zodiactap.mapper.base.system.inputmethod.SwitchImeInterface
import com.zodiactap.mapper.common.BuildConfigProvider
import com.zodiactap.mapper.common.utils.InputDeviceInfo
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.common.utils.then
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.PreferenceDefaults
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.system.apps.PackageManagerAdapter
import com.zodiactap.mapper.system.devices.DevicesAdapter
import com.zodiactap.mapper.system.inputmethod.ImeInfo
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import com.zodiactap.mapper.system.notifications.NotificationAdapter
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import com.zodiactap.mapper.system.root.SuAdapter
import com.zodiactap.mapper.system.shizuku.ShizukuAdapter
import com.zodiactap.mapper.system.shizuku.ShizukuUtils
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

class ConfigSettingsUseCaseImpl @Inject constructor(
    private val preferences: PreferenceRepository,
    private val permissionAdapter: PermissionAdapter,
    private val inputMethodAdapter: InputMethodAdapter,
    private val switchImeInterface: SwitchImeInterface,
    private val soundsManager: SoundsManager,
    private val suAdapter: SuAdapter,
    private val packageManagerAdapter: PackageManagerAdapter,
    private val shizukuAdapter: ShizukuAdapter,
    private val devicesAdapter: DevicesAdapter,
    private val buildConfigProvider: BuildConfigProvider,
    private val notificationAdapter: NotificationAdapter,
) : ConfigSettingsUseCase {

    private val imeHelper by lazy {
        KeyMapperImeHelper(
            switchImeInterface,
            inputMethodAdapter,
            buildConfigProvider.packageName,
        )
    }

    override val theme: Flow<Theme> =
        preferences.get(Keys.darkTheme).map { it ?: PreferenceDefaults.DARK_THEME }.map { value ->
            Theme.entries.single { it.value == value.toInt() }
        }

    override val isRootGranted: Flow<Boolean> = suAdapter.isRootGranted.map { it ?: false }

    override val isWriteSecureSettingsGranted: Flow<Boolean> = channelFlow {
        send(permissionAdapter.isGranted(Permission.WRITE_SECURE_SETTINGS))

        permissionAdapter.onPermissionsUpdate.collectLatest {
            send(permissionAdapter.isGranted(Permission.WRITE_SECURE_SETTINGS))
        }
    }

    override val isShizukuInstalled: Flow<Boolean> by lazy {
        shizukuAdapter.isInstalled
    }

    override val isShizukuStarted: Flow<Boolean> by lazy {
        shizukuAdapter.isStarted
    }

    override val isShizukuPermissionGranted: Flow<Boolean> = channelFlow {
        send(permissionAdapter.isGranted(Permission.SHIZUKU))

        permissionAdapter.onPermissionsUpdate.collectLatest {
            send(permissionAdapter.isGranted(Permission.SHIZUKU))
        }
    }

    override val isCompatibleImeChosen: Flow<Boolean> = inputMethodAdapter.chosenIme.map {
        imeHelper.isCompatibleImeChosen()
    }

    override val isCompatibleImeEnabled: Flow<Boolean> = inputMethodAdapter.inputMethods.map {
        imeHelper.isCompatibleImeEnabled()
    }

    override val connectedInputDevices: StateFlow<State<List<InputDeviceInfo>>>
        get() = devicesAdapter.connectedInputDevices

    override suspend fun enableCompatibleIme() {
        imeHelper.enableCompatibleInputMethod()
    }

    override suspend fun chooseCompatibleIme(): KMResult<ImeInfo> =
        imeHelper.chooseCompatibleInputMethod().then { inputMethodAdapter.getInfoById(it) }

    override suspend fun showImePicker(): KMResult<*> =
        inputMethodAdapter.showImePicker(fromForeground = true)

    override fun <T> getPreference(key: Preferences.Key<T>) = preferences.get(key)

    override fun <T> setPreference(key: Preferences.Key<T>, value: T?) = preferences.set(key, value)

    override val automaticBackupLocation =
        preferences.get(Keys.automaticBackupLocation)

    override fun setAutomaticBackupLocation(uri: String) {
        preferences.set(Keys.automaticBackupLocation, uri)
    }

    override fun disableAutomaticBackup() {
        preferences.set(Keys.automaticBackupLocation, null)
    }

    override val defaultLongPressDelay: Flow<Int> =
        preferences.get(Keys.defaultLongPressDelay)
            .map { it ?: PreferenceDefaults.LONG_PRESS_DELAY }

    override val defaultDoublePressDelay: Flow<Int> =
        preferences.get(Keys.defaultDoublePressDelay)
            .map { it ?: PreferenceDefaults.DOUBLE_PRESS_DELAY }

    override val defaultRepeatDelay: Flow<Int> =
        preferences.get(Keys.defaultRepeatDelay)
            .map { it ?: PreferenceDefaults.REPEAT_DELAY }

    override val defaultSequenceTriggerTimeout: Flow<Int> =
        preferences.get(Keys.defaultSequenceTriggerTimeout)
            .map { it ?: PreferenceDefaults.SEQUENCE_TRIGGER_TIMEOUT }

    override val defaultVibrateDuration: Flow<Int> =
        preferences.get(Keys.defaultVibrateDuration)
            .map { it ?: PreferenceDefaults.VIBRATION_DURATION }

    override val defaultRepeatRate: Flow<Int> =
        preferences.get(Keys.defaultRepeatRate)
            .map { it ?: PreferenceDefaults.REPEAT_RATE }

    override fun resetDefaultMappingOptions() {
        preferences.set(Keys.defaultLongPressDelay, null)
        preferences.set(Keys.defaultDoublePressDelay, null)
        preferences.set(Keys.defaultRepeatDelay, null)
        preferences.set(Keys.defaultSequenceTriggerTimeout, null)
        preferences.set(Keys.defaultVibrateDuration, null)
        preferences.set(Keys.defaultRepeatRate, null)
    }

    override fun requestWriteSecureSettingsPermission() {
        permissionAdapter.request(Permission.WRITE_SECURE_SETTINGS)
    }

    override fun requestShizukuPermission() {
        permissionAdapter.request(Permission.SHIZUKU)
    }

    override fun downloadShizuku() {
        packageManagerAdapter.downloadApp(ShizukuUtils.SHIZUKU_PACKAGE)
    }

    override fun openShizukuApp() {
        packageManagerAdapter.openApp(ShizukuUtils.SHIZUKU_PACKAGE)
    }

    override fun requestNotificationsPermission() {
        permissionAdapter.request(Permission.POST_NOTIFICATIONS)
    }

    override fun requestRootPermission() {
        suAdapter.requestPermission()
    }

    override fun isNotificationsPermissionGranted(): Boolean =
        permissionAdapter.isGranted(Permission.POST_NOTIFICATIONS)

    override fun getSoundFiles(): List<SoundFileInfo> = soundsManager.soundFiles.value

    override fun deleteSoundFiles(uid: List<String>) {
        uid.forEach {
            soundsManager.deleteSound(it)
        }
    }

    override fun resetAllSettings() {
        preferences.deleteAll()
    }

    override fun openNotificationChannelSettings(channelId: String) {
        notificationAdapter.openChannelSettings(channelId)
    }
}

interface ConfigSettingsUseCase {
    fun <T> getPreference(key: Preferences.Key<T>): Flow<T?>
    fun <T> setPreference(key: Preferences.Key<T>, value: T?)

    val theme: Flow<Theme>
    val automaticBackupLocation: Flow<String?>
    fun setAutomaticBackupLocation(uri: String)
    fun disableAutomaticBackup()
    val isRootGranted: Flow<Boolean>
    fun requestRootPermission()

    val isWriteSecureSettingsGranted: Flow<Boolean>
    val isShizukuInstalled: Flow<Boolean>
    val isShizukuStarted: Flow<Boolean>
    val isShizukuPermissionGranted: Flow<Boolean>
    fun downloadShizuku()

    fun openShizukuApp()
    val isCompatibleImeChosen: Flow<Boolean>
    val isCompatibleImeEnabled: Flow<Boolean>
    suspend fun enableCompatibleIme()
    suspend fun chooseCompatibleIme(): KMResult<ImeInfo>
    suspend fun showImePicker(): KMResult<*>

    val defaultLongPressDelay: Flow<Int>
    val defaultDoublePressDelay: Flow<Int>
    val defaultRepeatDelay: Flow<Int>
    val defaultSequenceTriggerTimeout: Flow<Int>
    val defaultVibrateDuration: Flow<Int>

    val defaultRepeatRate: Flow<Int>
    fun getSoundFiles(): List<SoundFileInfo>
    fun deleteSoundFiles(uid: List<String>)
    fun resetDefaultMappingOptions()
    fun requestWriteSecureSettingsPermission()
    fun requestNotificationsPermission()
    fun isNotificationsPermissionGranted(): Boolean
    fun openNotificationChannelSettings(channelId: String)

    fun requestShizukuPermission()

    val connectedInputDevices: StateFlow<State<List<InputDeviceInfo>>>
    fun resetAllSettings()
}
