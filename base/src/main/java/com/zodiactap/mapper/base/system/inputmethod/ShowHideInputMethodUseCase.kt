package com.zodiactap.mapper.base.system.inputmethod

import com.zodiactap.mapper.system.accessibility.AccessibilityServiceAdapter
import com.zodiactap.mapper.system.accessibility.AccessibilityServiceEvent
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking

class ShowHideInputMethodUseCaseImpl @Inject constructor(
    private val serviceAdapter: AccessibilityServiceAdapter,
) : ShowHideInputMethodUseCase {
    override val onHiddenChange: Flow<Boolean> = serviceAdapter.eventReceiver.mapNotNull {
        when (it) {
            AccessibilityServiceEvent.OnHideKeyboardEvent -> true
            AccessibilityServiceEvent.OnShowKeyboardEvent -> false
            else -> null
        }
    }

    override fun show() {
        runBlocking { serviceAdapter.send(AccessibilityServiceEvent.ShowKeyboard) }
    }

    override fun hide() {
        runBlocking { serviceAdapter.send(AccessibilityServiceEvent.HideKeyboard) }
    }
}

interface ShowHideInputMethodUseCase {
    val onHiddenChange: Flow<Boolean>
    fun show()
    fun hide()
}
