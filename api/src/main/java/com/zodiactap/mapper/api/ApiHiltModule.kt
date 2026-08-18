package com.zodiactap.mapper.api

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.zodiactap.mapper.system.apps.KeyMapShortcutActivityIntentBuilder

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiHiltModule {
    @Binds
    abstract fun bindKeyMapShortcutActivityIntentBuilder(
        impl: KeyMapShortcutActivityIntentBuilderImpl,
    ): KeyMapShortcutActivityIntentBuilder
}
