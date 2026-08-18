package com.zodiactap.mapper.base.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zodiactap.mapper.base.system.accessibility.ControlAccessibilityServiceUseCase
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import com.zodiactap.mapper.common.utils.AccessibilityServiceError
import com.zodiactap.mapper.common.utils.firstBlocking
import com.zodiactap.mapper.system.accessibility.AccessibilityServiceState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

sealed class AccessibilityServiceDialog {
    data class EnableService(val isRestrictedSetting: Boolean) : AccessibilityServiceDialog()
    data object RestartService : AccessibilityServiceDialog()
    data object CantFindSettings : AccessibilityServiceDialog()
}

@Singleton
class SetupAccessibilityServiceDelegateImpl @Inject constructor(
    private val useCase: ControlAccessibilityServiceUseCase,
    resourceProvider: ResourceProvider,
) : SetupAccessibilityServiceDelegate,
    ResourceProvider by resourceProvider {

    var dialogState: AccessibilityServiceDialog? by mutableStateOf(null)

    override val accessibilityServiceState: Flow<AccessibilityServiceState> = useCase.serviceState

    override fun showFixAccessibilityServiceDialog(error: AccessibilityServiceError) {
        dialogState = when (error) {
            AccessibilityServiceError.Disabled -> {
                val isRestricted = useCase.isRestrictedSetting()
                AccessibilityServiceDialog.EnableService(isRestricted)
            }

            AccessibilityServiceError.Crashed -> AccessibilityServiceDialog.RestartService
        }
    }

    override fun showEnableAccessibilityServiceDialog() {
        val state = accessibilityServiceState.firstBlocking()

        if (state == AccessibilityServiceState.DISABLED) {
            val isRestricted = useCase.isRestrictedSetting()
            dialogState = AccessibilityServiceDialog.EnableService(isRestricted)
        } else if (state == AccessibilityServiceState.CRASHED) {
            dialogState = AccessibilityServiceDialog.RestartService
        }
    }

    fun onStartServiceClick() {
        if (!useCase.startService()) {
            dialogState = AccessibilityServiceDialog.CantFindSettings
        } else {
            dialogState = null
        }
    }

    fun onRestartServiceClick() {
        if (!useCase.restartService()) {
            dialogState = AccessibilityServiceDialog.CantFindSettings
        } else {
            dialogState = null
        }
    }

    fun onCancelClick() {
        dialogState = null
    }

    fun onIgnoreCrashedClick() {
        useCase.acknowledgeCrashed()
        dialogState = null
    }

    override fun showCantFindAccessibilitySettingsDialog() {
        dialogState = AccessibilityServiceDialog.CantFindSettings
    }
}

interface SetupAccessibilityServiceDelegate {
    val accessibilityServiceState: Flow<AccessibilityServiceState>

    fun showFixAccessibilityServiceDialog(error: AccessibilityServiceError)
    fun showEnableAccessibilityServiceDialog()
    fun showCantFindAccessibilitySettingsDialog()
}
