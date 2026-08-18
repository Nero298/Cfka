package com.zodiactap.mapper.data.repositories

import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.data.entities.FloatingLayoutEntity
import com.zodiactap.mapper.data.entities.FloatingLayoutEntityWithButtons
import kotlinx.coroutines.flow.Flow

interface FloatingLayoutRepository {
    val layouts: Flow<State<List<FloatingLayoutEntityWithButtons>>>
    suspend fun insert(vararg layout: FloatingLayoutEntity)

    /**
     * @return whether the update happened successfully. It can be false if some constraints
     * failed.
     */
    suspend fun update(vararg layout: FloatingLayoutEntity): Boolean
    fun get(uid: String): Flow<FloatingLayoutEntityWithButtons?>
    fun delete(vararg uid: String)
    suspend fun count(): Int
}
