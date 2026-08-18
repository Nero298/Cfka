package com.zodiactap.mapper.base.system.apps

import android.graphics.drawable.Drawable
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.system.apps.PackageInfo
import com.zodiactap.mapper.system.apps.PackageManagerAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DisplayAppsUseCaseImpl @Inject constructor(private val adapter: PackageManagerAdapter) :
    DisplayAppsUseCase {
    override val installedPackages: Flow<State<List<PackageInfo>>> = adapter.installedPackages

    override fun getAppName(packageName: String): KMResult<String> = adapter.getAppName(packageName)

    override fun getAppIcon(packageName: String): KMResult<Drawable> =
        adapter.getAppIcon(packageName)

    override fun getActivityLabel(packageName: String, activityClass: String): KMResult<String> =
        adapter.getActivityLabel(packageName, activityClass)

    override fun getActivityIcon(packageName: String, activityClass: String): KMResult<Drawable?> =
        adapter.getActivityIcon(packageName, activityClass)
}

interface DisplayAppsUseCase {
    val installedPackages: Flow<State<List<PackageInfo>>>

    fun getActivityLabel(packageName: String, activityClass: String): KMResult<String>
    fun getActivityIcon(packageName: String, activityClass: String): KMResult<Drawable?>
    fun getAppName(packageName: String): KMResult<String>
    fun getAppIcon(packageName: String): KMResult<Drawable>
}
