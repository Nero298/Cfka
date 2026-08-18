package com.zodiactap.mapper.system.clipboard

interface ClipboardAdapter {
    fun copy(label: String, text: String)
}
