package com.zodiactap.mapper.data.utils

import androidx.datastore.preferences.core.Preferences
import com.zodiactap.mapper.common.utils.firstBlocking
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlowPrefDelegate<T>(private val key: Preferences.Key<T>, private val defaultValue: T) {

    operator fun getValue(thisRef: PreferenceRepository, prop: KProperty<*>?): Flow<T> =
        thisRef.get(key).map {
            it
                ?: defaultValue
        }
}

class PrefDelegate<T>(private val key: Preferences.Key<T>, private val defaultValue: T) {

    operator fun getValue(thisRef: PreferenceRepository, prop: KProperty<*>?): T =
        thisRef.get(key).map { it ?: defaultValue }.firstBlocking()

    operator fun setValue(thisRef: PreferenceRepository, prop: KProperty<*>?, value: T?) {
        thisRef.set(key, value)
    }
}
