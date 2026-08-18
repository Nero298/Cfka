package com.zodiactap.mapper.system.camera

data class CameraFlashInfo(
    val supportsVariableStrength: Boolean,
    val defaultStrength: Int,
    val maxStrength: Int,
)
