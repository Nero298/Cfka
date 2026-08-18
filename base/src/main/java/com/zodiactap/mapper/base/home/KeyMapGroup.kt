package com.zodiactap.mapper.base.home

import com.zodiactap.mapper.base.groups.Group
import com.zodiactap.mapper.base.keymaps.KeyMap
import com.zodiactap.mapper.common.utils.State

data class KeyMapGroup(
    val group: Group?,
    val subGroups: List<Group>,
    val parents: List<Group>,
    val keyMaps: State<List<KeyMap>>,
)
