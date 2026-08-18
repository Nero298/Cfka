package com.zodiactap.mapper.base.detection

import com.zodiactap.mapper.base.constraints.ConstraintState
import com.zodiactap.mapper.base.keymaps.KeyMap

data class DetectKeyMapModel(
    val keyMap: KeyMap,
    val groupConstraintStates: List<ConstraintState> = emptyList(),
)
