package com.zodiactap.mapper.base.utils

import android.os.Build
import com.zodiactap.mapper.base.BuildConfig
import com.zodiactap.mapper.common.BuildConfigProvider

class TestBuildConfigProvider(override var sdkInt: Int) : BuildConfigProvider {
    override val minApi: Int = Build.VERSION_CODES.O
    override val maxApi: Int = 1000
    override val packageName: String = BuildConfig.LIBRARY_PACKAGE_NAME
    override val version: String = "1.0.0"
    override val versionCode: Int = 1
}
