package com.zodiactap.mapper.base.system.inputmethod

import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.Success
import com.zodiactap.mapper.system.inputmethod.ImeInfo
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import kotlinx.coroutines.flow.MutableStateFlow

class FakeInputMethodAdapter : InputMethodAdapter {

    override val inputMethodHistory = MutableStateFlow<List<ImeInfo>>(emptyList())

    override val inputMethods = MutableStateFlow<List<ImeInfo>>(emptyList())

    override val chosenIme = MutableStateFlow<ImeInfo?>(null)

    override fun getChosenIme(): ImeInfo? {
        return chosenIme.value
    }

    override fun showImePicker(fromForeground: Boolean): KMResult<*> {
        return Success(Unit)
    }

    override fun getInfoById(imeId: String): KMResult<ImeInfo> {
        return inputMethods.value
            .firstOrNull { it.id == imeId }
            ?.let { Success(it) }
            ?: KMError.InputMethodNotFound(imeId)
    }

    override fun getInfoByPackageName(packageName: String): KMResult<ImeInfo> {
        return inputMethods.value
            .firstOrNull { it.packageName == packageName }
            ?.let { Success(it) }
            ?: KMError.InputMethodNotFound(packageName)
    }
}
