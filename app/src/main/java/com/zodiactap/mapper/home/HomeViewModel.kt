package com.zodiactap.mapper.home

import dagger.hilt.android.lifecycle.HiltViewModel
import com.zodiactap.mapper.base.actions.keyevent.FixKeyEventActionDelegate
import com.zodiactap.mapper.base.backup.BackupRestoreMappingsUseCase
import com.zodiactap.mapper.base.home.BaseHomeViewModel
import com.zodiactap.mapper.base.home.ListKeyMapsUseCase
import com.zodiactap.mapper.base.home.ShowHomeScreenAlertsUseCase
import com.zodiactap.mapper.base.keymaps.PauseKeyMapsUseCase
import com.zodiactap.mapper.base.onboarding.OnboardingUseCase
import com.zodiactap.mapper.base.onboarding.SetupAccessibilityServiceDelegate
import com.zodiactap.mapper.base.sorting.SortKeyMapsUseCase
import com.zodiactap.mapper.base.system.inputmethod.ShowInputMethodPickerUseCase
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.ui.DialogProvider
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val listKeyMaps: ListKeyMapsUseCase,
    private val pauseKeyMaps: PauseKeyMapsUseCase,
    private val backupRestore: BackupRestoreMappingsUseCase,
    private val showAlertsUseCase: ShowHomeScreenAlertsUseCase,
    private val onboarding: OnboardingUseCase,
    resourceProvider: ResourceProvider,
    private val sortKeyMaps: SortKeyMapsUseCase,
    private val showInputMethodPickerUseCase: ShowInputMethodPickerUseCase,
    val setupAccessibilityServiceDelegate: SetupAccessibilityServiceDelegate,
    fixKeyEventActionDelegate: FixKeyEventActionDelegate,
    navigationProvider: NavigationProvider,
    dialogProvider: DialogProvider,
) : BaseHomeViewModel(
    listKeyMaps,
    pauseKeyMaps,
    backupRestore,
    showAlertsUseCase,
    onboarding,
    resourceProvider,
    sortKeyMaps,
    showInputMethodPickerUseCase,
    setupAccessibilityServiceDelegate,
    fixKeyEventActionDelegate,
    navigationProvider,
    dialogProvider,
)
