package com.zodiactap.mapper.base.utils

import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.system.volume.RingerMode

object RingerModeStrings {
    fun getLabel(ringerMode: RingerMode) = when (ringerMode) {
        RingerMode.NORMAL -> R.string.ringer_mode_normal
        RingerMode.VIBRATE -> R.string.ringer_mode_vibrate
        RingerMode.SILENT -> R.string.ringer_mode_silent
    }
}
