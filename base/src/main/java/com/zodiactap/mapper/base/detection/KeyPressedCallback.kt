package com.zodiactap.mapper.base.detection

interface KeyPressedCallback<T> {
    fun onDownEvent(button: T)
    fun onUpEvent(button: T)
}
