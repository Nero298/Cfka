package com.zodiactap.mapper.common

interface KeyMapperClassProvider {
    fun getMainActivity(): Class<*>
    fun getAccessibilityService(): Class<*>
}
