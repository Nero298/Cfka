package com.zodiactap.mapper.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.zodiactap.mapper.data.db.dao.GroupDao

data class GroupEntityWithChildren(
    @Embedded
    val group: GroupEntity,

    @Relation(
        parentColumn = GroupDao.KEY_UID,
        entityColumn = GroupDao.KEY_PARENT_UID,
    )
    val children: List<GroupEntity>,
)
