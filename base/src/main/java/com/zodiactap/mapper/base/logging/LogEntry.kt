package com.zodiactap.mapper.base.logging

data class LogEntry(val id: Int, val time: Long, val severity: LogSeverity, val message: String)
