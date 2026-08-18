package com.zodiactap.mapper.common.utils

sealed class AccessibilityServiceError : KMError() {
    data object Disabled : AccessibilityServiceError()
    data object Crashed : AccessibilityServiceError()
}
