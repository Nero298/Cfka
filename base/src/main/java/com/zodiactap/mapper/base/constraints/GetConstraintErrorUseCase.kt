package com.zodiactap.mapper.base.constraints

import com.zodiactap.mapper.system.apps.PackageManagerAdapter
import com.zodiactap.mapper.system.camera.CameraAdapter
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import com.zodiactap.mapper.system.permissions.SystemFeatureAdapter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@Singleton
class GetConstraintErrorUseCaseImpl @Inject constructor(
    private val packageManagerAdapter: PackageManagerAdapter,
    private val permissionAdapter: PermissionAdapter,
    private val systemFeatureAdapter: SystemFeatureAdapter,
    private val inputMethodAdapter: InputMethodAdapter,
    private val cameraAdapter: CameraAdapter,
) : GetConstraintErrorUseCase {
    private val invalidateConstraintErrors = merge(
        permissionAdapter.onPermissionsUpdate,
        inputMethodAdapter.inputMethods.drop(1).map { },
        packageManagerAdapter.onPackagesChanged,
    )

    override val constraintErrorSnapshot: Flow<ConstraintErrorSnapshot> = channelFlow {
        send(createSnapshot())

        invalidateConstraintErrors.collectLatest {
            send(createSnapshot())
        }
    }

    private fun createSnapshot(): ConstraintErrorSnapshot = LazyConstraintErrorSnapshot(
        packageManagerAdapter,
        permissionAdapter,
        systemFeatureAdapter,
        inputMethodAdapter,
        cameraAdapter,
    )
}

interface GetConstraintErrorUseCase {
    val constraintErrorSnapshot: Flow<ConstraintErrorSnapshot>
}
