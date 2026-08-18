package com.zodiactap.mapper.system.shizuku

import kotlinx.coroutines.flow.StateFlow

interface ShizukuAdapter {
    val isInstalled: StateFlow<Boolean>
    val isStarted: StateFlow<Boolean>
    fun openShizukuApp()
    fun requestPermission()
}
