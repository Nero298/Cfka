package com.zodiactap.mapper.base.logging

import android.Manifest
import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import com.zodiactap.mapper.base.utils.ShareUtils
import com.zodiactap.mapper.common.BuildConfigProvider
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.Success
import com.zodiactap.mapper.common.utils.then
import com.zodiactap.mapper.system.files.FileAdapter
import com.zodiactap.mapper.system.files.FileUtils
import com.zodiactap.mapper.system.files.IFile
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import com.zodiactap.mapper.system.shell.ShellAdapter
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ViewModelScoped
class ShareLogcatUseCaseImpl @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val fileAdapter: FileAdapter,
    private val shellAdapter: ShellAdapter,
    private val permissionAdapter: PermissionAdapter,
    private val buildConfigProvider: BuildConfigProvider,
) : ShareLogcatUseCase {

    override fun isPermissionGranted(): Boolean {
        return permissionAdapter.isGranted(Permission.READ_LOGS)
    }

    override fun grantPermission(): KMResult<*> {
        return permissionAdapter.grant(Manifest.permission.READ_LOGS)
    }

    override suspend fun share(): KMResult<Unit> {
        val fileName = "logs/logcat_${FileUtils.createFileDate()}.txt"

        return withContext(Dispatchers.IO) {
            val file: IFile = fileAdapter.getPrivateFile(fileName)
            file.createFile()

            val command = "logcat -d -f ${file.path}"

            shellAdapter.execute(command).then {
                val publicUri = fileAdapter.getPublicUriForPrivateFile(file)

                ShareUtils.shareFile(ctx, publicUri.toUri(), buildConfigProvider.packageName)
                Success(Unit)
            }
        }
    }
}

interface ShareLogcatUseCase {
    fun isPermissionGranted(): Boolean
    fun grantPermission(): KMResult<*>
    suspend fun share(): KMResult<Unit>
}
