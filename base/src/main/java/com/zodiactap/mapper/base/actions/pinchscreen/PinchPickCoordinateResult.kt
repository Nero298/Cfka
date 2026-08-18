package com.zodiactap.mapper.base.actions.pinchscreen

import com.zodiactap.mapper.common.utils.PinchScreenType
import kotlinx.serialization.Serializable

@Serializable
data class PinchPickCoordinateResult(
    val x: Int,
    val y: Int,
    val distance: Int,
    val pinchType: PinchScreenType,
    val fingerCount: Int,
    val duration: Int,
    val description: String,
)
