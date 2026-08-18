package com.zodiactap.mapper

import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.zodiactap.mapper.base.purchasing.PurchasingManager
import com.zodiactap.mapper.common.BuildConfigProvider
import com.zodiactap.mapper.common.KeyMapperClassProvider
import com.zodiactap.mapper.common.utils.DefaultDispatcherProvider
import com.zodiactap.mapper.common.utils.DispatcherProvider
import com.zodiactap.mapper.purchasing.PurchasingManagerImpl
import com.zodiactap.mapper.system.accessibility.MyAccessibilityService
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@Module
@InstallIn(SingletonComponent::class)
class AppHiltModule {
    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope = MainScope()

    @Provides
    @Singleton
    fun provideDispatchers(): DispatcherProvider = DefaultDispatcherProvider()

    @Singleton
    @Provides
    fun provideBuildConfigProvider(): BuildConfigProvider = object : BuildConfigProvider {
        override val minApi: Int
            get() = Build.VERSION_CODES.O
        override val maxApi: Int
            get() = 1000
        override val packageName: String
            get() = BuildConfig.APPLICATION_ID
        override val version: String
            get() = BuildConfig.VERSION_NAME
        override val versionCode: Int
            get() = BuildConfig.VERSION_CODE
        override val sdkInt: Int
            get() = Build.VERSION.SDK_INT
    }

    @Singleton
    @Provides
    fun provideClassProvider(): KeyMapperClassProvider = object : KeyMapperClassProvider {
        override fun getMainActivity(): Class<*> {
            return MainActivity::class.java
        }

        override fun getAccessibilityService(): Class<*> {
            return MyAccessibilityService::class.java
        }
    }

    @Provides
    @Singleton
    fun providePurchasingManager(): PurchasingManager = PurchasingManagerImpl()
}
