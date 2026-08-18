package com.zodiactap.mapper.base.shortcuts

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.os.bundleOf
import dagger.hilt.android.scopes.ViewModelScoped
import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.system.apps.AppShortcutAdapter
import javax.inject.Inject

@ViewModelScoped
class CreateKeyMapShortcutUseCaseImpl @Inject constructor(
    private val appShortcutAdapter: AppShortcutAdapter,
) : CreateKeyMapShortcutUseCase {

    companion object {
        private const val ACTION_TRIGGER_KEYMAP_BY_UID =
            "com.zodiactap.mapper.ACTION_TRIGGER_KEYMAP_BY_UID"
        private const val EXTRA_KEYMAP_UID = "com.zodiactap.mapper.EXTRA_KEYMAP_UID"
    }

    override val isSupported: Boolean
        get() = appShortcutAdapter.areLauncherShortcutsSupported

    override fun pinShortcut(
        keyMapUid: String,
        shortcutLabel: String,
        icon: Drawable?,
    ): KMResult<*> {
        val shortcut = if (icon == null) {
            appShortcutAdapter.createLauncherShortcut(
                iconResId = R.mipmap.ic_launcher_round,
                label = shortcutLabel,
                intentAction = ACTION_TRIGGER_KEYMAP_BY_UID,
                bundleOf(EXTRA_KEYMAP_UID to keyMapUid),
            )
        } else {
            appShortcutAdapter.createLauncherShortcut(
                icon = icon,
                label = shortcutLabel,
                intentAction = ACTION_TRIGGER_KEYMAP_BY_UID,
                bundleOf(EXTRA_KEYMAP_UID to keyMapUid),
            )
        }
        return appShortcutAdapter.pinShortcut(shortcut)
    }

    override fun createIntent(keyMapUid: String, shortcutLabel: String, icon: Drawable?): Intent {
        val shortcut = if (icon == null) {
            appShortcutAdapter.createLauncherShortcut(
                iconResId = R.mipmap.ic_launcher_round,
                label = shortcutLabel,
                intentAction = ACTION_TRIGGER_KEYMAP_BY_UID,
                bundleOf(EXTRA_KEYMAP_UID to keyMapUid),
            )
        } else {
            appShortcutAdapter.createLauncherShortcut(
                icon = icon,
                label = shortcutLabel,
                intentAction = ACTION_TRIGGER_KEYMAP_BY_UID,
                bundleOf(EXTRA_KEYMAP_UID to keyMapUid),
            )
        }
        return appShortcutAdapter.createShortcutResultIntent(shortcut)
    }
}

interface CreateKeyMapShortcutUseCase {
    val isSupported: Boolean

    fun pinShortcut(keyMapUid: String, shortcutLabel: String, icon: Drawable?): KMResult<*>

    fun createIntent(keyMapUid: String, shortcutLabel: String, icon: Drawable?): Intent
}
