package com.zodiactap.mapper.base.utils.ui

sealed class TextListItem : ListItem {
    data class Success(override val id: String, val text: String) : TextListItem()
    data class Error(
        override val id: String,
        val text: String,
        val customButtonText: String? = null,
    ) : TextListItem()
}
