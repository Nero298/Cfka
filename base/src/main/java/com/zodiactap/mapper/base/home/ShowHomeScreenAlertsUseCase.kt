package com.zodiactap.mapper.base.home

import com.zodiactap.mapper.base.keymaps.PauseKeyMapsUseCase
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManager
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionState
import com.zodiactap.mapper.sysbridge.service.SystemBridgeSetupController
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ShowHomeScreenAlertsUseCaseImpl @Inject constructor(
    private val preferences: PreferenceRepository,
    private val permissions: PermissionAdapter,
    private val pauseKeyMapsUseCase: PauseKeyMapsUseCase,
    private val systemBridgeConnectionManager: SystemBridgeConnectionManager,
    private val systemBridgeSetupController: SystemBridgeSetupController,
) : ShowHomeScreenAlertsUseCase {
    override val hideAlerts: Flow<Boolean> =
        preferences.get(Keys.hideHomeScreenAlerts).map { it == true }

    override val isBatteryOptimised: Flow<Boolean> =
        permissions.isGrantedFlow(Permission.IGNORE_BATTERY_OPTIMISATION)
            .map { !it } // if granted then battery is NOT optimised

    override val areKeyMapsPaused: Flow<Boolean> = pauseKeyMapsUseCase.isPaused

    override val isLoggingEnabled: Flow<Boolean> = preferences.get(Keys.log).map { it == true }

    override fun disableBatteryOptimisation() {
        permissions.request(Permission.IGNORE_BATTERY_OPTIMISATION)
    }

    override fun resumeMappings() {
        pauseKeyMapsUseCase.resume()
    }

    override fun disableLogging() {
        preferences.set(Keys.log, false)
    }

    override val showNotificationPermissionAlert: Flow<Boolean> =
        combine(
            permissions.isGrantedFlow(Permission.POST_NOTIFICATIONS),
            preferences.get(Keys.neverShowNotificationPermissionAlert).map { it ?: false },
        ) { isGranted, neverShow ->
            !isGranted && !neverShow
        }

    override val showXiaomiAdbSecuritySettingsWarning: Flow<Boolean> =
        combine(
            systemBridgeConnectionManager.connectionState,
            systemBridgeSetupController.xiaomiAdbSecuritySettingsEnabled,
        ) { connectionState, xiaomiSettingEnabled ->
            connectionState is SystemBridgeConnectionState.Connected &&
                !xiaomiSettingEnabled
        }

    override fun launchDeveloperOptions() {
        systemBridgeSetupController.launchDeveloperOptions()
    }

    override fun requestNotificationPermission() {
        permissions.request(Permission.POST_NOTIFICATIONS)
    }

    override fun neverShowNotificationPermissionAlert() {
        preferences.set(Keys.neverShowNotificationPermissionAlert, true)
    }
}

interface ShowHomeScreenAlertsUseCase {
    val hideAlerts: Flow<Boolean>
    fun disableBatteryOptimisation()
    val isBatteryOptimised: Flow<Boolean>
    val areKeyMapsPaused: Flow<Boolean>
    fun resumeMappings()

    val isLoggingEnabled: Flow<Boolean>
    fun disableLogging()

    val showNotificationPermissionAlert: Flow<Boolean>
    fun requestNotificationPermission()
    fun neverShowNotificationPermissionAlert()

    val showXiaomiAdbSecuritySettingsWarning: Flow<Boolean>

    fun launchDeveloperOptions()
}
