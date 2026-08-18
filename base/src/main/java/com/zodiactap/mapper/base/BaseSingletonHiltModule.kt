package com.zodiactap.mapper.base

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.zodiactap.mapper.base.actions.GetActionErrorUseCase
import com.zodiactap.mapper.base.actions.GetActionErrorUseCaseImpl
import com.zodiactap.mapper.base.actions.sound.SoundsManager
import com.zodiactap.mapper.base.actions.sound.SoundsManagerImpl
import com.zodiactap.mapper.base.actions.uielement.InteractUiElementController
import com.zodiactap.mapper.base.actions.uielement.InteractUiElementUseCase
import com.zodiactap.mapper.base.backup.BackupManager
import com.zodiactap.mapper.base.backup.BackupManagerImpl
import com.zodiactap.mapper.base.constraints.GetConstraintErrorUseCase
import com.zodiactap.mapper.base.constraints.GetConstraintErrorUseCaseImpl
import com.zodiactap.mapper.base.debug.GetEventRecorder
import com.zodiactap.mapper.base.debug.GetEventRecorderImpl
import com.zodiactap.mapper.base.input.InputEventHub
import com.zodiactap.mapper.base.input.InputEventHubImpl
import com.zodiactap.mapper.base.keymaps.ConfigKeyMapState
import com.zodiactap.mapper.base.keymaps.ConfigKeyMapStateImpl
import com.zodiactap.mapper.base.keymaps.EnableKeyMapsUseCase
import com.zodiactap.mapper.base.keymaps.EnableKeyMapsUseCaseImpl
import com.zodiactap.mapper.base.keymaps.FingerprintGesturesSupportedUseCase
import com.zodiactap.mapper.base.keymaps.FingerprintGesturesSupportedUseCaseImpl
import com.zodiactap.mapper.base.keymaps.GetDefaultKeyMapOptionsUseCase
import com.zodiactap.mapper.base.keymaps.GetDefaultKeyMapOptionsUseCaseImpl
import com.zodiactap.mapper.base.keymaps.PauseKeyMapsUseCase
import com.zodiactap.mapper.base.keymaps.PauseKeyMapsUseCaseImpl
import com.zodiactap.mapper.base.onboarding.OnboardingUseCase
import com.zodiactap.mapper.base.onboarding.OnboardingUseCaseImpl
import com.zodiactap.mapper.base.onboarding.SetupAccessibilityServiceDelegate
import com.zodiactap.mapper.base.onboarding.SetupAccessibilityServiceDelegateImpl
import com.zodiactap.mapper.base.system.accessibility.AccessibilityServiceAdapterImpl
import com.zodiactap.mapper.base.system.accessibility.ControlAccessibilityServiceUseCase
import com.zodiactap.mapper.base.system.accessibility.ControlAccessibilityServiceUseCaseImpl
import com.zodiactap.mapper.base.system.inputmethod.ImeInputEventInjector
import com.zodiactap.mapper.base.system.inputmethod.ImeInputEventInjectorImpl
import com.zodiactap.mapper.base.system.inputmethod.ShowHideInputMethodUseCase
import com.zodiactap.mapper.base.system.inputmethod.ShowHideInputMethodUseCaseImpl
import com.zodiactap.mapper.base.system.inputmethod.ShowInputMethodPickerUseCase
import com.zodiactap.mapper.base.system.inputmethod.ShowInputMethodPickerUseCaseImpl
import com.zodiactap.mapper.base.system.inputmethod.SwitchImeAsyncImpl
import com.zodiactap.mapper.base.system.inputmethod.SwitchImeInterface
import com.zodiactap.mapper.base.system.inputmethod.ToggleCompatibleImeUseCase
import com.zodiactap.mapper.base.system.inputmethod.ToggleCompatibleImeUseCaseImpl
import com.zodiactap.mapper.base.system.notifications.AndroidNotificationAdapter
import com.zodiactap.mapper.base.system.notifications.ManageNotificationsUseCase
import com.zodiactap.mapper.base.system.notifications.ManageNotificationsUseCaseImpl
import com.zodiactap.mapper.base.trigger.RecordTriggerController
import com.zodiactap.mapper.base.trigger.RecordTriggerControllerImpl
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.navigation.NavigationProviderImpl
import com.zodiactap.mapper.base.utils.ui.DialogProvider
import com.zodiactap.mapper.base.utils.ui.DialogProviderImpl
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import com.zodiactap.mapper.base.utils.ui.ResourceProviderImpl
import com.zodiactap.mapper.common.utils.Clock
import com.zodiactap.mapper.common.utils.ClockImpl
import com.zodiactap.mapper.common.utils.DefaultUuidGenerator
import com.zodiactap.mapper.common.utils.UuidGenerator
import com.zodiactap.mapper.system.accessibility.AccessibilityServiceAdapter
import com.zodiactap.mapper.system.inputmethod.KeyEventRelayServiceWrapper
import com.zodiactap.mapper.system.inputmethod.KeyEventRelayServiceWrapperImpl
import com.zodiactap.mapper.system.notifications.NotificationAdapter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BaseSingletonHiltModule {
    @Singleton
    @Binds
    abstract fun provideNotificationAdapter(impl: AndroidNotificationAdapter): NotificationAdapter

    @Singleton
    @Binds
    abstract fun provideAccessibilityAdapter(
        impl: AccessibilityServiceAdapterImpl,
    ): AccessibilityServiceAdapter

    @Singleton
    @Binds
    abstract fun provideResourceProvider(impl: ResourceProviderImpl): ResourceProvider

    @Singleton
    @Binds
    abstract fun provideOnboardingUseCase(impl: OnboardingUseCaseImpl): OnboardingUseCase

    @Binds
    @Singleton
    abstract fun bindPauseKeyMapsUseCase(impl: PauseKeyMapsUseCaseImpl): PauseKeyMapsUseCase

    @Binds
    @Singleton
    abstract fun bindShowInputMethodPickerUseCase(
        impl: ShowInputMethodPickerUseCaseImpl,
    ): ShowInputMethodPickerUseCase

    @Binds
    @Singleton
    abstract fun bindControlAccessibilityServiceUseCase(
        impl: ControlAccessibilityServiceUseCaseImpl,
    ): ControlAccessibilityServiceUseCase

    @Binds
    @Singleton
    abstract fun bindToggleCompatibleImeUseCase(
        impl: ToggleCompatibleImeUseCaseImpl,
    ): ToggleCompatibleImeUseCase

    @Binds
    @Singleton
    abstract fun bindInteractUiElementUseCase(
        impl: InteractUiElementController,
    ): InteractUiElementUseCase

    @Binds
    @Singleton
    abstract fun bindShowHideInputMethodUseCase(
        impl: ShowHideInputMethodUseCaseImpl,
    ): ShowHideInputMethodUseCase

    @Binds
    @Singleton
    abstract fun bindBackupManager(impl: BackupManagerImpl): BackupManager

    @Binds
    @Singleton
    abstract fun bindSoundsManager(impl: SoundsManagerImpl): SoundsManager

    @Binds
    @Singleton
    abstract fun bindRecordTriggerUseCase(
        impl: RecordTriggerControllerImpl,
    ): RecordTriggerController

    @Binds
    @Singleton
    abstract fun bindGetEventOutputUseCase(impl: GetEventRecorderImpl): GetEventRecorder

    @Binds
    @Singleton
    abstract fun bindFingerprintGesturesSupportedUseCase(
        impl: FingerprintGesturesSupportedUseCaseImpl,
    ): FingerprintGesturesSupportedUseCase

    @Binds
    @Singleton
    abstract fun bindGetActionErrorUseCase(impl: GetActionErrorUseCaseImpl): GetActionErrorUseCase

    @Binds
    @Singleton
    abstract fun bindGetConstraintErrorUseCase(
        impl: GetConstraintErrorUseCaseImpl,
    ): GetConstraintErrorUseCase

    @Binds
    @Singleton
    abstract fun bindManageNotificationsUseCase(
        impl: ManageNotificationsUseCaseImpl,
    ): ManageNotificationsUseCase

    @Binds
    @Singleton
    abstract fun bindUuidGenerator(impl: DefaultUuidGenerator): UuidGenerator

    @Binds
    @Singleton
    abstract fun bindNavigationProvider(impl: NavigationProviderImpl): NavigationProvider

    @Binds
    @Singleton
    abstract fun bindDialogProvider(impl: DialogProviderImpl): DialogProvider

    @Binds
    @Singleton
    abstract fun bindInputEventHub(impl: InputEventHubImpl): InputEventHub

    @Binds
    @Singleton
    abstract fun keyEventRelayServiceWrapper(
        impl: KeyEventRelayServiceWrapperImpl,
    ): KeyEventRelayServiceWrapper

    @Binds
    @Singleton
    abstract fun imeInputEvenInjector(impl: ImeInputEventInjectorImpl): ImeInputEventInjector

    @Binds
    @Singleton
    abstract fun bindConfigKeyMapState(impl: ConfigKeyMapStateImpl): ConfigKeyMapState

    @Binds
    @Singleton
    abstract fun bindGetDefaultKeyMapOptionsUseCas(
        impl: GetDefaultKeyMapOptionsUseCaseImpl,
    ): GetDefaultKeyMapOptionsUseCase

    @Binds
    @Singleton
    abstract fun bindSwitchImeInterface(impl: SwitchImeAsyncImpl): SwitchImeInterface

    @Binds
    @Singleton
    abstract fun bindEnableKeyMapsUseCase(impl: EnableKeyMapsUseCaseImpl): EnableKeyMapsUseCase

    @Binds
    @Singleton
    abstract fun bindSetupAccessibilityServiceDelegate(
        impl: SetupAccessibilityServiceDelegateImpl,
    ): SetupAccessibilityServiceDelegate

    @Binds
    @Singleton
    abstract fun bindClock(impl: ClockImpl): Clock
}
