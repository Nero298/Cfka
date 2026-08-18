package com.zodiactap.mapper.base.expertmode

import dagger.hilt.android.scopes.ViewModelScoped
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.ui.ResourceProvider
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ViewModelScoped
class ExpertModeSetupDelegateImpl @Inject constructor(
    @Named("viewmodel")
    viewModelScope: CoroutineScope,
    useCase: SystemBridgeSetupUseCase,
    resourceProvider: ResourceProvider,
    private val navigationProvider: NavigationProvider,
) : SystemBridgeSetupDelegateImpl(
    viewModelScope,
    useCase,
    resourceProvider,
) {
    override fun onFinishClick() {
        viewModelScope.launch {
            navigationProvider.popBackStack()
        }
    }
}
