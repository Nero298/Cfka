package com.zodiactap.mapper.data.repositories

import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.data.entities.FloatingButtonEntity
import com.zodiactap.mapper.data.entities.FloatingButtonEntityWithLayout
import kotlinx.coroutines.flow.Flow

interface FloatingButtonRepository {
    val buttonsList: Flow<State<List<FloatingButtonEntityWithLayout>>>

    fun insert(vararg button: FloatingButtonEntity)
    fun update(button: FloatingButtonEntity)
    suspend fun get(uid: String): FloatingButtonEntityWithLayout?
    fun delete(vararg uid: String)
}
