package com.zodiactap.mapper.data.repositories

import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.data.entities.AccessibilityNodeEntity
import kotlinx.coroutines.flow.Flow

interface AccessibilityNodeRepository {
    val nodes: Flow<State<List<AccessibilityNodeEntity>>>
    suspend fun get(id: Long): AccessibilityNodeEntity?
    fun insert(vararg node: AccessibilityNodeEntity)
    suspend fun deleteAll()
}
