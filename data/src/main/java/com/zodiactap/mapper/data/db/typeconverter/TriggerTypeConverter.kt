package com.zodiactap.mapper.data.db.typeconverter

import androidx.room.TypeConverter
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import com.zodiactap.mapper.data.entities.EntityExtra
import com.zodiactap.mapper.data.entities.TriggerEntity
import com.zodiactap.mapper.data.entities.TriggerKeyEntity

class TriggerTypeConverter {
    private val gson = GsonBuilder()
        .registerTypeAdapter(TriggerEntity.DESERIALIZER)
        .registerTypeAdapter(TriggerKeyEntity.SERIALIZER)
        .registerTypeAdapter(TriggerKeyEntity.DESERIALIZER)
        .registerTypeAdapter(EntityExtra.DESERIALIZER).create()

    @TypeConverter
    fun toTrigger(json: String): TriggerEntity {
        return gson.fromJson(json)
    }

    @TypeConverter
    fun toJsonString(trigger: TriggerEntity) = gson.toJson(trigger)!!
}
