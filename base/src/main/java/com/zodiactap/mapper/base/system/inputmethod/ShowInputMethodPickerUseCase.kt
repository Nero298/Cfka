package com.zodiactap.mapper.base.system.inputmethod

import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.PreferenceDefaults
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShowInputMethodPickerUseCaseImpl @Inject constructor(
    private val inputMethodAdapter: InputMethodAdapter,
    private val preferenceRepository: PreferenceRepository,
) : ShowInputMethodPickerUseCase {
    override val isAutoSwitchImeEnabled: Flow<Boolean> =
        preferenceRepository.get(Keys.changeImeOnInputFocus)
            .map { it ?: PreferenceDefaults.CHANGE_IME_ON_INPUT_FOCUS }

    override fun disableAutoSwitch() {
        preferenceRepository.set(Keys.changeImeOnInputFocus, false)
    }

    override fun show(fromForeground: Boolean) {
        inputMethodAdapter.showImePicker(fromForeground = fromForeground)
    }
}

interface ShowInputMethodPickerUseCase {
    val isAutoSwitchImeEnabled: Flow<Boolean>

    fun disableAutoSwitch()
    fun show(fromForeground: Boolean)
}
