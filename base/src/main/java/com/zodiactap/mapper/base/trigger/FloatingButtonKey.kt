package com.zodiactap.mapper.base.trigger

import com.zodiactap.mapper.base.floating.FloatingButtonData
import com.zodiactap.mapper.base.floating.FloatingButtonEntityMapper
import com.zodiactap.mapper.base.keymaps.ClickType
import com.zodiactap.mapper.data.entities.FloatingButtonEntityWithLayout
import com.zodiactap.mapper.data.entities.FloatingButtonKeyEntity
import com.zodiactap.mapper.data.entities.TriggerKeyEntity
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class FloatingButtonKey(
    override val uid: String = UUID.randomUUID().toString(),
    val buttonUid: String,
    val button: FloatingButtonData?,
    override val clickType: ClickType,
) : TriggerKey() {

    override val allowedLongPress: Boolean = true
    override val allowedDoublePress: Boolean = true

    override fun compareTo(other: TriggerKey) = when (other) {
        is FloatingButtonKey -> compareValuesBy(
            this,
            other,
            { it.uid },
            { it.clickType },
        )

        else -> super.compareTo(other)
    }

    companion object {
        fun fromEntity(
            entity: FloatingButtonKeyEntity,
            buttonEntity: FloatingButtonEntityWithLayout?,
        ): TriggerKey {
            val clickType = when (entity.clickType) {
                TriggerKeyEntity.SHORT_PRESS -> ClickType.SHORT_PRESS
                TriggerKeyEntity.LONG_PRESS -> ClickType.LONG_PRESS
                TriggerKeyEntity.DOUBLE_PRESS -> ClickType.DOUBLE_PRESS
                else -> ClickType.SHORT_PRESS
            }
            return FloatingButtonKey(
                uid = entity.uid,
                buttonUid = entity.buttonUid,
                button = buttonEntity?.let { buttonEntity ->
                    FloatingButtonEntityMapper.fromEntity(
                        buttonEntity.button,
                        buttonEntity.layout.name,
                    )
                },
                clickType = clickType,
            )
        }

        fun toEntity(key: FloatingButtonKey): FloatingButtonKeyEntity {
            val clickType = when (key.clickType) {
                ClickType.SHORT_PRESS -> TriggerKeyEntity.SHORT_PRESS
                ClickType.LONG_PRESS -> TriggerKeyEntity.LONG_PRESS
                ClickType.DOUBLE_PRESS -> TriggerKeyEntity.DOUBLE_PRESS
            }

            return FloatingButtonKeyEntity(
                uid = key.uid,
                buttonUid = key.buttonUid,
                clickType = clickType,
            )
        }
    }
}
