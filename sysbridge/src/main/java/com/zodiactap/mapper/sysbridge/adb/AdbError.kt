package com.zodiactap.mapper.sysbridge.adb

import com.zodiactap.mapper.common.utils.KMError

sealed class AdbError : KMError() {
    data object PairingError : AdbError()
    data object ServerNotFound : AdbError()
    data object KeyCreationError : AdbError()
    data object ConnectionError : AdbError()
    data object SslHandshakeError : AdbError()
    data class Unknown(val exception: kotlin.Exception) : AdbError()
}
