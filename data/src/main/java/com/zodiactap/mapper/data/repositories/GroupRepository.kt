package com.zodiactap.mapper.data.repositories

import com.zodiactap.mapper.data.entities.GroupEntity
import com.zodiactap.mapper.data.entities.GroupEntityWithChildren
import com.zodiactap.mapper.data.entities.KeyMapEntitiesWithGroup
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    val groups: Flow<List<GroupEntity>>

    fun getKeyMapsByGroup(groupUid: String): Flow<KeyMapEntitiesWithGroup>
    suspend fun getGroup(uid: String): GroupEntity?
    fun getAllGroups(): Flow<List<GroupEntity>>
    fun getGroups(vararg uid: String): Flow<List<GroupEntity>>
    fun getGroupsByParent(uid: String?): Flow<List<GroupEntity>>
    fun getGroupWithChildren(uid: String): Flow<GroupEntityWithChildren>
    suspend fun insert(groupEntity: GroupEntity)
    suspend fun update(groupEntity: GroupEntity)
    fun delete(uid: String)
    suspend fun setLastOpenedDate(groupUid: String, timestamp: Long)
}
