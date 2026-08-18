package com.zodiactap.mapper.trigger

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.zodiactap.mapper.base.keymaps.DisplayKeyMapUseCase
import com.zodiactap.mapper.base.keymaps.FingerprintGesturesSupportedUseCase
import com.zodiactap.mapper.base.onboarding.OnboardingTipDelegate
import com.zodiactap.mapper.base.onboarding.OnboardingUseCase
import com.zodiactap.mapper.base.onboarding.SetupAccessibilityServiceDelegate
import com.zodiactap.mapper.base.shortcuts.CreateKeyMapShortcutUseCase
import com.zodiactap.mapper.base.trigger.BaseConfigTriggerViewModel
import com.zodiactap.mapper.base.trigger.ConfigTriggerUseCase
import com.zodiactap.mapper.base.trigger.RecordTriggerController
import com.zodiactap.mapper.base.trigger.TriggerSetupDelegate
import com.zodiactap.mapper.base.trigger.TriggerSetupShortcut
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.ui.DialogProvider
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManager
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ConfigTriggerViewModel @Inject constructor(
    private val onboarding: OnboardingUseCase,
    private val config: ConfigTriggerUseCase,
    private val recordTrigger: RecordTriggerController,
    private val createKeyMapShortcut: CreateKeyMapShortcutUseCase,
    private val displayKeyMap: DisplayKeyMapUseCase,
    private val fingerprintGesturesSupported: FingerprintGesturesSupportedUseCase,
    private val systemBridgeConnectionManager: SystemBridgeConnectionManager,
    setupAccessibilityServiceDelegate: SetupAccessibilityServiceDelegate,
    onboardingTipDelegate: OnboardingTipDelegate,
    triggerSetupDelegate: TriggerSetupDelegate,
    resourceProvider: ResourceProvider,
    navigationProvider: NavigationProvider,
    dialogProvider: DialogProvider,
) : BaseConfigTriggerViewModel(
    onboarding = onboarding,
    config = config,
    recordTrigger = recordTrigger,
    createKeyMapShortcut = createKeyMapShortcut,
    displayKeyMap = displayKeyMap,
    fingerprintGesturesSupported = fingerprintGesturesSupported,
    setupAccessibilityServiceDelegate = setupAccessibilityServiceDelegate,
    systemBridgeConnectionManager = systemBridgeConnectionManager,
    onboardingTipDelegate = onboardingTipDelegate,
    triggerSetupDelegate = triggerSetupDelegate,
    resourceProvider = resourceProvider,
    navigationProvider = navigationProvider,
    dialogProvider = dialogProvider,
) {
    override fun onEditFloatingButtonClick() {}

    override fun onEditFloatingLayoutClick() {}

    override fun showTriggerSetup(shortcut: TriggerSetupShortcut, forceExpertMode: Boolean) {
        when (shortcut) {
            TriggerSetupShortcut.ASSISTANT,
            TriggerSetupShortcut.FLOATING_BUTTON_CUSTOM,
            TriggerSetupShortcut.FLOATING_BUTTON_LOCK_SCREEN,
                -> viewModelScope.launch {
                    navigateToAdvancedTriggers("purchase_assistant_trigger")
                }

            else -> super.showTriggerSetup(shortcut, forceExpertMode)
        }
    }
}
