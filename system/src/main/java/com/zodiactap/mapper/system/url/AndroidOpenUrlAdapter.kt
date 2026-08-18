package com.zodiactap.mapper.system.url

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.zodiactap.mapper.common.utils.KMResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidOpenUrlAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    OpenUrlAdapter {

    private val ctx = context.applicationContext

    override fun openUrl(url: String): KMResult<*> = UrlUtils.openUrl(ctx, url)
}
