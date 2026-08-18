package com.zodiactap.mapper.system.accessibility

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import com.zodiactap.mapper.base.actions.PerformActionsUseCaseImpl
import com.zodiactap.mapper.base.constraints.DetectConstraintsUseCaseImpl
import com.zodiactap.mapper.base.detection.DetectKeyMapsUseCaseImpl
import com.zodiactap.mapper.base.expertmode.SystemBridgeSetupAssistantController
import com.zodiactap.mapper.base.input.InputEventHub
import com.zodiactap.mapper.base.keymaps.FingerprintGesturesSupportedUseCase
import com.zodiactap.mapper.base.keymaps.PauseKeyMapsUseCase
import com.zodiactap.mapper.base.system.accessibility.AccessibilityNodeRecorder
import com.zodiactap.mapper.base.system.accessibility.BaseAccessibilityServiceController
import com.zodiactap.mapper.base.system.inputmethod.AutoSwitchImeController
import com.zodiactap.mapper.base.trigger.RecordTriggerController
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.system.inputmethod.KeyEventRelayServiceWrapper

class AccessibilityServiceController @AssistedInject constructor(
    @Assisted
    private val service: MyAccessibilityService,
    accessibilityNodeRecorderFactory: AccessibilityNodeRecorder.Factory,
    performActionsUseCaseFactory: PerformActionsUseCaseImpl.Factory,
    detectKeyMapsUseCaseFactory: DetectKeyMapsUseCaseImpl.Factory,
    detectConstraintsUseCaseFactory: DetectConstraintsUseCaseImpl.Factory,
    fingerprintGesturesSupported: FingerprintGesturesSupportedUseCase,
    pauseKeyMapsUseCase: PauseKeyMapsUseCase,
    settingsRepository: PreferenceRepository,
    keyEventRelayServiceWrapper: KeyEventRelayServiceWrapper,
    inputEventHub: InputEventHub,
    recordTriggerController: RecordTriggerController,
    setupAssistantControllerFactory: SystemBridgeSetupAssistantController.Factory,
    autoSwitchImeControllerFactory: AutoSwitchImeController.Factory,
) : BaseAccessibilityServiceController(
    service = service,
    accessibilityNodeRecorderFactory = accessibilityNodeRecorderFactory,
    performActionsUseCaseFactory = performActionsUseCaseFactory,
    detectKeyMapsUseCaseFactory = detectKeyMapsUseCaseFactory,
    detectConstraintsUseCaseFactory = detectConstraintsUseCaseFactory,
    fingerprintGesturesSupported = fingerprintGesturesSupported,
    pauseKeyMapsUseCase = pauseKeyMapsUseCase,
    settingsRepository = settingsRepository,
    keyEventRelayServiceWrapper = keyEventRelayServiceWrapper,
    inputEventHub = inputEventHub,
    recordTriggerController = recordTriggerController,
    setupAssistantControllerFactory = setupAssistantControllerFactory,
    autoSwitchImeControllerFactory = autoSwitchImeControllerFactory,
) {
    @AssistedFactory
    interface Factory {
        fun create(service: MyAccessibilityService): AccessibilityServiceController
    }
}
