package com.zodiactap.mapper.base

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import com.zodiactap.mapper.base.actions.ConfigActionsUseCase
import com.zodiactap.mapper.base.actions.ConfigActionsUseCaseImpl
import com.zodiactap.mapper.base.actions.CreateActionUseCase
import com.zodiactap.mapper.base.actions.CreateActionUseCaseImpl
import com.zodiactap.mapper.base.actions.DisplayActionUseCase
import com.zodiactap.mapper.base.actions.TestActionUseCase
import com.zodiactap.mapper.base.actions.TestActionUseCaseImpl
import com.zodiactap.mapper.base.actions.keyevent.ConfigKeyEventUseCase
import com.zodiactap.mapper.base.actions.keyevent.ConfigKeyEventUseCaseImpl
import com.zodiactap.mapper.base.actions.keyevent.FixKeyEventActionDelegate
import com.zodiactap.mapper.base.actions.keyevent.FixKeyEventActionDelegateImpl
import com.zodiactap.mapper.base.actions.sound.ChooseSoundFileUseCase
import com.zodiactap.mapper.base.actions.sound.ChooseSoundFileUseCaseImpl
import com.zodiactap.mapper.base.backup.BackupRestoreMappingsUseCase
import com.zodiactap.mapper.base.backup.BackupRestoreMappingsUseCaseImpl
import com.zodiactap.mapper.base.constraints.ConfigConstraintsUseCase
import com.zodiactap.mapper.base.constraints.ConfigConstraintsUseCaseImpl
import com.zodiactap.mapper.base.constraints.CreateConstraintUseCase
import com.zodiactap.mapper.base.constraints.CreateConstraintUseCaseImpl
import com.zodiactap.mapper.base.constraints.DisplayConstraintUseCase
import com.zodiactap.mapper.base.expertmode.ExpertModeSetupDelegateImpl
import com.zodiactap.mapper.base.expertmode.SystemBridgeSetupDelegate
import com.zodiactap.mapper.base.expertmode.SystemBridgeSetupUseCase
import com.zodiactap.mapper.base.expertmode.SystemBridgeSetupUseCaseImpl
import com.zodiactap.mapper.base.home.ListKeyMapsUseCase
import com.zodiactap.mapper.base.home.ListKeyMapsUseCaseImpl
import com.zodiactap.mapper.base.home.ShowHomeScreenAlertsUseCase
import com.zodiactap.mapper.base.home.ShowHomeScreenAlertsUseCaseImpl
import com.zodiactap.mapper.base.keymaps.DisplayKeyMapUseCase
import com.zodiactap.mapper.base.keymaps.DisplayKeyMapUseCaseImpl
import com.zodiactap.mapper.base.logging.DisplayLogUseCase
import com.zodiactap.mapper.base.logging.DisplayLogUseCaseImpl
import com.zodiactap.mapper.base.logging.ShareLogcatUseCase
import com.zodiactap.mapper.base.logging.ShareLogcatUseCaseImpl
import com.zodiactap.mapper.base.onboarding.OnboardingTipDelegate
import com.zodiactap.mapper.base.onboarding.OnboardingTipDelegateImpl
import com.zodiactap.mapper.base.settings.ConfigSettingsUseCase
import com.zodiactap.mapper.base.settings.ConfigSettingsUseCaseImpl
import com.zodiactap.mapper.base.shortcuts.CreateKeyMapShortcutUseCase
import com.zodiactap.mapper.base.shortcuts.CreateKeyMapShortcutUseCaseImpl
import com.zodiactap.mapper.base.sorting.SortKeyMapsUseCase
import com.zodiactap.mapper.base.sorting.SortKeyMapsUseCaseImpl
import com.zodiactap.mapper.base.system.apps.DisplayAppShortcutsUseCase
import com.zodiactap.mapper.base.system.apps.DisplayAppShortcutsUseCaseImpl
import com.zodiactap.mapper.base.system.apps.DisplayAppsUseCase
import com.zodiactap.mapper.base.system.apps.DisplayAppsUseCaseImpl
import com.zodiactap.mapper.base.system.bluetooth.ChooseBluetoothDeviceUseCase
import com.zodiactap.mapper.base.system.bluetooth.ChooseBluetoothDeviceUseCaseImpl
import com.zodiactap.mapper.base.trigger.ConfigTriggerUseCase
import com.zodiactap.mapper.base.trigger.ConfigTriggerUseCaseImpl
import com.zodiactap.mapper.base.trigger.SetupInputMethodUseCase
import com.zodiactap.mapper.base.trigger.SetupInputMethodUseCaseImpl
import com.zodiactap.mapper.base.trigger.TriggerSetupDelegate
import com.zodiactap.mapper.base.trigger.TriggerSetupDelegateImpl

