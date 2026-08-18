package com.zodiactap.mapper.data.repositories

import com.zodiactap.mapper.data.entities.LogEntryEntity
import kotlinx.coroutines.flow.Flow

interface LogRepository {
    val log: Flow<List<LogEntryEntity>>
    fun insert(entry: LogEntryEntity)
    suspend fun insertSuspend(entry: LogEntryEntity)
    fun deleteAll()
}
