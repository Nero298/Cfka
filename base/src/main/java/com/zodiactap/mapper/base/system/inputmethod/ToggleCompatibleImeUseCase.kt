package com.zodiactap.mapper.base.system.inputmethod

import android.os.Build
import com.zodiactap.mapper.common.BuildConfigProvider
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.then
import com.zodiactap.mapper.system.accessibility.AccessibilityServiceAdapter
import com.zodiactap.mapper.system.accessibility.AccessibilityServiceState
import com.zodiactap.mapper.system.inputmethod.ImeInfo
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Singleton
class ToggleCompatibleImeUseCaseImpl @Inject constructor(
    private val inputMethodAdapter: InputMethodAdapter,
    private val buildConfigProvider: BuildConfigProvider,
    private val switchImeInterface: SwitchImeInterface,
    private val serviceAdapter: AccessibilityServiceAdapter,
    private val permissionAdapter: PermissionAdapter,
) : ToggleCompatibleImeUseCase {
    private val keyMapperImeHelper =
        KeyMapperImeHelper(switchImeInterface, inputMethodAdapter, buildConfigProvider.packageName)

    override val sufficientPermissions: Flow<Boolean> = channelFlow {
        suspend fun invalidate() {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    serviceAdapter.state.first() == AccessibilityServiceState.ENABLED -> send(true)

                permissionAdapter.isGranted(Permission.WRITE_SECURE_SETTINGS) -> send(true)

                else -> send(false)
            }
        }

        invalidate()

        launch {
            permissionAdapter.onPermissionsUpdate.collectLatest {
                invalidate()
            }
        }

        launch {
            serviceAdapter.state.collectLatest {
                invalidate()
            }
        }
    }

    override suspend fun toggle(): KMResult<ImeInfo> =
        keyMapperImeHelper.toggleCompatibleInputMethod().then { inputMethodAdapter.getInfoById(it) }
}

interface ToggleCompatibleImeUseCase {
    val sufficientPermissions: Flow<Boolean>

    suspend fun toggle(): KMResult<ImeInfo>
}
