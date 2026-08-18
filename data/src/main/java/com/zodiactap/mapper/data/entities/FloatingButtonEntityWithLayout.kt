package com.zodiactap.mapper.data.entities

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.zodiactap.mapper.data.db.dao.FloatingButtonDao
import com.zodiactap.mapper.data.db.dao.FloatingLayoutDao
import kotlinx.parcelize.Parcelize

@Parcelize
data class FloatingButtonEntityWithLayout(
    @Embedded val button: FloatingButtonEntity,

    @Relation(
        parentColumn = FloatingButtonDao.KEY_LAYOUT_UID,
        entityColumn = FloatingLayoutDao.KEY_UID,
    )
    val layout: FloatingLayoutEntity,
) : Parcelable
