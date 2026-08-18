package com.zodiactap.mapper.base.detection

import com.zodiactap.mapper.base.actions.Action
import com.zodiactap.mapper.base.actions.PerformActionTriggerDevice
import com.zodiactap.mapper.base.actions.PerformActionsUseCase
import com.zodiactap.mapper.common.utils.InputEventAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SequenceTriggerActionPerformer(
    private val coroutineScope: CoroutineScope,
    private val useCase: PerformActionsUseCase,
    private val actionList: List<Action>,
) {
    private var job: Job? = null

    fun onTriggered(device: PerformActionTriggerDevice, metaState: Int) {
        /*
        this job shouldn't be cancelled when the trigger is released. all actions should be performed
        once before repeating (if configured).
         */
        job?.cancel()
        job = coroutineScope.launch {
            for (action in actionList) {
                performAction(action, metaState, device)

                delay(action.delayBeforeNextAction?.toLong() ?: 0L)
            }
        }
    }

    fun reset() {
        job?.cancel()
        job = null
    }

    private suspend fun performAction(
        action: Action,
        metaState: Int,
        device: PerformActionTriggerDevice,
    ) {
        repeat(action.multiplier ?: 1) {
            useCase.perform(action.data, InputEventAction.DOWN_UP, metaState, device)
        }
    }
}
