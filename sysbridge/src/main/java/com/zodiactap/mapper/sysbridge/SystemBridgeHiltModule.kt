package com.zodiactap.mapper.sysbridge

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.zodiactap.mapper.sysbridge.adb.AdbManager
import com.zodiactap.mapper.sysbridge.adb.AdbManagerImpl
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManager
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManagerImpl
import com.zodiactap.mapper.sysbridge.service.SystemBridgeSetupController
import com.zodiactap.mapper.sysbridge.service.SystemBridgeSetupControllerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemBridgeHiltModule {

    @Singleton
    @Binds
    abstract fun bindSystemBridgeSetupController(
        impl: SystemBridgeSetupControllerImpl,
    ): SystemBridgeSetupController

    @Singleton
    @Binds
    abstract fun bindSystemBridgeManager(
        impl: SystemBridgeConnectionManagerImpl,
    ): SystemBridgeConnectionManager

    @Singleton
    @Binds
    abstract fun bindAdbManager(impl: AdbManagerImpl): AdbManager
}
