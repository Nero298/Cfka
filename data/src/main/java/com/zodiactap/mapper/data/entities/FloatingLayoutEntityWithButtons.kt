package com.zodiactap.mapper.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.zodiactap.mapper.data.db.dao.FloatingButtonDao
import com.zodiactap.mapper.data.db.dao.FloatingLayoutDao

data class FloatingLayoutEntityWithButtons(
    @Embedded
    val layout: FloatingLayoutEntity,

    @Relation(
        parentColumn = FloatingLayoutDao.KEY_UID,
        entityColumn = FloatingButtonDao.KEY_LAYOUT_UID,
    )
    val buttons: List<FloatingButtonEntity>,
)
