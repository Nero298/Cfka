package com.zodiactap.mapper.base.onboarding

import androidx.datastore.preferences.core.Preferences
import com.zodiactap.mapper.base.keymaps.KeyMap
import com.zodiactap.mapper.base.keymaps.KeyMapEntityMapper
import com.zodiactap.mapper.base.trigger.TriggerErrorSnapshot
import com.zodiactap.mapper.common.BuildConfigProvider
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.repositories.KeyMapRepository
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.data.utils.PrefDelegate
import com.zodiactap.mapper.system.files.FileAdapter
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import com.zodiactap.mapper.system.shizuku.ShizukuAdapter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class OnboardingUseCaseImpl @Inject constructor(
    private val settingsRepository: PreferenceRepository,
    private val fileAdapter: FileAdapter,
    private val shizukuAdapter: ShizukuAdapter,
    private val permissionAdapter: PermissionAdapter,
    private val keyMapRepository: KeyMapRepository,
    private val buildConfigProvider: BuildConfigProvider,
) : PreferenceRepository by settingsRepository,
    OnboardingUseCase {

    override var shownAppIntro by PrefDelegate(Keys.shownAppIntro, false)

    override val showWhatsNew = get(Keys.lastInstalledVersionCodeHomeScreen)
        .map { (it ?: -1) < buildConfigProvider.versionCode }

    override fun showedWhatsNew() {
        set(Keys.lastInstalledVersionCodeHomeScreen, buildConfigProvider.versionCode)
    }

    override fun getWhatsNewText(): String =
        with(fileAdapter.openAsset("whats-new.txt").bufferedReader()) {
            readText()
        }

    override val promptForShizukuPermission: Flow<Boolean> = combine(
        settingsRepository.get(Keys.shownShizukuPermissionPrompt),
        shizukuAdapter.isInstalled,
        permissionAdapter.isGrantedFlow(Permission.SHIZUKU),
    ) {
            shownPromptBefore,
            isShizkuInstalled,
            isShizukuPermissionGranted,
        ->
        shownPromptBefore != true && isShizkuInstalled && !isShizukuPermissionGranted
    }

    override val showShizukuAppIntroSlide: Boolean
        get() = shizukuAdapter.isInstalled.value

    override val hasViewedAdvancedTriggers: Flow<Boolean> =
        get(Keys.viewedAdvancedTriggers).map { it ?: false }

    override fun viewedAdvancedTriggers() {
        set(Keys.viewedAdvancedTriggers, true)
    }

    override fun showTapTarget(tapTarget: OnboardingTapTarget): Flow<Boolean> {
        val shownKey = getTapTargetKey(tapTarget)

        return settingsRepository.get(shownKey).map { isShown -> !(isShown ?: false) }
    }

    override fun completedTapTarget(tapTarget: OnboardingTapTarget) {
        val key = getTapTargetKey(tapTarget)
        settingsRepository.set(key, true)
    }

    private fun getTapTargetKey(tapTarget: OnboardingTapTarget): Preferences.Key<Boolean> {
        return when (tapTarget) {
            OnboardingTapTarget.CHOOSE_ACTION -> Keys.shownTapTargetChooseAction
            OnboardingTapTarget.CREATE_KEY_MAP -> Keys.shownTapTargetCreateKeyMap
        }
    }

    override val showMigrateScreenOffKeyMapsNotification: Flow<Boolean> =
        get(Keys.handledMigrateScreenOffKeyMapsNotification).map { isHandled ->
            if (isHandled == true) {
                return@map false
            }

            val keyMaps = keyMapRepository.getAll()
                .first()
                .map { keyMap -> KeyMapEntityMapper.fromEntity(keyMap, emptyList()) }

            isScreenOffTriggerMigrationRequired(keyMaps)
        }

    override fun handledMigrateScreenOffKeyMapsNotification() =
        set(Keys.handledMigrateScreenOffKeyMapsNotification, true)

    private fun isScreenOffTriggerMigrationRequired(keyMapList: List<KeyMap>): Boolean {
        for (keyMap in keyMapList) {
            for (key in keyMap.trigger.keys) {
                if (TriggerErrorSnapshot.isScreenOffTriggerMigrationRequired(keyMap.trigger, key)) {
                    return true
                }
            }
        }

        return false
    }
}

interface OnboardingUseCase {
    var shownAppIntro: Boolean

    val showWhatsNew: Flow<Boolean>
    fun showedWhatsNew()
    fun getWhatsNewText(): String

    val promptForShizukuPermission: Flow<Boolean>

    val showShizukuAppIntroSlide: Boolean

    val hasViewedAdvancedTriggers: Flow<Boolean>
    fun viewedAdvancedTriggers()

    fun showTapTarget(tapTarget: OnboardingTapTarget): Flow<Boolean>
    fun completedTapTarget(tapTarget: OnboardingTapTarget)

    val showMigrateScreenOffKeyMapsNotification: Flow<Boolean>
    fun handledMigrateScreenOffKeyMapsNotification()
}
