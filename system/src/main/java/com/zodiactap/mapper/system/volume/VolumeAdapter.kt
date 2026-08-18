package com.zodiactap.mapper.system.volume

import com.zodiactap.mapper.common.utils.KMResult
import kotlinx.coroutines.flow.Flow

interface VolumeAdapter {
    val ringerMode: RingerMode
    val ringerModeFlow: Flow<RingerMode>

    fun raiseVolume(stream: VolumeStream? = null, showVolumeUi: Boolean): KMResult<*>
    fun lowerVolume(stream: VolumeStream? = null, showVolumeUi: Boolean): KMResult<*>
    fun muteVolume(stream: VolumeStream? = null, showVolumeUi: Boolean): KMResult<*>
    fun unmuteVolume(stream: VolumeStream? = null, showVolumeUi: Boolean): KMResult<*>
    fun toggleMuteVolume(stream: VolumeStream? = null, showVolumeUi: Boolean): KMResult<*>
    fun showVolumeUi(): KMResult<*>
    fun setRingerMode(mode: RingerMode): KMResult<*>

    val isMicrophoneMuted: Boolean
    fun muteMicrophone(): KMResult<*>
    fun unmuteMicrophone(): KMResult<*>

    fun isDndEnabled(): Boolean
    fun enableDndMode(dndMode: DndMode): KMResult<*>
    fun disableDndMode(): KMResult<*>
}
