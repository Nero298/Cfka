package com.zodiactap.mapper.system.apps

import kotlinx.serialization.Serializable

@Serializable
data class ActivityInfo(val activityName: String, val packageName: String)
