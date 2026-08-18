package com.zodiactap.mapper.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.zodiactap.mapper.base.onboarding.SetupAccessibilityServiceDelegate
import com.zodiactap.mapper.base.utils.navigation.NavDestination
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.navigation.navigate
import com.zodiactap.mapper.base.utils.ui.DialogProvider
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val setupAccessibilityServiceDelegate: SetupAccessibilityServiceDelegate,
    resourceProvider: ResourceProvider,
    dialogProvider: DialogProvider,
    navigationProvider: NavigationProvider,
) : ViewModel(),
    ResourceProvider by resourceProvider,
    DialogProvider by dialogProvider,
    NavigationProvider by navigationProvider {

    var previousNightMode: Int? = null

    fun onCantFindAccessibilitySettings() {
        setupAccessibilityServiceDelegate.showCantFindAccessibilitySettingsDialog()
    }

    fun launchExpertModeSetup() {
        viewModelScope.launch {
            navigate("expert_mode_setup", NavDestination.ExpertModeSetup)
        }
    }
}
