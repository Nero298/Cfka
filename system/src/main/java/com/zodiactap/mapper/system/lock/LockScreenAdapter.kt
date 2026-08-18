package com.zodiactap.mapper.system.lock

import com.zodiactap.mapper.common.utils.KMResult
import kotlinx.coroutines.flow.Flow

interface LockScreenAdapter {
    fun secureLockDevice(): KMResult<*>

    fun isLocked(): Boolean
    fun isLockedFlow(): Flow<Boolean>
    fun isLockScreenShowing(): Boolean
    fun isLockScreenShowingFlow(): Flow<Boolean>
}
