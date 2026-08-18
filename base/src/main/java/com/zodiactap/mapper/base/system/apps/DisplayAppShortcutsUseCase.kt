package com.zodiactap.mapper.base.system.apps

import android.graphics.drawable.Drawable
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.system.apps.AppShortcutAdapter
import com.zodiactap.mapper.system.apps.AppShortcutInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DisplayAppShortcutsUseCaseImpl @Inject constructor(
    private val appShortcutAdapter: AppShortcutAdapter,
) : DisplayAppShortcutsUseCase {
    override val shortcuts: Flow<State<List<AppShortcutInfo>>> =
        appShortcutAdapter.installedAppShortcuts

    override fun getShortcutName(appShortcutInfo: AppShortcutInfo): KMResult<String> =
        appShortcutAdapter.getShortcutName(appShortcutInfo)

    override fun getShortcutIcon(appShortcutInfo: AppShortcutInfo): KMResult<Drawable> =
        appShortcutAdapter.getShortcutIcon(appShortcutInfo)
}

interface DisplayAppShortcutsUseCase {
    val shortcuts: Flow<State<List<AppShortcutInfo>>>

    fun getShortcutName(appShortcutInfo: AppShortcutInfo): KMResult<String>
    fun getShortcutIcon(appShortcutInfo: AppShortcutInfo): KMResult<Drawable>
}
