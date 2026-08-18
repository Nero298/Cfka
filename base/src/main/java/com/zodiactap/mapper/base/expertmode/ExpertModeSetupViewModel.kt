package com.zodiactap.mapper.base.expertmode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ExpertModeSetupViewModel @Inject constructor(
    delegate: SystemBridgeSetupDelegate,
    navigationProvider: NavigationProvider,
    resourceProvider: ResourceProvider,
) : ViewModel(),
    SystemBridgeSetupDelegate by delegate,
    NavigationProvider by navigationProvider,
    ResourceProvider by resourceProvider {

    fun onBackClick() {
        viewModelScope.launch {
            popBackStack()
        }
    }
}
