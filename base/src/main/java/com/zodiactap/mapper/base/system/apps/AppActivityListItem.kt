package com.zodiactap.mapper.base.system.apps

import com.zodiactap.mapper.base.utils.ui.IconInfo
import com.zodiactap.mapper.base.utils.ui.SimpleListItemOld
import com.zodiactap.mapper.base.utils.ui.TintType
import com.zodiactap.mapper.system.apps.ActivityInfo

data class AppActivityListItem(
    val appName: String,
    val activityInfo: ActivityInfo,
    override val icon: IconInfo?,
) : SimpleListItemOld {
    override val id: String
        get() = "${activityInfo.packageName}${activityInfo.activityName}"

    override val title: String
        get() = appName

    override val subtitle: String
        get() = activityInfo.activityName

    override val subtitleTint: TintType
        get() = TintType.None

    override val isEnabled: Boolean = true

    override fun getSearchableString() =
        "$appName ${activityInfo.packageName} ${activityInfo.activityName}"
}
