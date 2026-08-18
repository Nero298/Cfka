package com.zodiactap.mapper.base.utils

import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.system.volume.VolumeStream

object VolumeStreamStrings {
    fun getLabel(stream: VolumeStream) = when (stream) {
        VolumeStream.ALARM -> R.string.stream_alarm
        VolumeStream.DTMF -> R.string.stream_dtmf
        VolumeStream.MUSIC -> R.string.stream_music
        VolumeStream.NOTIFICATION -> R.string.stream_notification
        VolumeStream.RING -> R.string.stream_ring
        VolumeStream.SYSTEM -> R.string.stream_system
        VolumeStream.VOICE_CALL -> R.string.stream_voice_call
        VolumeStream.ACCESSIBILITY -> R.string.stream_accessibility
    }
}
