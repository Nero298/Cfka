package com.zodiactap.mapper.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.zodiactap.mapper.data.repositories.AccessibilityNodeRepository
import com.zodiactap.mapper.data.repositories.FloatingButtonRepository
import com.zodiactap.mapper.data.repositories.FloatingLayoutRepository
import com.zodiactap.mapper.data.repositories.GroupRepository
import com.zodiactap.mapper.data.repositories.KeyMapRepository
import com.zodiactap.mapper.data.repositories.LogRepository
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.data.repositories.PreferenceRepositoryImpl
import com.zodiactap.mapper.data.repositories.RoomAccessibilityNodeRepository
import com.zodiactap.mapper.data.repositories.RoomFloatingButtonRepository
import com.zodiactap.mapper.data.repositories.RoomFloatingLayoutRepository
import com.zodiactap.mapper.data.repositories.RoomGroupRepository
import com.zodiactap.mapper.data.repositories.RoomKeyMapRepository
import com.zodiactap.mapper.data.repositories.RoomLogRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataHiltModule {
    @Singleton
    @Binds
    abstract fun providePreferenceRepository(impl: PreferenceRepositoryImpl): PreferenceRepository

    @Singleton
    @Binds
    abstract fun provideGroupRepository(impl: RoomGroupRepository): GroupRepository

    @Singleton
    @Binds
    abstract fun provideKeyMapRepository(impl: RoomKeyMapRepository): KeyMapRepository

    @Singleton
    @Binds
    abstract fun provideAccessibilityNodeRepository(
        impl: RoomAccessibilityNodeRepository,
    ): AccessibilityNodeRepository

    @Singleton
    @Binds
    abstract fun provideLogRepository(impl: RoomLogRepository): LogRepository

    @Singleton
    @Binds
    abstract fun provideFloatingButtonRepository(
        impl: RoomFloatingButtonRepository,
    ): FloatingButtonRepository

    @Singleton
    @Binds
    abstract fun provideFloatingLayoutRepository(
        impl: RoomFloatingLayoutRepository,
    ): FloatingLayoutRepository
}
