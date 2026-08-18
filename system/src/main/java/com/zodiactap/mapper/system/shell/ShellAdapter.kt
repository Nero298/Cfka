package com.zodiactap.mapper.system.shell

import com.zodiactap.mapper.common.models.ShellResult
import com.zodiactap.mapper.common.utils.KMResult
import kotlinx.coroutines.flow.Flow

interface ShellAdapter {
    suspend fun execute(command: String, timeoutMillis: Long = 10000L): KMResult<ShellResult>

    suspend fun executeWithStreamingOutput(
        command: String,
        timeoutMillis: Long,
    ): Flow<KMResult<ShellResult>>
}
