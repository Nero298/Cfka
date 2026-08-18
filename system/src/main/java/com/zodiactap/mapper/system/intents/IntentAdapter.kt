package com.zodiactap.mapper.system.intents

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.Success
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntentAdapterImpl @Inject constructor(@ApplicationContext private val context: Context) :
    IntentAdapter {
    private val ctx = context.applicationContext

    override fun send(
        target: IntentTarget,
        uri: String,
        extras: List<IntentExtraModel>,
    ): KMResult<*> {
        val intent = Intent.parseUri(uri, 0)

        extras.forEach { e ->
            e.type.putInIntent(intent, e.name, e.value)
        }

        try {
            when (target) {
                IntentTarget.ACTIVITY -> {
                    if (intent.flags == 0) {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }

                    ctx.startActivity(intent)
                }

                IntentTarget.BROADCAST_RECEIVER -> ctx.sendBroadcast(intent)
                IntentTarget.SERVICE -> ctx.startService(intent)
            }
            return Success(Unit)
        } catch (e: Exception) {
            return KMError.Exception(e)
        }
    }
}

interface IntentAdapter {
    fun send(target: IntentTarget, uri: String, extras: List<IntentExtraModel>): KMResult<*>
}
