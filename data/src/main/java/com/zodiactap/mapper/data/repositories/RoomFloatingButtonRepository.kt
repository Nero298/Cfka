package com.zodiactap.mapper.data.repositories

import com.zodiactap.mapper.common.utils.DefaultDispatcherProvider
import com.zodiactap.mapper.common.utils.DispatcherProvider
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.data.db.dao.FloatingButtonDao
import com.zodiactap.mapper.data.entities.FloatingButtonEntity
import com.zodiactap.mapper.data.entities.FloatingButtonEntityWithLayout
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Singleton
class RoomFloatingButtonRepository @Inject constructor(
    private val dao: FloatingButtonDao,
    private val coroutineScope: CoroutineScope,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider(),
) : FloatingButtonRepository {
    override val buttonsList: StateFlow<State<List<FloatingButtonEntityWithLayout>>> = dao.getAll()
        .map { State.Data(it) }
        .stateIn(coroutineScope, SharingStarted.Eagerly, State.Loading)

    override fun insert(vararg button: FloatingButtonEntity) {
        coroutineScope.launch(dispatchers.default()) {
            dao.insert(*button)
        }
    }

    override fun update(button: FloatingButtonEntity) {
        coroutineScope.launch(dispatchers.default()) {
            dao.update(button)
        }
    }

    override suspend fun get(uid: String): FloatingButtonEntityWithLayout? =
        dao.getByUidWithLayout(uid)

    override fun delete(vararg uid: String) {
        coroutineScope.launch(dispatchers.default()) {
            dao.deleteByUid(*uid)
        }
    }
}
