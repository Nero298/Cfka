package com.zodiactap.mapper.base.keymaps

import android.view.KeyEvent
import com.zodiactap.mapper.base.actions.Action
import com.zodiactap.mapper.base.actions.ActionData
import com.zodiactap.mapper.base.actions.ActionEntityMapper
import com.zodiactap.mapper.base.actions.canBeHeldDown
import com.zodiactap.mapper.base.constraints.ConstraintEntityMapper
import com.zodiactap.mapper.base.constraints.ConstraintModeEntityMapper
import com.zodiactap.mapper.base.constraints.ConstraintState
import com.zodiactap.mapper.base.detection.KeyMapAlgorithm
import com.zodiactap.mapper.base.trigger.Trigger
import com.zodiactap.mapper.base.trigger.TriggerEntityMapper
import com.zodiactap.mapper.base.trigger.TriggerKey
import com.zodiactap.mapper.data.entities.FloatingButtonEntityWithLayout
import com.zodiactap.mapper.data.entities.KeyMapEntity
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class KeyMap(
    val dbId: Long? = null,
    val uid: String = UUID.randomUUID().toString(),
    val trigger: Trigger = Trigger(),
    val actionList: List<Action> = emptyList(),
    val constraintState: ConstraintState = ConstraintState(),
    val isEnabled: Boolean = true,
    val groupUid: String? = null,
) {

    val showToast: Boolean
        get() = trigger.showToast

    val vibrate: Boolean
        get() = trigger.vibrate

    val vibrateDuration: Int?
        get() = trigger.vibrateDuration

    fun isRepeatingActionsAllowed(): Boolean = KeyMapAlgorithm.performActionOnDown(trigger)

    fun isChangingActionRepeatRateAllowed(action: Action): Boolean =
        action.repeat && isRepeatingActionsAllowed()

    fun isChangingActionRepeatDelayAllowed(action: Action): Boolean =
        action.repeat && isRepeatingActionsAllowed()

    fun isHoldingDownActionAllowed(action: Action): Boolean =
        KeyMapAlgorithm.performActionOnDown(trigger) && action.data.canBeHeldDown()

    fun isHoldingDownActionBeforeRepeatingAllowed(action: Action): Boolean =
        action.repeat && action.holdDown && isHoldingDownActionAllowed(action)

    fun isChangingRepeatModeAllowed(action: Action): Boolean =
        action.repeat && isRepeatingActionsAllowed()

    fun isChangingRepeatLimitAllowed(action: Action): Boolean =
        action.repeat && isRepeatingActionsAllowed()

    fun isStopHoldingDownActionWhenTriggerPressedAgainAllowed(action: Action): Boolean =
        action.holdDown && !action.repeat && isHoldingDownActionAllowed(action)

    fun isDelayBeforeNextActionAllowed(): Boolean = actionList.isNotEmpty()
}

/**
 * Whether this key map requires an input method to detect the key events.
 * If the key map needs to answer or end a call then it must use an input method to detect
 * the key events because volume key events are not sent to accessibility services when a call
 * is incoming.
 */
fun KeyMap.requiresImeKeyEventForwarding(): Boolean {
    val hasPhoneCallAction =
        actionList.any { it.data is ActionData.AnswerCall || it.data is ActionData.EndCall }

    val hasVolumeKeys = trigger.keys
        .mapNotNull { it as? com.zodiactap.mapper.base.trigger.KeyEventTriggerKey }
        .any {
            it.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                it.keyCode == KeyEvent.KEYCODE_VOLUME_UP
        }

    return hasVolumeKeys && hasPhoneCallAction
}

/**
 * Whether this trigger key requires an input method to detect the key events.
 * If the key map needs to answer or end a call then it must use an input method to detect
 * the key events because volume key events are not sent to accessibility services when a call
 * is incoming.
 */
fun KeyMap.requiresImeKeyEventForwardingInPhoneCall(triggerKey: TriggerKey): Boolean {
    if (triggerKey !is com.zodiactap.mapper.base.trigger.KeyEventTriggerKey) {
        return false
    }

    val hasPhoneCallAction =
        actionList.any { it.data is ActionData.AnswerCall || it.data is ActionData.EndCall }

    val hasVolumeKeys = trigger.keys
        .mapNotNull { it as? com.zodiactap.mapper.base.trigger.KeyEventTriggerKey }
        .any {
            it.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                it.keyCode == KeyEvent.KEYCODE_VOLUME_UP
        }

    return hasVolumeKeys && hasPhoneCallAction
}

object KeyMapEntityMapper {
    fun fromEntity(
        entity: KeyMapEntity,
        floatingButtons: List<FloatingButtonEntityWithLayout>,
    ): KeyMap {
        val actionList = entity.actionList
            .filterNotNull()
            .mapNotNull { ActionEntityMapper.fromEntity(it) }

        val constraintList =
            entity.constraintList.map { ConstraintEntityMapper.fromEntity(it) }.toSet()

        val constraintMode = ConstraintModeEntityMapper.fromEntity(entity.constraintMode)

        return KeyMap(
            dbId = entity.id,
            uid = entity.uid,
            trigger = TriggerEntityMapper.fromEntity(entity.trigger, floatingButtons),
            actionList = actionList,
            constraintState = ConstraintState(constraintList, constraintMode),
            isEnabled = entity.isEnabled,
            groupUid = entity.groupUid,
        )
    }

    fun toEntity(keyMap: KeyMap, dbId: Long): KeyMapEntity {
        val actionEntityList = ActionEntityMapper.toEntity(keyMap)

        return KeyMapEntity(
            id = dbId,
            trigger = TriggerEntityMapper.toEntity(keyMap.trigger),
            actionList = actionEntityList,
            constraintList = keyMap.constraintState.constraints.map {
                ConstraintEntityMapper.toEntity(
                    it,
                )
            },
            constraintMode = ConstraintModeEntityMapper.toEntity(keyMap.constraintState.mode),
            isEnabled = keyMap.isEnabled,
            uid = keyMap.uid,
            groupUid = keyMap.groupUid,
        )
    }
}
