package com.zodiactap.mapper.system.apps

data class PackageInfo(
    val packageName: String,
    val activities: List<ActivityInfo>,
    val isEnabled: Boolean,
    /**
     * Whether this package can be launched.
     */
    val isLaunchable: Boolean,
    val versionCode: Long,
)
