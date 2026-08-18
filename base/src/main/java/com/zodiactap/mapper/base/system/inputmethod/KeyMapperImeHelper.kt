package com.zodiactap.mapper.base.system.inputmethod

import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.Success
import com.zodiactap.mapper.common.utils.firstBlocking
import com.zodiactap.mapper.common.utils.isSuccess
import com.zodiactap.mapper.common.utils.onSuccess
import com.zodiactap.mapper.common.utils.then
import com.zodiactap.mapper.common.utils.valueOrNull
import com.zodiactap.mapper.system.inputmethod.ImeInfo
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KeyMapperImeHelper(
    private val switchImeInterface: SwitchImeInterface,
    private val imeAdapter: InputMethodAdapter,
    private val packageName: String,
) {
    companion object {
        const val KEY_MAPPER_GUI_IME_PACKAGE =
            "com.zodiactap.mapper.inputmethod.latin"

        private const val KEY_MAPPER_LEANBACK_IME_PACKAGE =
            "com.zodiactap.mapper.inputmethod.leanback"

        private const val KEY_MAPPER_HACKERS_KEYBOARD_PACKAGE =
            "com.zodiactap.mapper.inputmethod.hackers"

        const val MIN_SUPPORTED_GUI_KEYBOARD_VERSION_CODE: Int = 20

        fun isKeyMapperInputMethod(imePackage: String, keyMapperPackageName: String): Boolean {
            return imePackage == keyMapperPackageName ||
                imePackage == KEY_MAPPER_GUI_IME_PACKAGE ||
                imePackage == KEY_MAPPER_LEANBACK_IME_PACKAGE ||
                imePackage == KEY_MAPPER_HACKERS_KEYBOARD_PACKAGE
        }
    }

    private val keyMapperImePackageList = arrayOf(
        packageName,
        KEY_MAPPER_GUI_IME_PACKAGE,
        KEY_MAPPER_LEANBACK_IME_PACKAGE,
        KEY_MAPPER_HACKERS_KEYBOARD_PACKAGE,
    )

    val isCompatibleImeEnabledFlow: Flow<Boolean> =
        imeAdapter.inputMethods
            .map { containsCompatibleIme(it) }

    val isCompatibleImeChosenFlow: Flow<Boolean> =
        imeAdapter.chosenIme
            .map { chosenIme ->
                if (chosenIme == null) {
                    false
                } else {
                    isKeyMapperInputMethod(chosenIme.packageName, packageName)
                }
            }

    fun enableCompatibleInputMethod(): KMResult<Unit> {
        var result: KMResult<Unit>? = null

        for (imePackageName in keyMapperImePackageList) {
            val imeId =
                imeAdapter.getInfoByPackageName(imePackageName).valueOrNull()?.id ?: continue

            result = switchImeInterface.enableIme(imeId)

            // Stop trying to enable IMEs if one is enabled.
            if (result.isSuccess) {
                break
            }
        }

        return result ?: KMError.InputMethodNotFound(packageName)
    }

    fun chooseCompatibleInputMethod(): KMResult<String> =
        getLastUsedCompatibleImeId().then { imeId ->
            switchImeInterface.switchIme(imeId).then { Success(imeId) }
        }

    fun chooseLastUsedIncompatibleInputMethod(): KMResult<String> =
        getLastUsedIncompatibleImeId().then { imeId ->
            switchImeInterface.switchIme(imeId).then { Success(imeId) }
        }

    fun toggleCompatibleInputMethod(): KMResult<String> {
        return if (isCompatibleImeChosen()) {
            chooseLastUsedIncompatibleInputMethod()
        } else {
            chooseCompatibleInputMethod()
        }
    }

    fun isCompatibleImeChosen(): Boolean {
        val chosenIme = imeAdapter.getChosenIme() ?: return false

        return isKeyMapperInputMethod(chosenIme.packageName, packageName)
    }

    fun isCompatibleImeEnabled(): Boolean = imeAdapter.inputMethods
        .map { containsCompatibleIme(it) }
        .firstBlocking()

    private fun containsCompatibleIme(imeList: List<ImeInfo>): Boolean = imeList
        .filter { it.isEnabled }
        .any { it.packageName in keyMapperImePackageList }

    private fun getLastUsedCompatibleImeId(): KMResult<String> {
        for (ime in imeAdapter.inputMethodHistory.firstBlocking()) {
            if (ime.packageName in keyMapperImePackageList && ime.isEnabled) {
                return Success(ime.id)
            }
        }

        imeAdapter.getInfoByPackageName(KEY_MAPPER_GUI_IME_PACKAGE).onSuccess { ime ->
            if (ime.isEnabled) {
                return Success(ime.id)
            }
        }

        return imeAdapter.getInfoByPackageName(packageName).then { ime ->
            if (ime.isEnabled) {
                Success(ime.id)
            } else {
                KMError.NoCompatibleImeEnabled
            }
        }
    }

    private fun getLastUsedIncompatibleImeId(): KMResult<String> {
        for (ime in imeAdapter.inputMethodHistory.firstBlocking()) {
            if (ime.packageName !in keyMapperImePackageList) {
                return Success(ime.id)
            }
        }

        return KMError.NoIncompatibleKeyboardsInstalled
    }
}
