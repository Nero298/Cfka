package com.zodiactap.mapper.data.migration.fingerprintmaps

import com.zodiactap.mapper.common.utils.hasFlag
import com.zodiactap.mapper.data.entities.EntityExtra
import com.zodiactap.mapper.data.entities.FingerprintMapEntity
import com.zodiactap.mapper.data.entities.FingerprintTriggerKeyEntity
import com.zodiactap.mapper.data.entities.KeyMapEntity
import com.zodiactap.mapper.data.entities.TriggerEntity
import com.zodiactap.mapper.data.entities.TriggerKeyEntity

object FingerprintToKeyMapMigration {
    fun migrate(entity: FingerprintMapEntity): KeyMapEntity? {
        if (entity.flags.hasFlag(FingerprintMapEntity.FLAG_MIGRATED_TO_KEY_MAP)) {
            return null
        }

        if (entity.isEnabled && entity.actionList.isEmpty() && entity.constraintList.isEmpty()) {
            return null
        }

        val triggerKey = FingerprintTriggerKeyEntity(
            type = when (entity.id) {
                FingerprintMapEntity.ID_SWIPE_DOWN -> FingerprintTriggerKeyEntity.ID_SWIPE_DOWN
                FingerprintMapEntity.ID_SWIPE_UP -> FingerprintTriggerKeyEntity.ID_SWIPE_UP
                FingerprintMapEntity.ID_SWIPE_LEFT -> FingerprintTriggerKeyEntity.ID_SWIPE_LEFT
                FingerprintMapEntity.ID_SWIPE_RIGHT -> FingerprintTriggerKeyEntity.ID_SWIPE_RIGHT
                else -> FingerprintTriggerKeyEntity.ID_SWIPE_DOWN
            },
            clickType = TriggerKeyEntity.SHORT_PRESS,
        )

        val extras = entity.extras.mapNotNull { extra ->
            when (extra.id) {
                FingerprintMapEntity.EXTRA_VIBRATION_DURATION -> EntityExtra(
                    TriggerEntity.EXTRA_VIBRATION_DURATION,
                    extra.data,
                )

                else -> null
            }
        }

        var flags = 0

        if (entity.flags.hasFlag(FingerprintMapEntity.FLAG_VIBRATE)) {
            flags = flags or TriggerEntity.TRIGGER_FLAG_VIBRATE
        }

        if (entity.flags.hasFlag(FingerprintMapEntity.FLAG_SHOW_TOAST)) {
            flags = flags or TriggerEntity.TRIGGER_FLAG_SHOW_TOAST
        }

        return KeyMapEntity(
            id = 0,
            trigger = TriggerEntity(
                keys = listOf(triggerKey),
                extras = extras,
                mode = TriggerEntity.UNDEFINED,
                flags = flags,
            ),
            actionList = entity.actionList,
            constraintList = entity.constraintList,
            constraintMode = entity.constraintMode,
            isEnabled = entity.isEnabled,
        )
    }
}
