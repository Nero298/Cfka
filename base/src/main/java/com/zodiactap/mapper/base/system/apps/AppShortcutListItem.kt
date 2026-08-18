package com.zodiactap.mapper.base.system.apps

import com.zodiactap.mapper.base.utils.ui.IconInfo
import com.zodiactap.mapper.base.utils.ui.SimpleListItemOld
import com.zodiactap.mapper.base.utils.ui.TintType
import com.zodiactap.mapper.system.apps.AppShortcutInfo

data class AppShortcutListItem(
    val shortcutInfo: AppShortcutInfo,
    val label: String,
    override val icon: IconInfo?,
) : SimpleListItemOld {
    override val id: String
        get() = shortcutInfo.toString()

    override val title: String
        get() = label

    override val subtitle: String? = null
    override val subtitleTint: TintType = TintType.None
    override val isEnabled: Boolean = true

    override fun getSearchableString() = label
}
