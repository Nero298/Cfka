package com.zodiactap.mapper.base.keymaps

import com.zodiactap.mapper.base.utils.ui.compose.ComposeIconInfo

data class ShortcutModel<T>(val icon: ComposeIconInfo, val text: String, val data: T)
