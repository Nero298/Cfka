package com.zodiactap.mapper.data.repositories

import com.zodiactap.mapper.common.utils.DefaultDispatcherProvider
import com.zodiactap.mapper.common.utils.DispatcherProvider
import com.zodiactap.mapper.data.db.dao.GroupDao
import com.zodiactap.mapper.data.entities.GroupEntity
import com.zodiactap.mapper.data.entities.GroupEntityWithChildren
import com.zodiactap.mapper.data.entities.KeyMapEntitiesWithGroup
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Singleton
class RoomGroupRepository @Inject constructor(
    private val dao: GroupDao,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider(),
) : GroupRepository {

    override val groups: StateFlow<List<GroupEntity>> =
        dao.getAll().stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun getKeyMapsByGroup(groupUid: String): Flow<KeyMapEntitiesWithGroup> {
        return dao.getKeyMapsByGroup(groupUid).flowOn(dispatchers.io())
    }

    override suspend fun getGroup(uid: String): GroupEntity? {
        return withContext(dispatchers.io()) { dao.getById(uid) }
    }

    override fun getAllGroups(): Flow<List<GroupEntity>> {
        return dao.getAll().flowOn(dispatchers.io())
    }

    override fun getGroups(vararg uid: String): Flow<List<GroupEntity>> {
        return dao.getManyByIdFlow(*uid).flowOn(dispatchers.io())
    }

    override fun getGroupsByParent(uid: String?): Flow<List<GroupEntity>> {
        return dao.getGroupsByParent(uid).flowOn(dispatchers.io())
    }

    override fun getGroupWithChildren(uid: String): Flow<GroupEntityWithChildren> {
        return dao.getGroupWithSubGroups(uid).flowOn(dispatchers.io())
    }

    override suspend fun insert(groupEntity: GroupEntity) {
        withContext(dispatchers.io()) {
            dao.insert(groupEntity)
        }
    }

    override suspend fun update(groupEntity: GroupEntity) {
        withContext(dispatchers.io()) {
            dao.update(groupEntity)
        }
    }

    override fun delete(uid: String) {
        coroutineScope.launch {
            withContext(dispatchers.io()) {
                dao.deleteByUid(uid)
            }
        }
    }

    override suspend fun setLastOpenedDate(groupUid: String, timestamp: Long) {
        withContext(dispatchers.io()) {
            dao.setLastOpenedDate(groupUid, timestamp)
        }
    }
}
