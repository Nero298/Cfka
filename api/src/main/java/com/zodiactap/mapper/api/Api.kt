package com.zodiactap.mapper.api

object Api {
    // Do not use the package name for debug/ci builds
    const val ACTION_TRIGGER_KEYMAP_BY_UID =
        "com.zodiactap.mapper.ACTION_TRIGGER_KEYMAP_BY_UID"
    const val EXTRA_KEYMAP_ID = "com.zodiactap.mapper.EXTRA_KEYMAP_UID"

    const val ACTION_PAUSE_MAPPINGS = "com.zodiactap.mapper.ACTION_PAUSE_MAPPINGS"
    const val ACTION_RESUME_MAPPINGS = "com.zodiactap.mapper.ACTION_RESUME_MAPPINGS"
    const val ACTION_TOGGLE_MAPPINGS = "com.zodiactap.mapper.ACTION_TOGGLE_MAPPINGS"

    const val ACTION_ENABLE_KEY_MAP = "com.zodiactap.mapper.ACTION_ENABLE_KEY_MAP"
    const val ACTION_DISABLE_KEY_MAP = "com.zodiactap.mapper.ACTION_DISABLE_KEY_MAP"
    const val ACTION_TOGGLE_KEY_MAP = "com.zodiactap.mapper.ACTION_TOGGLE_KEY_MAP"
}
