package com.zodiactap.mapper.base.actions

import com.zodiactap.mapper.base.actions.sound.SoundsManager
import com.zodiactap.mapper.base.system.inputmethod.SwitchImeInterface
import com.zodiactap.mapper.common.BuildConfigProvider
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManager
import com.zodiactap.mapper.system.apps.PackageManagerAdapter
import com.zodiactap.mapper.system.camera.CameraAdapter
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import com.zodiactap.mapper.system.notifications.NotificationReceiverAdapter
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import com.zodiactap.mapper.system.permissions.SystemFeatureAdapter
import com.zodiactap.mapper.system.ringtones.RingtoneAdapter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@Singleton
class GetActionErrorUseCaseImpl @Inject constructor(
    private val packageManagerAdapter: PackageManagerAdapter,
    private val inputMethodAdapter: InputMethodAdapter,
    private val switchImeInterface: SwitchImeInterface,
    private val permissionAdapter: PermissionAdapter,
    private val systemFeatureAdapter: SystemFeatureAdapter,
    private val cameraAdapter: CameraAdapter,
    private val soundsManager: SoundsManager,
    private val ringtoneAdapter: RingtoneAdapter,
    private val buildConfigProvider: BuildConfigProvider,
    private val systemBridgeConnectionManager: SystemBridgeConnectionManager,
    private val preferenceRepository: PreferenceRepository,
    private val notificationReceiverAdapter: NotificationReceiverAdapter,
) : GetActionErrorUseCase {

    private val invalidateActionErrors = merge(
        inputMethodAdapter.chosenIme.drop(1).map { },
        // invalidate when the input methods change
        inputMethodAdapter.inputMethods.drop(1).map { },
        permissionAdapter.onPermissionsUpdate,
        soundsManager.soundFiles.drop(1).map { },
        packageManagerAdapter.onPackagesChanged,
        notificationReceiverAdapter.isEnabled,
        merge(
            systemBridgeConnectionManager.connectionState.drop(1).map { },
            preferenceRepository.get(Keys.keyEventActionsUseSystemBridge),
        ),
    )

    override val actionErrorSnapshot: Flow<ActionErrorSnapshot> = channelFlow {
        send(createSnapshot())

        invalidateActionErrors.collectLatest {
            send(createSnapshot())
        }
    }

    private fun createSnapshot(): ActionErrorSnapshot {
        return LazyActionErrorSnapshot(
            packageManagerAdapter,
            inputMethodAdapter,
            switchImeInterface,
            permissionAdapter,
            systemFeatureAdapter,
            cameraAdapter,
            soundsManager,
            ringtoneAdapter,
            buildConfigProvider,
            systemBridgeConnectionManager,
            preferenceRepository,
        )
    }
}

interface GetActionErrorUseCase {
    val actionErrorSnapshot: Flow<ActionErrorSnapshot>
}
