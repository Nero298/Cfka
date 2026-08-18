package com.zodiactap.mapper.data.migration

import com.google.gson.JsonObject

class JsonMigration(
    val versionBefore: Int,
    val versionAfter: Int,
    val migrate: (json: JsonObject) -> JsonObject,
)
