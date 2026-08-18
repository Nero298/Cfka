package com.zodiactap.mapper.base.logging

data class LogListItem(
    val id: Int,
    val time: String,
    val severity: LogSeverity,
    val message: String,
)
