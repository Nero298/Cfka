package com.zodiactap.mapper.base.floating

import com.zodiactap.mapper.data.entities.FloatingLayoutEntity
import com.zodiactap.mapper.data.entities.FloatingLayoutEntityWithButtons
import java.util.UUID

data class FloatingLayoutData(
    val uid: String = UUID.randomUUID().toString(),
    val name: String,
    val buttons: List<FloatingButtonData> = emptyList(),
)

object FloatingLayoutEntityMapper {
    fun fromEntity(entity: FloatingLayoutEntityWithButtons): FloatingLayoutData {
        return FloatingLayoutData(
            uid = entity.layout.uid,
            name = entity.layout.name,
            buttons = entity.buttons.map { buttonEntity ->
                FloatingButtonEntityMapper.fromEntity(buttonEntity, entity.layout.name)
            },
        )
    }

    fun toEntity(layout: FloatingLayoutData): FloatingLayoutEntity {
        return FloatingLayoutEntity(
            uid = layout.uid,
            name = layout.name,
        )
    }
}
