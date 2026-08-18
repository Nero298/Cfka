package com.zodiactap.mapper.base.home

import com.zodiactap.mapper.base.trigger.KeyMapListItemModel
import com.zodiactap.mapper.common.utils.State

data class KeyMapListState(
    val appBarState: KeyMapAppBarState,
    val listItems: State<List<KeyMapListItemModel>>,
    val showCreateKeyMapTapTarget: Boolean,
)
