package com.zodiactap.mapper.api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.qualifiers.ApplicationContext
import com.zodiactap.mapper.system.apps.KeyMapShortcutActivityIntentBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyMapShortcutActivityIntentBuilderImpl @Inject constructor(
    @ApplicationContext private val ctx: Context,
) : KeyMapShortcutActivityIntentBuilder {
    override fun build(intentAction: String, intentExtras: Bundle): Intent =
        Intent(ctx, LaunchKeyMapShortcutActivity::class.java).apply {
            action = intentAction

            putExtras(intentExtras)
        }
}
