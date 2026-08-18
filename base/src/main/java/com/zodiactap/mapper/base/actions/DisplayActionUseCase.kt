package com.zodiactap.mapper.base.actions

import android.graphics.drawable.Drawable
import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult
import kotlinx.coroutines.flow.Flow

interface DisplayActionUseCase : GetActionErrorUseCase {
    val showDeviceDescriptors: Flow<Boolean>
    fun getAppName(packageName: String): KMResult<String>
    fun getAppIcon(packageName: String): KMResult<Drawable>
    fun getInputMethodLabel(imeId: String): KMResult<String>
    fun getRingtoneLabel(uri: String): KMResult<String>
    suspend fun fixError(error: KMError)
    fun neverShowDndTriggerError()
    fun startAccessibilityService(): Boolean
    fun restartAccessibilityService(): Boolean
}
