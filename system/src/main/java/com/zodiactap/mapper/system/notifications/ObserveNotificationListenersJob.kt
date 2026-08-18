package com.zodiactap.mapper.system.notifications

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import com.zodiactap.mapper.system.JobSchedulerHelper
import com.zodiactap.mapper.system.permissions.AndroidPermissionAdapter
import javax.inject.Inject

@AndroidEntryPoint
class ObserveNotificationListenersJob : JobService() {

    @Inject
    lateinit var permissionAdapter: AndroidPermissionAdapter

    override fun onStartJob(params: JobParameters?): Boolean {
        permissionAdapter.onPermissionsChanged()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            JobSchedulerHelper.observeEnabledNotificationListeners(applicationContext)
        }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean = false
}
