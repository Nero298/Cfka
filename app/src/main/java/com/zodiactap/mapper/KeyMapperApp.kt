package com.zodiactap.mapper

import android.annotation.SuppressLint
import dagger.hilt.android.HiltAndroidApp
import com.zodiactap.mapper.base.BaseKeyMapperApp

@SuppressLint("LogNotTimber")
@HiltAndroidApp
class KeyMapperApp : BaseKeyMapperApp() {
    override fun getMainActivityClass(): Class<*> {
        return MainActivity::class.java
    }
}
