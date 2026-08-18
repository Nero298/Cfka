package com.zodiactap.mapper.system.nfc

import com.zodiactap.mapper.common.utils.KMResult

interface NfcAdapter {
    fun isEnabled(): Boolean
    fun enable(): KMResult<*>
    fun disable(): KMResult<*>
}
