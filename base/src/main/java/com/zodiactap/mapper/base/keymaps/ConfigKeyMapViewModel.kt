package com.zodiactap.mapper.base.keymaps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.zodiactap.mapper.base.onboarding.OnboardingTapTarget
import com.zodiactap.mapper.base.onboarding.OnboardingUseCase
import com.zodiactap.mapper.base.trigger.ConfigTriggerUseCase
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.ui.DialogProvider
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.common.utils.dataOrNull
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ConfigKeyMapViewModel @Inject constructor(
    private val configKeyMapState: ConfigKeyMapState,
    private val configTrigger: ConfigTriggerUseCase,
    private val onboarding: OnboardingUseCase,
    navigationProvider: NavigationProvider,
    dialogProvider: DialogProvider,
) : ViewModel(),
    NavigationProvider by navigationProvider,
    DialogProvider by dialogProvider {

    val isKeyMapEdited: Boolean
        get() = configKeyMapState.isEdited

    val isEnabled: StateFlow<Boolean> = configTrigger.keyMap
        .map { state -> state.dataOrNull()?.isEnabled ?: true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val showActionsTapTarget: StateFlow<Boolean> =
        combine(
            onboarding.showTapTarget(OnboardingTapTarget.CHOOSE_ACTION),
            configKeyMapState.keyMap.filterIsInstance<State.Data<KeyMap>>(),
        ) { showTapTarget, keyMapState ->
            // Show the choose action tap target if they have recorded a key and
            // have no actions.
            showTapTarget &&
                keyMapState.data.trigger.keys.isNotEmpty() &&
                keyMapState.data.actionList.isEmpty()
        }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onDoneClick() {
        configKeyMapState.save()

        viewModelScope.launch {
            popBackStack()
        }
    }

    fun loadNewKeyMap(groupUid: String?) {
        configKeyMapState.loadNewKeyMap(groupUid)
    }

    fun loadKeyMap(uid: String) {
        viewModelScope.launch {
            configKeyMapState.loadKeyMap(uid)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            popBackStack()
        }
    }

    fun onEnabledChanged(enabled: Boolean) {
        configTrigger.setEnabled(enabled)
    }
}
