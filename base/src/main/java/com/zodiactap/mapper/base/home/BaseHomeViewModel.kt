package com.zodiactap.mapper.base.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.base.actions.keyevent.FixKeyEventActionDelegate
import com.zodiactap.mapper.base.backup.BackupRestoreMappingsUseCase
import com.zodiactap.mapper.base.keymaps.PauseKeyMapsUseCase
import com.zodiactap.mapper.base.onboarding.OnboardingUseCase
import com.zodiactap.mapper.base.onboarding.SetupAccessibilityServiceDelegate
import com.zodiactap.mapper.base.sorting.SortKeyMapsUseCase
import com.zodiactap.mapper.base.system.inputmethod.ShowInputMethodPickerUseCase
import com.zodiactap.mapper.base.utils.navigation.NavDestination
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.navigation.navigate
import com.zodiactap.mapper.base.utils.ui.DialogModel
import com.zodiactap.mapper.base.utils.ui.DialogProvider
import com.zodiactap.mapper.base.utils.ui.DialogResponse
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import com.zodiactap.mapper.base.utils.ui.showDialog
import kotlinx.coroutines.launch

abstract class BaseHomeViewModel(
    private val listKeyMaps: ListKeyMapsUseCase,
    private val pauseKeyMaps: PauseKeyMapsUseCase,
    private val backupRestore: BackupRestoreMappingsUseCase,
    private val showAlertsUseCase: ShowHomeScreenAlertsUseCase,
    private val onboarding: OnboardingUseCase,
    resourceProvider: ResourceProvider,
    private val sortKeyMaps: SortKeyMapsUseCase,
    private val showInputMethodPickerUseCase: ShowInputMethodPickerUseCase,
    setupAccessibilityServiceDelegate: SetupAccessibilityServiceDelegate,
    fixKeyEventActionDelegate: FixKeyEventActionDelegate,
    navigationProvider: NavigationProvider,
    dialogProvider: DialogProvider,
) : ViewModel(),
    ResourceProvider by resourceProvider,
    DialogProvider by dialogProvider,
    NavigationProvider by navigationProvider {

    val keyMapListViewModel by lazy {
        KeyMapListViewModel(
            viewModelScope,
            listKeyMaps,
            resourceProvider,
            sortKeyMaps,
            showAlertsUseCase,
            pauseKeyMaps,
            backupRestore,
            showInputMethodPickerUseCase,
            onboarding,
            setupAccessibilityServiceDelegate,
            fixKeyEventActionDelegate,
            navigationProvider,
            dialogProvider,
        )
    }

    init {
        viewModelScope.launch {
            onboarding.showWhatsNew.collect { showWhatsNew ->
                if (showWhatsNew) {
                    showWhatsNewDialog()
                }
            }
        }
    }

    fun launchSettings() {
        viewModelScope.launch {
            navigate("settings", NavDestination.Settings)
        }
    }

    fun launchAbout() {
        viewModelScope.launch {
            navigate("about", NavDestination.About)
        }
    }

    private suspend fun showWhatsNewDialog() {
        val dialog = DialogModel.Alert(
            title = getString(R.string.whats_new),
            message = onboarding.getWhatsNewText(),
            positiveButtonText = getString(R.string.pos_ok),
            neutralButtonText = getString(R.string.neutral_changelog),
        )

        // don't return if they dismiss the dialog because this is common behaviour.
        val response = showDialog("whats-new", dialog)

        if (response == DialogResponse.NEUTRAL) {
            showDialog("url_changelog", DialogModel.OpenUrl(getString(R.string.url_changelog)))
        }

        onboarding.showedWhatsNew()
    }
}

enum class SelectedKeyMapsEnabled {
    ALL,
    NONE,
    MIXED,
}

data class HomeWarningListItem(val id: String, val text: String)
