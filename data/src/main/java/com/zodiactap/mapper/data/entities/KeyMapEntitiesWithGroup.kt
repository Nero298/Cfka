package com.zodiactap.mapper.data.entities

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.zodiactap.mapper.data.db.dao.GroupDao
import com.zodiactap.mapper.data.db.dao.KeyMapDao
import kotlinx.parcelize.Parcelize

@Parcelize
data class KeyMapEntitiesWithGroup(
    @Embedded
    val group: GroupEntity,

    @Relation(
        parentColumn = GroupDao.KEY_UID,
        entityColumn = KeyMapDao.KEY_GROUP_UID,
    )
    val keyMaps: List<KeyMapEntity>,
) : Parcelable
