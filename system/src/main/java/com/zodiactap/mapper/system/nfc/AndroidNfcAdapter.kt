package com.zodiactap.mapper.system.nfc

import android.content.Context
import android.nfc.NfcManager
import android.os.Build
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManager
import com.zodiactap.mapper.system.root.SuAdapter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking

@Singleton
class AndroidNfcAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val suAdapter: SuAdapter,
    private val systemBridgeConnectionManager: SystemBridgeConnectionManager,
) : NfcAdapter {
    private val ctx = context.applicationContext

    private val nfcManager: NfcManager? by lazy { ctx.getSystemService() }

    override fun isEnabled(): Boolean = nfcManager?.defaultAdapter?.isEnabled ?: false

    override fun enable(): KMResult<*> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return systemBridgeConnectionManager.run { bridge -> bridge.setNfcEnabled(true) }
        } else {
            return runBlocking { suAdapter.execute("svc nfc enable") }
        }
    }

    override fun disable(): KMResult<*> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return systemBridgeConnectionManager.run { bridge -> bridge.setNfcEnabled(false) }
        } else {
            return runBlocking { suAdapter.execute("svc nfc disable") }
        }
    }
}
