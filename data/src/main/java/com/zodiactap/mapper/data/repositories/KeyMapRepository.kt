package com.zodiactap.mapper.data.repositories

import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.data.entities.KeyMapEntity
import kotlinx.coroutines.flow.Flow

interface KeyMapRepository {
    val keyMapList: Flow<State<List<KeyMapEntity>>>

    fun getAll(): Flow<List<KeyMapEntity>>
    fun getByGroup(groupUid: String?): Flow<List<KeyMapEntity>>
    fun insert(vararg keyMap: KeyMapEntity)
    fun update(vararg keyMap: KeyMapEntity)
    suspend fun get(uid: String): KeyMapEntity?
    fun delete(vararg uid: String)
    suspend fun deleteAll()
    fun count(): Flow<Int>

    fun duplicate(vararg uid: String)
    fun enableById(vararg uid: String)
    fun disableById(vararg uid: String)
    fun enableByGroup(groupUid: String?)
    fun disableByGroup(groupUid: String?)
    fun toggleById(vararg uid: String)
    fun moveToGroup(groupUid: String?, vararg uid: String)
}
