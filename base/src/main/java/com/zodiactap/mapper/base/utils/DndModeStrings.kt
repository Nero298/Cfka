package com.zodiactap.mapper.base.utils

import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.system.volume.DndMode

object DndModeStrings {
    fun getLabel(dndMode: DndMode) = when (dndMode) {
        DndMode.ALARMS -> R.string.dnd_mode_alarms
        DndMode.PRIORITY -> R.string.dnd_mode_priority
        DndMode.NONE -> R.string.dnd_mode_none
    }
}
