package com.zodiactap.mapper.base.groups

import com.zodiactap.mapper.base.constraints.ConstraintEntityMapper
import com.zodiactap.mapper.base.constraints.ConstraintModeEntityMapper
import com.zodiactap.mapper.base.constraints.ConstraintState
import com.zodiactap.mapper.data.entities.GroupEntity

data class Group(
    val uid: String,
    val name: String,
    val constraintState: ConstraintState,
    val parentUid: String?,
    val lastOpenedDate: Long,
)

object GroupEntityMapper {
    fun fromEntity(entity: GroupEntity): Group {
        val constraintList =
            entity.constraintList.map { ConstraintEntityMapper.fromEntity(it) }.toSet()

        val constraintMode = ConstraintModeEntityMapper.fromEntity(entity.constraintMode)

        return Group(
            uid = entity.uid,
            name = entity.name,
            constraintState = ConstraintState(constraintList, constraintMode),
            parentUid = entity.parentUid,
            lastOpenedDate = entity.lastOpenedDate ?: System.currentTimeMillis(),
        )
    }

    fun toEntity(group: Group): GroupEntity {
        return GroupEntity(
            uid = group.uid,
            name = group.name,
            constraintList = group.constraintState.constraints.map {
                ConstraintEntityMapper.toEntity(it)
            },
            constraintMode = ConstraintModeEntityMapper.toEntity(group.constraintState.mode),
            parentUid = group.parentUid,
            lastOpenedDate = group.lastOpenedDate,
        )
    }
}
