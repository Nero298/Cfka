package com.zodiactap.mapper.system.popup

interface ToastAdapter {
    fun show(message: String, isLong: Boolean = false)
}
