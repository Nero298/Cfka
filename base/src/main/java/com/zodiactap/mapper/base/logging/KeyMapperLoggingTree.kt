package com.zodiactap.mapper.base.logging

import android.util.Log
import com.zodiactap.mapper.base.BuildConfig
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.entities.LogEntryEntity
import com.zodiactap.mapper.data.repositories.LogRepository
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class KeyMapperLoggingTree @Inject constructor(
    private val coroutineScope: CoroutineScope,
    preferenceRepository: PreferenceRepository,
    private val logRepository: LogRepository,
) : Timber.Tree() {
    private val logEverything: StateFlow<Boolean> = preferenceRepository.get(Keys.log)
        .map { it ?: false }
        .stateIn(coroutineScope, SharingStarted.Eagerly, false)

    private val messagesToLog = MutableSharedFlow<LogEntryEntity>(
        extraBufferCapacity = 1000,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    init {
        messagesToLog
            .onEach {
                logRepository.insertSuspend(it)
            }
            .flowOn(Dispatchers.Default)
            .launchIn(coroutineScope)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // error, warn, and info logs should always log even if the user setting is turned off
        if (!logEverything.value &&
            priority != Log.ERROR &&
            priority != Log.WARN &&
            priority != Log.INFO
        ) {
            return
        }

        // Log to logcat if extra logging is enabled. If it is a debug build then a Timber
        // DebugTree is planted in BaseKeyMapperApp so do not duplicate the log.
        if (logEverything.value && !BuildConfig.DEBUG) {
            Log.println(priority, tag, message)
        }

        val severity = when (priority) {
            Log.ERROR -> LogEntryEntity.SEVERITY_ERROR
            Log.DEBUG -> LogEntryEntity.SEVERITY_DEBUG
            Log.INFO -> LogEntryEntity.SEVERITY_INFO
            else -> LogEntryEntity.SEVERITY_DEBUG
        }

        messagesToLog.tryEmit(
            LogEntryEntity(
                id = 0,
                time = Calendar.getInstance().timeInMillis,
                severity = severity,
                message = message,
            ),
        )
    }
}
