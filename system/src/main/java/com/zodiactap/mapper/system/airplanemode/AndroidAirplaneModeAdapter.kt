package com.zodiactap.mapper.system.airplanemode

import android.content.Context
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.SettingsUtils
import com.zodiactap.mapper.common.utils.Success
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManager
import com.zodiactap.mapper.system.root.SuAdapter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Singleton
class AndroidAirplaneModeAdapter @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val systemBridgeConnectionManager: SystemBridgeConnectionManager,
    private val suAdapter: SuAdapter,
    private val coroutineScope: CoroutineScope,
) : AirplaneModeAdapter {

    override suspend fun enable(): KMResult<*> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            systemBridgeConnectionManager.run { bridge -> bridge.setAirplaneMode(true) }
        } else {
            val success = SettingsUtils.putGlobalSetting(ctx, Settings.Global.AIRPLANE_MODE_ON, 1)
            broadcastAirplaneModeChanged(true)
            if (success) {
                Success(Unit)
            } else {
                KMError.FailedToModifySystemSetting(Settings.Global.AIRPLANE_MODE_ON)
            }
        }
    }

    override suspend fun disable(): KMResult<*> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            systemBridgeConnectionManager.run { bridge -> bridge.setAirplaneMode(false) }
        } else {
            val success = SettingsUtils.putGlobalSetting(ctx, Settings.Global.AIRPLANE_MODE_ON, 0)
            if (success) {
                broadcastAirplaneModeChanged(false)
                Success(Unit)
            } else {
                KMError.FailedToModifySystemSetting(Settings.Global.AIRPLANE_MODE_ON)
            }
        }
    }

    override fun isEnabled(): Boolean =
        SettingsUtils.getGlobalSetting<Int>(ctx, Settings.Global.AIRPLANE_MODE_ON) == 1

    private fun broadcastAirplaneModeChanged(enabled: Boolean) {
        coroutineScope.launch {
            suAdapter.execute(
                "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state $enabled",
            )
        }
    }
}
