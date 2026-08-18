package com.zodiactap.mapper.sysbridge.utils

import com.zodiactap.mapper.common.utils.KMError

sealed class SystemBridgeError : KMError() {
    data object Disconnected : SystemBridgeError()
    data object WriteEvdevEventFailed : SystemBridgeError()
}
