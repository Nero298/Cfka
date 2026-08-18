package com.zodiactap.mapper.system.apps

import android.content.Intent
import android.os.Bundle

interface KeyMapShortcutActivityIntentBuilder {
    fun build(intentAction: String, intentExtras: Bundle): Intent
}