@Module
@InstallIn(ViewModelComponent::class)
abstract class BaseViewModelHiltModule {
    @Binds
    @ViewModelScoped
    abstract fun bindDisplayKeyMapUseCase(impl: DisplayKeyMapUseCaseImpl): DisplayKeyMapUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindDisplayActionUseCase(impl: DisplayKeyMapUseCaseImpl): DisplayActionUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindDisplayConstraintUseCase(
        impl: DisplayKeyMapUseCaseImpl,
    ): DisplayConstraintUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindListKeyMapsUseCase(impl: ListKeyMapsUseCaseImpl): ListKeyMapsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindBackupRestoreMappingsUseCase(
        impl: BackupRestoreMappingsUseCaseImpl,
    ): BackupRestoreMappingsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindShowHomeScreenAlertsUseCase(
        impl: ShowHomeScreenAlertsUseCaseImpl,
    ): ShowHomeScreenAlertsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindSortKeyMapsUseCase(impl: SortKeyMapsUseCaseImpl): SortKeyMapsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindDisplayLogUseCase(impl: DisplayLogUseCaseImpl): DisplayLogUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindConfigSettingsUseCase(impl: ConfigSettingsUseCaseImpl): ConfigSettingsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindChooseBluetoothDeviceUseCase(
        impl: ChooseBluetoothDeviceUseCaseImpl,
    ): ChooseBluetoothDeviceUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindChooseSoundFileUseCase(
        impl: ChooseSoundFileUseCaseImpl,
    ): ChooseSoundFileUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindConfigKeyEventUseCase(impl: ConfigKeyEventUseCaseImpl): ConfigKeyEventUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindDisplayAppShortcutsUseCase(
        impl: DisplayAppShortcutsUseCaseImpl,
    ): DisplayAppShortcutsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindDisplayAppsUseCase(impl: DisplayAppsUseCaseImpl): DisplayAppsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindTestActionUseCase(impl: TestActionUseCaseImpl): TestActionUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindCreateKeyMapShortcutUseCase(
        impl: CreateKeyMapShortcutUseCaseImpl,
    ): CreateKeyMapShortcutUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindCreateActionUseCase(impl: CreateActionUseCaseImpl): CreateActionUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindCreateConstraintUseCase(
        impl: CreateConstraintUseCaseImpl,
    ): CreateConstraintUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindExpertModeSetupUseCase(
        impl: SystemBridgeSetupUseCaseImpl,
    ): SystemBridgeSetupUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindConfigConstraintsUseCase(
        impl: ConfigConstraintsUseCaseImpl,
    ): ConfigConstraintsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindConfigActionsUseCase(impl: ConfigActionsUseCaseImpl): ConfigActionsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindConfigTriggerUseCase(impl: ConfigTriggerUseCaseImpl): ConfigTriggerUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindTriggerSetupDelegate(impl: TriggerSetupDelegateImpl): TriggerSetupDelegate

    @Binds
    @ViewModelScoped
    abstract fun bindSetupInputMethodUseCase(
        impl: SetupInputMethodUseCaseImpl,
    ): SetupInputMethodUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindShareLogcatUseCase(impl: ShareLogcatUseCaseImpl): ShareLogcatUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindOnboardingTipDelegate(impl: OnboardingTipDelegateImpl): OnboardingTipDelegate

    @Binds
    @ViewModelScoped
    abstract fun bindFixKeyEventActionDelegate(
        impl: FixKeyEventActionDelegateImpl,
    ): FixKeyEventActionDelegate

    @Binds
    @ViewModelScoped
    abstract fun bindExpertModeSetupDelegate(
        impl: ExpertModeSetupDelegateImpl,
    ): SystemBridgeSetupDelegate
}
