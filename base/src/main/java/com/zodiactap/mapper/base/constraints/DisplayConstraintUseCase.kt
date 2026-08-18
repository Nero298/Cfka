package com.zodiactap.mapper.base.constraints

import android.graphics.drawable.Drawable
import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult

interface DisplayConstraintUseCase : GetConstraintErrorUseCase {
    fun getAppName(packageName: String): KMResult<String>
    fun getAppIcon(packageName: String): KMResult<Drawable>
    fun getInputMethodLabel(imeId: String): KMResult<String>
    fun neverShowDndTriggerError()
    suspend fun fixError(error: KMError)
}
