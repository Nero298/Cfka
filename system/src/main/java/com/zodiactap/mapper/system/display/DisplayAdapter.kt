package com.zodiactap.mapper.system.display

import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.Orientation
import com.zodiactap.mapper.common.utils.PhysicalOrientation
import com.zodiactap.mapper.common.utils.SizeKM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DisplayAdapter {
    val isScreenOn: Flow<Boolean>
    val orientation: Flow<Orientation>
    val cachedOrientation: Orientation
    val physicalOrientation: Flow<PhysicalOrientation>
    val cachedPhysicalOrientation: PhysicalOrientation
    val size: SizeKM
    val isAmbientDisplayEnabled: Flow<Boolean>

    /**
     * The distinct resolutions supported by the default display, taken from the
     * display's supported modes. The dimensions are in the display's natural orientation.
     */
    val supportedResolutions: StateFlow<Set<SizeKM>>

    fun isAutoRotateEnabled(): Boolean
    fun enableAutoRotate(): KMResult<*>
    fun disableAutoRotate(): KMResult<*>
    fun setOrientation(orientation: Orientation): KMResult<*>

    /**
     * Fetch the orientation and bypass the cached value that updates when the listener changes.
     */
    fun fetchOrientation(): Orientation

    fun isAutoBrightnessEnabled(): Boolean
    fun increaseBrightness(): KMResult<*>
    fun decreaseBrightness(): KMResult<*>
    fun enableAutoBrightness(): KMResult<*>
    fun disableAutoBrightness(): KMResult<*>
}
